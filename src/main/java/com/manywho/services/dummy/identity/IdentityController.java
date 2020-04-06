package com.manywho.services.dummy.identity;

import com.google.common.base.Strings;
import com.manywho.sdk.api.AuthorizationType;
import com.manywho.sdk.api.run.elements.config.Authorization;
import com.manywho.sdk.api.run.elements.config.Group;
import com.manywho.sdk.api.run.elements.config.User;
import com.manywho.sdk.api.run.elements.type.MObject;
import com.manywho.sdk.api.run.elements.type.ObjectDataRequest;
import com.manywho.sdk.api.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.api.run.elements.type.Property;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.api.security.AuthenticatedWhoResult;
import com.manywho.sdk.api.security.AuthenticationCredentials;
import com.manywho.sdk.services.controllers.AbstractIdentityController;
import com.manywho.sdk.services.types.TypeBuilder;
import com.manywho.sdk.services.types.system.$User;
import com.manywho.sdk.services.types.system.AuthorizationAttribute;
import com.manywho.sdk.services.types.system.AuthorizationGroup;
import com.manywho.sdk.services.types.system.AuthorizationUser;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class IdentityController extends AbstractIdentityController {
    private final static Integer USERS_IN_DIRECTORY = 101;
    private final static Integer GROUPS_IN_DIRECTORY = 53;

    private final Provider<AuthenticatedWho> authenticatedWhoProvider;
    private final TypeBuilder typeBuilder;

    @Context
    private HttpHeaders httpHeaders;

    @Context
    private UriInfo uriInfo;

    @Inject
    public IdentityController(Provider<AuthenticatedWho> authenticatedWhoProvider, TypeBuilder typeBuilder) {
        this.authenticatedWhoProvider = authenticatedWhoProvider;
        this.typeBuilder = typeBuilder;
    }

    @Path("/authentication")
    @POST
    @Override
    public AuthenticatedWhoResult authentication(AuthenticationCredentials authenticationCredentials) throws Exception {
        AuthenticatedWhoResult authenticatedWhoResult = new AuthenticatedWhoResult();

        authenticatedWhoResult.setDirectoryId( "Dummy Directory");
        authenticatedWhoResult.setDirectoryName( "Dummy Directory");
        authenticatedWhoResult.setEmail("user1@example.com");
        authenticatedWhoResult.setFirstName("Jon");
        authenticatedWhoResult.setIdentityProvider("Dummy Identity Provider");
        authenticatedWhoResult.setLastName("Doe");
        authenticatedWhoResult.setStatus(AuthenticatedWhoResult.AuthenticationStatus.Authenticated);
        authenticatedWhoResult.setTenantName("Tenant 1");
        authenticatedWhoResult.setToken("user1token");
        authenticatedWhoResult.setUserId( "user1");
        authenticatedWhoResult.setUsername("user1");

        return authenticatedWhoResult;
    }

    @Path("/authorization")
    @POST
    @Override
    public ObjectDataResponse authorization(ObjectDataRequest objectDataRequest) throws Exception {
        AuthenticatedWho authenticatedWho = authenticatedWhoProvider.get();

        $User userObject;
        URI host = baseUri(httpHeaders.getHeaderString("X-Forwarded-Proto"));

        String status = getUserAuthorizationStatus(objectDataRequest.getAuthorization(), authenticatedWho);

        if (status.equals("401")) {
            userObject = new $User();
            userObject.setDirectoryId("UNKNOWN");
            userObject.setDirectoryName("UNKNOWN");
            userObject.setAuthenticationType(AuthorizationType.Oauth2);
            userObject.setLoginUrl(host + "callback/fake-idp?");
            userObject.setStatus("401");
            userObject.setUserId(UUID.randomUUID().toString());
        } else {
            userObject = new $User();
            userObject.setDirectoryId("Dummy Directory");
            userObject.setDirectoryName("Dummy Directory");
            userObject.setAuthenticationType(AuthorizationType.Oauth2);
            userObject.setLoginUrl("");
            userObject.setPrimaryGroupId("7");
            userObject.setPrimaryGroupName("S Club 7");
            userObject.setRoleId("123");
            userObject.setRoleName("Party Creators");
            userObject.setStatus("200");
            userObject.setUserId(UUID.randomUUID().toString());
        }

        return new ObjectDataResponse(typeBuilder.from(userObject));
    }

    @Path("/authorization/group/attribute")
    @POST
    @Override
    public ObjectDataResponse groupAttributes(ObjectDataRequest objectDataRequest) throws Exception {
        AuthorizationAttribute attribute = new AuthorizationAttribute("users", "Users");

        return new ObjectDataResponse(typeBuilder.from(attribute));
    }

    @Path("/authorization/group")
    @POST
    @Override
    public ObjectDataResponse groups(ObjectDataRequest objectDataRequest) throws Exception {
        List<MObject> allGroups = typeBuilder.from(loadGroups(USERS_IN_DIRECTORY));
        List<MObject> groupsToReturn = new ArrayList<>();

        boolean hasMoreValues = true;

        if (objectDataRequest.getObjectData() != null && objectDataRequest.getObjectData().size() > 0) {
            for (MObject requestedGroups : objectDataRequest.getObjectData()) {
                if (requestedGroups.getDeveloperName().equals("GroupAuthorizationGroup")) {

                    String idToSearch = requestedGroups.getProperties().stream()
                            .filter(property -> property.getDeveloperName().equals("AuthenticationId"))
                            .findFirst()
                            .orElse(new Property("AuthenticationId", ""))
                            .getContentValue();

                    allGroups.stream()
                            .filter(u -> u.getExternalId().equals(idToSearch))
                            .findFirst()
                            .map(groupsToReturn::add);
                }
            }
        } else {

            hasMoreValues = searchObjectsByExternalIds(objectDataRequest, allGroups, groupsToReturn, hasMoreValues, GROUPS_IN_DIRECTORY);
        }

        return createResponse(groupsToReturn, hasMoreValues);
    }

    @Path("/authorization/user/attribute")
    @POST
    @Override
    public ObjectDataResponse userAttributes(ObjectDataRequest objectDataRequest) throws Exception {
        AuthorizationAttribute authorizationAttribute = new AuthorizationAttribute("accountId", "Account ID");

        return new ObjectDataResponse(typeBuilder.from(authorizationAttribute));
    }

    @Path("/authorization/user")
    @POST
    @Override
    public ObjectDataResponse users(ObjectDataRequest objectDataRequest) throws Exception {
        List<MObject> allUsers = typeBuilder.from(loadUsers(USERS_IN_DIRECTORY));
        List<MObject> usersToReturn = new ArrayList<>();

        boolean hasMoreValues = true;

        if (objectDataRequest.getObjectData() != null && objectDataRequest.getObjectData().size() > 0) {
            for (MObject requestedUsers : objectDataRequest.getObjectData()) {
                if (requestedUsers.getDeveloperName().equals("GroupAuthorizationUser")) {

                    String idToSearch = requestedUsers.getProperties().stream()
                            .filter(property -> property.getDeveloperName().equals("AuthenticationId"))
                            .findFirst()
                            .orElse(new Property("AuthenticationId", ""))
                            .getContentValue();

                    allUsers.stream()
                            .filter(u -> u.getExternalId().equals(idToSearch))
                            .findFirst()
                            .map(usersToReturn::add);
                }
            }
        } else {
            hasMoreValues = searchObjectsByExternalIds(objectDataRequest, allUsers, usersToReturn, hasMoreValues, USERS_IN_DIRECTORY);
        }

        return createResponse(usersToReturn, hasMoreValues);
    }

    public String getUserAuthorizationStatus(Authorization authorization, AuthenticatedWho user) {
        switch (authorization.getGlobalAuthenticationType()) {
            case Public:
                return "200";
            case AllUsers:
                if (!user.getUserId().equalsIgnoreCase("PUBLIC_USER")) {
                    return "200";
                } else {
                    return "401";
                }
            case Specified:
                if (!user.getUserId().equalsIgnoreCase("PUBLIC_USER")) {
                    String userId = user.getUserId();

                    if (authorization.hasUsers()) {
                        for (User allowedUser:authorization.getUsers()) {
                            if (allowedUser.getAttribute().equalsIgnoreCase("accountId")
                                    && Objects.equals(allowedUser.getAuthenticationId(), userId)) {

                                return "200";
                            }
                        }
                    }

                    if (authorization.hasGroups()) {
                        List<AuthorizationGroup> groups = new ArrayList<>();

                        if (user.getUserId().equals("user1")) {
                            //user1 is member of group1
                            groups.add(loadGroup("1"));
                        }

                        for (Group group : authorization.getGroups()) {
                            if (groups.stream().anyMatch(m -> m.getId().equals(group.getAuthenticationId()))) {
                                return "200";
                            }
                        }
                    }
                }
            default:
                return "401";
        }
    }

    private boolean searchObjectsByExternalIds(ObjectDataRequest objectDataRequest, List<MObject> allObjects, List<MObject> objectsToReturn, boolean hasMoreValues, Integer objectsInDirectory) {
        List<MObject> objectCollection = allObjects;

        if (Strings.isNullOrEmpty(objectDataRequest.getListFilter().getSearch()) == false) {
            objectCollection = allObjects.stream()
                    .filter(o -> o.getExternalId().startsWith(objectDataRequest.getListFilter().getSearch()))
                    .collect(Collectors.toList());
        }

        int fromValue = 1;
        Integer toValue = objectsInDirectory;

        if (objectDataRequest.getListFilter() != null) {
            fromValue = objectDataRequest.getListFilter().getOffset();
            toValue = objectDataRequest.getListFilter().getOffset() + objectDataRequest.getListFilter().getLimit();
        }

        if (toValue >= objectCollection.size()) {
            hasMoreValues = false;
            toValue = objectCollection.size();
        }

        for (Integer counter = fromValue; counter < toValue; counter++) {
            objectsToReturn.add(allObjects.get(counter));
        }
        return hasMoreValues;
    }

    private AuthorizationGroup loadGroup(String groupId) {
        return new AuthorizationGroup("group" + groupId, "Group " + groupId, "Group " + groupId);
    }

    private List<AuthorizationGroup> loadGroups(Integer howManyGroups) {
        List<AuthorizationGroup> groupCollection = new ArrayList<>();

        for (Integer i = 1; i <= howManyGroups; i++) {
            groupCollection.add(loadGroup(i.toString()));
        }

        return groupCollection;
    }

    private List<AuthorizationUser> loadUsers(Integer howManyUsers) {
        List<AuthorizationUser> userCollection = new ArrayList<>();

        for (Integer i = 1; i <= howManyUsers; i++) {
            userCollection.add(loadUser(i.toString()));
        }

        return userCollection;
    }

    private AuthorizationUser loadUser(String userId) {
        return new AuthorizationUser("user" + userId, "User " + userId, "User " + userId);
    }

    private ObjectDataResponse createResponse(List<MObject> objectCollection, boolean hasMore) {
        ObjectDataResponse objectDataResponse =  new ObjectDataResponse(objectCollection);
        objectDataResponse.setHasMoreResults(hasMore);

        return objectDataResponse;
    }

    private URI baseUri(String headerProtocol) {

        if (Strings.isNullOrEmpty(headerProtocol) == false) {
            UriBuilder uri = uriInfo.getBaseUriBuilder();
            uri.scheme(headerProtocol);

            if (headerProtocol.toLowerCase().equals("https")) {
                uri.port(443);
            }

            return uri.build();
        } else {
            return uriInfo.getBaseUri();
        }
    }
}