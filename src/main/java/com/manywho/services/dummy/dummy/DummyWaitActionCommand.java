package com.manywho.services.dummy.dummy;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.api.InvokeType;
import com.manywho.sdk.api.run.EngineValue;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.api.run.elements.config.ServiceResponse;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.client.run.RunClient;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.sdk.services.identity.AuthorizationEncoder;
import com.manywho.sdk.services.values.ValueBuilder;
import com.manywho.services.dummy.ApplicationConfiguration;
import com.manywho.services.dummy.dummy.DummyWaitAction.Input;
import com.manywho.services.dummy.dummy.DummyWaitAction.Output;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DummyWaitActionCommand implements ActionCommand<ApplicationConfiguration, DummyWaitAction, Input, Output> {
    private final static Logger LOGGER = LoggerFactory.getLogger(DummyWaitActionCommand.class);

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    private final RunClient runClient;
    private final AuthenticatedWho user;
    private final AuthorizationEncoder authorizationEncoder;
    private final ValueBuilder valueBuilder;

    @Inject
    public DummyWaitActionCommand(RunClient runClient, AuthenticatedWho user, AuthorizationEncoder authorizationEncoder, ValueBuilder valueBuilder) {
        this.runClient = runClient;
        this.user = user;
        this.authorizationEncoder = authorizationEncoder;
        this.valueBuilder = valueBuilder;
    }

    @Override
    public ActionResponse<Output> execute(ApplicationConfiguration configuration, ServiceRequest serviceRequest, Input input) {
        scheduledExecutorService.scheduleAtFixedRate(new WaitHandler(scheduledExecutorService, serviceRequest, input, user), 0, 1, TimeUnit.SECONDS);

        return new ActionResponse<>(InvokeType.Wait);
    }

    class WaitHandler implements Runnable {
        private final AtomicInteger ITERATIONS = new AtomicInteger();

        private final ScheduledExecutorService scheduledExecutorService;
        private final ServiceRequest serviceRequest;
        private final Input input;
        private final AuthenticatedWho user;

        WaitHandler(ScheduledExecutorService scheduledExecutorService, ServiceRequest serviceRequest, Input input, AuthenticatedWho user) {
            this.scheduledExecutorService = scheduledExecutorService;
            this.serviceRequest = serviceRequest;
            this.input = input;
            this.user = user;
        }

        @Override
        public void run() {
            int iteration = ITERATIONS.getAndIncrement();

            // If we've gone past our time limit, shutdown this task
            if (iteration > input.getNumberOfSeconds()) {
                LOGGER.info("Shutting down executor");

                scheduledExecutorService.shutdown();
                return;
            }

            // If we've reached our time limit, send a FORWARD to the flow with the output populated with the final message
            if (iteration == input.getNumberOfSeconds()) {
                LOGGER.info("Sending a FORWARD to the flow after {} seconds", iteration);

                EngineValue output = valueBuilder.from("Message", ContentType.String, "The message finished");

                sendCallback(new ServiceResponse(serviceRequest.getTenantId(), InvokeType.Forward, output, serviceRequest.getToken()));
                return;
            }

            if (input.sendUpdatesEverySecond() == null || input.sendUpdatesEverySecond()) {
                LOGGER.info("Sending a WAIT to the flow after {} out of {} seconds", iteration, input.getNumberOfSeconds());

                // Otherwise, send a wait message to the Engine with an output
                EngineValue output = valueBuilder.from("Message", ContentType.String, String.format("The message is on #%d", iteration));

                sendCallback(new ServiceResponse(serviceRequest.getTenantId(), InvokeType.Wait, output, serviceRequest.getToken(), String.format("This is second #%d", iteration)));
            }
        }

        void sendCallback(ServiceResponse serviceResponse) {
            String authorization = authorizationEncoder.encode(user);

            RetryPolicy<InvokeType> retryPolicy = new RetryPolicy<InvokeType>()
                    .handle(Exception.class)
                    .withDelay(Duration.ofSeconds(1))
                    .withMaxRetries(3);

            Failsafe.with(retryPolicy)
                    .onFailure(event -> LOGGER.error("Something went wrong sending the callback to {}", serviceRequest.getCallbackUri(), event.getFailure()))
                    .onSuccess(event -> LOGGER.info("Sent the callback to {} and received {}", serviceRequest.getCallbackUri(), event.getResult()))
                    .get(() -> {
                        Response<InvokeType> response;
                        try {
                            response = runClient.callback(authorization, serviceRequest.getTenantId(), serviceRequest.getCallbackUri(), serviceResponse)
                                    .execute();
                        } catch (IOException e) {
                            LOGGER.error("Something went wrong sending the callback", e);

                            throw new RuntimeException(e);
                        }

                        boolean acceptableInvokeType =
                                response.body().equals(InvokeType.Success) ||
                                response.body().equals(InvokeType.Wait);

                        if (response.isSuccessful() && !acceptableInvokeType) {
                            throw new RuntimeException("Flow did not accept the callback and sent back: " + response.body().toString());
                        }

                        if (response.isSuccessful() == false) {
                            try {
                                LOGGER.error("The callback was not successful: {}", response.errorBody().string());
                            } catch (Exception e) {
                                LOGGER.error("Unable to convert the error response to a string", e);

                                throw new RuntimeException(e);
                            }
                        }

                        return response.body();
                    });
        }
    }
}
