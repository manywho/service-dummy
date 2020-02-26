package com.manywho.services.dummy.social;

import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.manywho.sdk.api.run.elements.config.SocialServiceRequest;
import com.manywho.sdk.api.social.MentionedWho;
import com.manywho.sdk.api.social.MessageList;
import com.manywho.sdk.api.social.Who;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/social")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SocialController {

    @Path("/stream")
    @POST
    public TextNode createStream(SocialServiceRequest serviceRequest) {
        return new TextNode(UUID.randomUUID().toString());
    }

    @Path("/stream/{id}")
    @POST
    public MessageList getStreamMessages(SocialServiceRequest serviceRequest, @PathParam("id") UUID streamId) {
        return new MessageList();
    }

    @Path("/stream/{id}/follower")
    @POST
    public List<Who> getStreamFollowers(SocialServiceRequest serviceRequest, @PathParam("id") UUID streamId) {
        return new ArrayList<>();
    }

//    @Path("/stream/{id}/message")
//    @POST
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    public Message postNewMessage(@PostNewMessage @FormDataParam("serviceRequest") SocialServiceRequest serviceRequest,
//                                  @FormDataParam("file") InputStream file,
//                                  @FormDataParam("file") FormDataContentDisposition fileDetails,
//                                  @PathParam("id") UUID streamId) {
//        Message message = new Message();
//        message.setCreatedDate(DateTime.now());
//        message.setId(UUID.randomUUID().toString());
//        message.setRepliedToId(serviceRequest.getNewMessage().getRepliedTo());
//        message.setSender(createWho());
//        message.setText(serviceRequest.getNewMessage().getMessageText());
//
//        return message;
//    }

    @Path("/stream/{id}/user/me")
    @POST
    public Who getCurrentUser(SocialServiceRequest serviceRequest, @PathParam("id") UUID streamId) {
        return createWho();
    }

    @Path("/stream/{id}/user/name/{name}")
    @POST
    public List<MentionedWho> searchUsersByName(SocialServiceRequest serviceRequest,
                                                @PathParam("id") UUID streamId,
                                                @PathParam("name") String name) {
        if (Strings.isNullOrEmpty(name)) {
            return new ArrayList<>();
        }

        MentionedWho mentionedWho = new MentionedWho();
        mentionedWho.setAvatarUrl("https://assets.rbl.ms/18406606/980x.jpg");
        mentionedWho.setFullName("Boaty McBoatface");
        mentionedWho.setId("e03f4f6c-f4ce-4cd6-942c-0e6afac58d4f");
        mentionedWho.setName("Boaty");

        return Lists.newArrayList(mentionedWho);
    }

    private static Who createWho() {
        Who who = new Who();
        who.setAvatarUrl("https://assets.rbl.ms/18406606/980x.jpg");
        who.setFullName("Boaty McBoatface");
        who.setId("e03f4f6c-f4ce-4cd6-942c-0e6afac58d4f");

        return who;
    }
}
