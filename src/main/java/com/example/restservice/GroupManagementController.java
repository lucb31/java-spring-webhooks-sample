package com.example.restservice;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.Group;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.GroupCollectionPage;
import com.microsoft.graph.requests.GroupCollectionRequestBuilder;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;


@RestController
public class GroupManagementController {
    // region app registration information
    private final String clientId = "";
    private final String clientSecret = "";
    private final String tenantId = "";

    private final List<String> scopes = Arrays.asList("https://graph.microsoft.com/.default");
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());


    @GetMapping("/groups")
    @ResponseBody
    public String welcome() {
        final GraphServiceClient<Request> graphClient = getClient();
        graphClient.setServiceRoot("https://graph.microsoft.com/beta");
        List<Group> groups = getAllGroups(graphClient);
        LOGGER.debug(groups.toString());
        return "Hello World";
    }

    private List<Group> getAllGroups(GraphServiceClient<Request> graphClient) {
        GroupCollectionPage groupPage = graphClient.groups().buildRequest().get();
        List<Group> groups= groupPage.getCurrentPage();
        while (groupPage != null) {
            groups= groupPage.getCurrentPage();
            final GroupCollectionRequestBuilder nextPage = groupPage.getNextPage();
            if(nextPage == null) {
                break;
            } else {
                groupPage = nextPage.buildRequest().get();
            }
        }
        return groups;
    }

    private GraphServiceClient<Request> getClient() {
        final ClientSecretCredential defaultCredential = new ClientSecretCredentialBuilder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .tenantId(tenantId)
                .build();
        final IAuthenticationProvider authProvider = new TokenCredentialAuthProvider(this.scopes, defaultCredential);
        return GraphServiceClient.builder().authenticationProvider(authProvider).buildClient();
    }
    /*
    public GroupManagement groupManagement() {
        return new GroupManagement("Hello World");
    }

     */
}
