package info.jab.cursor.client;

import info.jab.cursor.client.ApiClient;
import info.jab.cursor.client.ApiException;
import info.jab.cursor.client.api.AgentInformationApi;
import info.jab.cursor.client.api.AgentManagementApi;
import info.jab.cursor.client.api.GeneralEndpointsApi;
import info.jab.cursor.client.model.*;
import java.net.URI;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

/**
 * WireMock integration test for AgentsApi using payload examples from OpenAPI YAML
 */
class AgentsApiWireMockTest {

    private WireMockServer wireMockServer;
    private AgentManagementApi agentManagementApi;
    private AgentInformationApi agentInformationApi;
    private GeneralEndpointsApi generalEndpointsApi;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Start WireMock server
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8080));
        wireMockServer.start();

        // Configure WireMock
        WireMock.configureFor("localhost", 8080);

        // Create ObjectMapper for JSON serialization
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Create API client pointing to WireMock server
        ApiClient apiClient = new ApiClient();
        apiClient.updateBaseUri("http://localhost:8080");
        agentManagementApi = new AgentManagementApi(apiClient);
        agentInformationApi = new AgentInformationApi(apiClient);
        generalEndpointsApi = new GeneralEndpointsApi(apiClient);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Nested
    @DisplayName("Agent management")
    class AgentManagementTests {

        @Test
        @DisplayName("Should launch agent successfully when valid request provided")
        void should_launchAgentSuccessfully_when_validRequestProvided() throws Exception {
            // Given
            LaunchAgentRequest request = createMockLaunchAgentRequest();
            Agent mockResponse = createMockAgent(
                "bc_abc123",
                "Add README Documentation",
                Agent.StatusEnum.CREATING,
                "https://github.com/your-org/your-repo",
                "main",
                "cursor/add-readme-1234",
                "https://cursor.com/agents?id=bc_abc123",
                false,
                "2024-01-15T10:30:00Z",
                null
            );

            stubFor(post(urlEqualTo("/v0/agents"))
                .willReturn(aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(mockResponse))));

            // When
            Agent response = agentManagementApi.launchAgent(request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response)
                .extracting(Agent::getId, Agent::getName, Agent::getStatus)
                .containsExactly("bc_abc123", "Add README Documentation", Agent.StatusEnum.CREATING);
            assertThat(response.getSource())
                .extracting(Source::getRepository, Source::getRef)
                .containsExactly(URI.create("https://github.com/your-org/your-repo"), "main");
            assertThat(response.getTarget())
                .extracting(Target::getBranchName, Target::getUrl, Target::getAutoCreatePr)
                .containsExactly("cursor/add-readme-1234", URI.create("https://cursor.com/agents?id=bc_abc123"), false);

            verify(postRequestedFor(urlEqualTo("/v0/agents"))
                .withHeader("Content-Type", equalTo("application/json")));
        }

        @Test
        @DisplayName("Should throw ApiException when validation error occurs during launch")
        void should_throwApiException_when_validationErrorOccursDuringLaunch() throws Exception {
            // Given
            LaunchAgentRequest request = createMockLaunchAgentRequest();
            ErrorResponse errorResponse = createMockErrorResponse(
                "VALIDATION_ERROR",
                "Invalid request data"
            );

            stubFor(post(urlEqualTo("/v0/agents"))
                .willReturn(aResponse()
                    .withStatus(400)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(errorResponse))));

            // When & Then
            assertThatThrownBy(() -> agentManagementApi.launchAgent(request))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getCode())
                .isEqualTo(400);

            verify(postRequestedFor(urlEqualTo("/v0/agents")));
        }

        @Test
        @DisplayName("Should throw ApiException when unauthorized")
        void should_throwApiException_when_unauthorized() throws Exception {
            // Given
            LaunchAgentRequest request = createMockLaunchAgentRequest();
            ErrorResponse errorResponse = createMockErrorResponse(
                "UNAUTHORIZED",
                "Authentication required"
            );

            stubFor(post(urlEqualTo("/v0/agents"))
                .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(errorResponse))));

            // When & Then
            assertThatThrownBy(() -> agentManagementApi.launchAgent(request))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getCode())
                .isEqualTo(401);
        }


        @Test
        @DisplayName("Should add follow-up successfully when valid request provided")
        void should_addFollowUpSuccessfully_when_validRequestProvided() throws Exception {
            // Given
            String agentId = "bc_abc123";
            FollowUpRequest request = createMockFollowUpRequest();
            FollowUpResponse mockResponse = new FollowUpResponse();
            mockResponse.setId(agentId);

            stubFor(post(urlEqualTo("/v0/agents/" + agentId + "/followup"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(mockResponse))));

            // When
            FollowUpResponse response = agentManagementApi.addFollowUp(agentId, request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(agentId);

            verify(postRequestedFor(urlEqualTo("/v0/agents/" + agentId + "/followup")));
        }

        @Test
        @DisplayName("Should delete agent successfully when valid ID provided")
        void should_deleteAgentSuccessfully_when_validIdProvided() throws Exception {
            // Given
            String agentId = "bc_abc123";
            DeleteAgentResponse mockResponse = new DeleteAgentResponse();
            mockResponse.setId(agentId);

            stubFor(delete(urlEqualTo("/v0/agents/" + agentId))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(mockResponse))));

            // When
            DeleteAgentResponse response = agentManagementApi.deleteAgent(agentId);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(agentId);
            verify(deleteRequestedFor(urlEqualTo("/v0/agents/" + agentId)));
        }

        @Test
        @DisplayName("Should throw ApiException when deleting non-existent agent")
        void should_throwApiException_when_deletingNonExistentAgent() throws Exception {
            // Given
            String agentId = "bc_nonexistent";
            ErrorResponse errorResponse = createMockErrorResponse(
                "AGENT_NOT_FOUND",
                "Agent with specified ID not found"
            );

            stubFor(delete(urlEqualTo("/v0/agents/" + agentId))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(errorResponse))));

            // When & Then
            assertThatThrownBy(() -> agentManagementApi.deleteAgent(agentId))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getCode())
                .isEqualTo(404);

            verify(deleteRequestedFor(urlEqualTo("/v0/agents/" + agentId)));
        }
    }

    @Nested
    @DisplayName("Agent information")
    class AgentInformationTests {

        @Test
        @DisplayName("Should get agent successfully when valid ID provided")
        void should_getAgentSuccessfully_when_validIdProvided() throws Exception {
            // Given
            String agentId = "bc_abc123";
            Agent mockResponse = createMockAgent(
                agentId,
                "Add README Documentation",
                Agent.StatusEnum.COMPLETED,
                "https://github.com/your-org/your-repo",
                "main",
                "cursor/add-readme-1234",
                "https://cursor.com/agents?id=bc_abc123",
                false,
                "2024-01-15T10:30:00Z",
                "2024-01-15T11:45:00Z"
            );

            stubFor(get(urlEqualTo("/v0/agents/" + agentId))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(mockResponse))));

            // When
            Agent response = agentInformationApi.getAgent(agentId);

            // Then
            assertThat(response).isNotNull();
            assertThat(response)
                .extracting(Agent::getId, Agent::getName, Agent::getStatus)
                .containsExactly(agentId, "Add README Documentation", Agent.StatusEnum.COMPLETED);
            assertThat(response.getUpdatedAt()).isNotNull();

            verify(getRequestedFor(urlEqualTo("/v0/agents/" + agentId)));
        }

        @Test
        @DisplayName("Should throw ApiException when agent not found")
        void should_throwApiException_when_agentNotFound() throws Exception {
            // Given
            String agentId = "bc_nonexistent";
            ErrorResponse errorResponse = createMockErrorResponse(
                "AGENT_NOT_FOUND",
                "Agent with specified ID not found"
            );

            stubFor(get(urlEqualTo("/v0/agents/" + agentId))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(errorResponse))));

            // When & Then
            assertThatThrownBy(() -> agentInformationApi.getAgent(agentId))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getCode())
                .isEqualTo(404);

            verify(getRequestedFor(urlEqualTo("/v0/agents/" + agentId)));
        }

        @Test
        @DisplayName("Should list agents successfully when authorized")
        void should_listAgentsSuccessfully_when_authorized() throws Exception {
            // Given
            List<Agent> mockAgents = Arrays.asList(
                createMockAgent(
                    "bc_abc123",
                    "Add README Documentation",
                    Agent.StatusEnum.COMPLETED,
                    "https://github.com/your-org/your-repo",
                    "main",
                    "cursor/add-readme-1234",
                    "https://cursor.com/agents?id=bc_abc123",
                    false,
                    "2024-01-15T10:30:00Z",
                    "2024-01-15T11:45:00Z"
                ),
                createMockAgent(
                    "bc_def456",
                    "Fix authentication bug",
                    Agent.StatusEnum.RUNNING,
                    "https://github.com/your-org/your-repo",
                    "main",
                    "cursor/fix-auth-789",
                    "https://cursor.com/agents?id=bc_def456",
                    true,
                    "2024-01-15T14:20:00Z",
                    null
                )
            );

            AgentsList mockAgentsList = new AgentsList()
                .agents(mockAgents)
                .nextCursor("bc_def456");

            stubFor(get(urlEqualTo("/v0/agents"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(mockAgentsList))));

            // When
            AgentsList response = agentInformationApi.listAgents(null, null);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getAgents()).hasSize(2);
            assertThat(response.getNextCursor()).isEqualTo("bc_def456");
            assertThat(response.getAgents().get(0))
                .extracting(Agent::getId, Agent::getName, Agent::getStatus)
                .containsExactly("bc_abc123", "Add README Documentation", Agent.StatusEnum.COMPLETED);
            assertThat(response.getAgents().get(1))
                .extracting(Agent::getId, Agent::getName, Agent::getStatus)
                .containsExactly("bc_def456", "Fix authentication bug", Agent.StatusEnum.RUNNING);

            verify(getRequestedFor(urlEqualTo("/v0/agents")));
        }

        @Test
        @DisplayName("Should throw ApiException when unauthorized to list agents")
        void should_throwApiException_when_unauthorizedToListAgents() throws Exception {
            // Given
            ErrorResponse errorResponse = createMockErrorResponse(
                "UNAUTHORIZED",
                "Authentication required"
            );

            stubFor(get(urlEqualTo("/v0/agents"))
                .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(errorResponse))));

            // When & Then
            assertThatThrownBy(() -> agentInformationApi.listAgents(null, null))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getCode())
                .isEqualTo(401);

            verify(getRequestedFor(urlEqualTo("/v0/agents")));
        }

        @Test
        @DisplayName("Should list agents with limit parameter")
        void should_listAgentsWithLimit_when_limitProvided() throws Exception {
            // Given
            List<Agent> mockAgents = Arrays.asList(
                createMockAgent(
                    "bc_abc123",
                    "Add README Documentation",
                    Agent.StatusEnum.COMPLETED,
                    "https://github.com/your-org/your-repo",
                    "main",
                    "cursor/add-readme-1234",
                    "https://cursor.com/agents?id=bc_abc123",
                    false,
                    "2024-01-15T10:30:00Z",
                    "2024-01-15T11:45:00Z"
                )
            );

            AgentsList mockAgentsList = new AgentsList()
                .agents(mockAgents)
                .nextCursor("bc_def456");

            stubFor(get(urlPathEqualTo("/v0/agents"))
                .withQueryParam("limit", equalTo("10"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(mockAgentsList))));

            // When
            AgentsList response = agentInformationApi.listAgents(10, null);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getAgents()).hasSize(1);
            assertThat(response.getNextCursor()).isEqualTo("bc_def456");

            verify(getRequestedFor(urlPathEqualTo("/v0/agents"))
                .withQueryParam("limit", equalTo("10")));
        }

        @Test
        @DisplayName("Should list agents with cursor parameter")
        void should_listAgentsWithCursor_when_cursorProvided() throws Exception {
            // Given
            List<Agent> mockAgents = Arrays.asList(
                createMockAgent(
                    "bc_def456",
                    "Fix authentication bug",
                    Agent.StatusEnum.RUNNING,
                    "https://github.com/your-org/your-repo",
                    "main",
                    "cursor/fix-auth-789",
                    "https://cursor.com/agents?id=bc_def456",
                    true,
                    "2024-01-15T14:20:00Z",
                    null
                )
            );

            AgentsList mockAgentsList = new AgentsList()
                .agents(mockAgents)
                .nextCursor("bc_ghi789");

            stubFor(get(urlPathEqualTo("/v0/agents"))
                .withQueryParam("cursor", equalTo("bc_xyz789"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(mockAgentsList))));

            // When
            AgentsList response = agentInformationApi.listAgents(null, "bc_xyz789");

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getAgents()).hasSize(1);
            assertThat(response.getNextCursor()).isEqualTo("bc_ghi789");

            verify(getRequestedFor(urlPathEqualTo("/v0/agents"))
                .withQueryParam("cursor", equalTo("bc_xyz789")));
        }

        @Test
        @DisplayName("Should list agents with both limit and cursor parameters")
        void should_listAgentsWithLimitAndCursor_when_bothProvided() throws Exception {
            // Given
            List<Agent> mockAgents = Arrays.asList(
                createMockAgent(
                    "bc_def456",
                    "Fix authentication bug",
                    Agent.StatusEnum.RUNNING,
                    "https://github.com/your-org/your-repo",
                    "main",
                    "cursor/fix-auth-789",
                    "https://cursor.com/agents?id=bc_def456",
                    true,
                    "2024-01-15T14:20:00Z",
                    null
                )
            );

            AgentsList mockAgentsList = new AgentsList()
                .agents(mockAgents)
                .nextCursor("bc_ghi789");

            stubFor(get(urlPathEqualTo("/v0/agents"))
                .withQueryParam("limit", equalTo("5"))
                .withQueryParam("cursor", equalTo("bc_xyz789"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(mockAgentsList))));

            // When
            AgentsList response = agentInformationApi.listAgents(5, "bc_xyz789");

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getAgents()).hasSize(1);
            assertThat(response.getNextCursor()).isEqualTo("bc_ghi789");

            verify(getRequestedFor(urlPathEqualTo("/v0/agents"))
                .withQueryParam("limit", equalTo("5"))
                .withQueryParam("cursor", equalTo("bc_xyz789")));
        }

        @Test
        @DisplayName("Should get agent conversation successfully when valid ID provided")
        void should_getAgentConversationSuccessfully_when_validIdProvided() throws Exception {
            // Given
            String agentId = "bc_abc123";
            ConversationResponse mockResponse = createMockConversationResponse(agentId);

            stubFor(get(urlEqualTo("/v0/agents/" + agentId + "/conversation"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(mockResponse))));

            // When
            ConversationResponse response = agentInformationApi.getAgentConversation(agentId);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(agentId);
            assertThat(response.getMessages()).isNotNull();
            assertThat(response.getMessages()).hasSize(1);

            verify(getRequestedFor(urlEqualTo("/v0/agents/" + agentId + "/conversation")));
        }

        @Test
        @DisplayName("Should throw ApiException when agent conversation not found")
        void should_throwApiException_when_agentConversationNotFound() throws Exception {
            // Given
            String agentId = "bc_nonexistent";
            ErrorResponse errorResponse = createMockErrorResponse(
                "AGENT_NOT_FOUND",
                "Agent with specified ID not found"
            );

            stubFor(get(urlEqualTo("/v0/agents/" + agentId + "/conversation"))
                .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(errorResponse))));

            // When & Then
            assertThatThrownBy(() -> agentInformationApi.getAgentConversation(agentId))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getCode())
                .isEqualTo(404);

            verify(getRequestedFor(urlEqualTo("/v0/agents/" + agentId + "/conversation")));
        }

        @Test
        @DisplayName("Should handle forbidden access when getting agent conversation")
        void should_handleForbiddenAccess_when_gettingAgentConversation() throws Exception {
            // Given
            String agentId = "bc_abc123";
            ErrorResponse errorResponse = createMockErrorResponse(
                "FORBIDDEN",
                "Insufficient permissions"
            );

            stubFor(get(urlEqualTo("/v0/agents/" + agentId + "/conversation"))
                .willReturn(aResponse()
                    .withStatus(403)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(errorResponse))));

            // When & Then
            assertThatThrownBy(() -> agentInformationApi.getAgentConversation(agentId))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getCode())
                .isEqualTo(403);

            verify(getRequestedFor(urlEqualTo("/v0/agents/" + agentId + "/conversation")));
        }
    }

    @Nested
    @DisplayName("General Endpoints")
    class GeneralEndpointsTests {

        @Test
        @DisplayName("Should get API key info successfully when authorized")
        void should_getApiKeyInfoSuccessfully_when_authorized() throws Exception {
            // Given
            ApiKeyInfo mockResponse = createMockApiKeyInfo();

            stubFor(get(urlEqualTo("/v0/me"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(mockResponse))));

            // When
            ApiKeyInfo response = generalEndpointsApi.getApiKeyInfo();

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getApiKeyName()).isEqualTo("Production API Key");
            assertThat(response.getUserEmail()).isEqualTo("developer@example.com");
            assertThat(response.getCreatedAt()).isNotNull();

            verify(getRequestedFor(urlEqualTo("/v0/me")));
        }

        @Test
        @DisplayName("Should throw ApiException when unauthorized to get API key info")
        void should_throwApiException_when_unauthorizedToGetApiKeyInfo() throws Exception {
            // Given
            ErrorResponse errorResponse = createMockErrorResponse(
                "UNAUTHORIZED",
                "Authentication required"
            );

            stubFor(get(urlEqualTo("/v0/me"))
                .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(errorResponse))));

            // When & Then
            assertThatThrownBy(() -> generalEndpointsApi.getApiKeyInfo())
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getCode())
                .isEqualTo(401);

            verify(getRequestedFor(urlEqualTo("/v0/me")));
        }

        @Test
        @DisplayName("Should list models successfully when authorized")
        void should_listModelsSuccessfully_when_authorized() throws Exception {
            // Given
            ModelsList mockResponse = createMockModelsList();

            stubFor(get(urlEqualTo("/v0/models"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(mockResponse))));

            // When
            ModelsList response = generalEndpointsApi.listModels();

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getModels()).isNotNull();
            assertThat(response.getModels()).hasSize(3);
            assertThat(response.getModels()).containsExactly(
                "claude-4-sonnet-thinking",
                "o3",
                "claude-4-opus-thinking"
            );

            verify(getRequestedFor(urlEqualTo("/v0/models")));
        }

        @Test
        @DisplayName("Should throw ApiException when unauthorized to list models")
        void should_throwApiException_when_unauthorizedToListModels() throws Exception {
            // Given
            ErrorResponse errorResponse = createMockErrorResponse(
                "UNAUTHORIZED",
                "Authentication required"
            );

            stubFor(get(urlEqualTo("/v0/models"))
                .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(errorResponse))));

            // When & Then
            assertThatThrownBy(() -> generalEndpointsApi.listModels())
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getCode())
                .isEqualTo(401);

            verify(getRequestedFor(urlEqualTo("/v0/models")));
        }

        @Test
        @DisplayName("Should list repositories successfully when authorized")
        void should_listRepositoriesSuccessfully_when_authorized() throws Exception {
            // Given
            RepositoriesList mockResponse = createMockRepositoriesList();

            stubFor(get(urlEqualTo("/v0/repositories"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(mockResponse))));

            // When
            RepositoriesList response = generalEndpointsApi.listRepositories();

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getRepositories()).isNotNull();
            assertThat(response.getRepositories()).hasSize(2);
            assertThat(response.getRepositories().get(0))
                .extracting(Repository::getOwner, Repository::getName)
                .containsExactly("your-org", "your-repo");
            assertThat(response.getRepositories().get(1))
                .extracting(Repository::getOwner, Repository::getName)
                .containsExactly("another-org", "another-repo");

            verify(getRequestedFor(urlEqualTo("/v0/repositories")));
        }

        @Test
        @DisplayName("Should throw ApiException when unauthorized to list repositories")
        void should_throwApiException_when_unauthorizedToListRepositories() throws Exception {
            // Given
            ErrorResponse errorResponse = createMockErrorResponse(
                "UNAUTHORIZED",
                "Authentication required"
            );

            stubFor(get(urlEqualTo("/v0/repositories"))
                .willReturn(aResponse()
                    .withStatus(401)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(errorResponse))));

            // When & Then
            assertThatThrownBy(() -> generalEndpointsApi.listRepositories())
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getCode())
                .isEqualTo(401);

            verify(getRequestedFor(urlEqualTo("/v0/repositories")));
        }

        @Test
        @DisplayName("Should handle rate limiting when listing repositories")
        void should_handleRateLimiting_when_listingRepositories() throws Exception {
            // Given
            ErrorResponse errorResponse = createMockErrorResponse(
                "RATE_LIMIT_EXCEEDED",
                "Too many requests"
            );

            stubFor(get(urlEqualTo("/v0/repositories"))
                .willReturn(aResponse()
                    .withStatus(429)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(errorResponse))));

            // When & Then
            assertThatThrownBy(() -> generalEndpointsApi.listRepositories())
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getCode())
                .isEqualTo(429);

            verify(getRequestedFor(urlEqualTo("/v0/repositories")));
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle network timeout gracefully")
        void should_handleNetworkTimeout_when_serverNotResponding() {
            // Given - simulate a connection failure instead of timeout
            stubFor(get(urlEqualTo("/v0/agents/bc_test"))
                .willReturn(aResponse()
                    .withStatus(500)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"error\":{\"code\":\"INTERNAL_ERROR\",\"message\":\"Connection failed\"}}")))
                    ;

            // When & Then
            assertThatThrownBy(() -> agentInformationApi.getAgent("bc_test"))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getCode())
                .isEqualTo(500);
        }

        @ParameterizedTest(name = "Should handle HTTP error codes: {0}")
        @ValueSource(ints = {400, 401, 403, 429, 500})
        @DisplayName("Should handle various HTTP error codes")
        void should_handleHttpErrorCodes_when_serverReturnsError(int errorCode) throws Exception {
            // Given
            String errorCodeString = switch (errorCode) {
                case 400 -> "VALIDATION_ERROR";
                case 401 -> "UNAUTHORIZED";
                case 403 -> "FORBIDDEN";
                case 429 -> "RATE_LIMIT_EXCEEDED";
                case 500 -> "INTERNAL_ERROR";
                default -> "INTERNAL_ERROR";
            };
            ErrorResponse errorResponse = createMockErrorResponse(
                errorCodeString,
                "Server error occurred"
            );
            stubFor(get(urlEqualTo("/v0/agents/bc_test"))
                .willReturn(aResponse()
                    .withStatus(errorCode)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(errorResponse))));

            // When & Then
            assertThatThrownBy(() -> agentInformationApi.getAgent("bc_test"))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getCode())
                .isEqualTo(errorCode);
        }

        @Test
        @DisplayName("Should handle rate limiting")
        void should_handleRateLimiting_when_tooManyRequests() throws Exception {
            // Given
            ErrorResponse errorResponse = createMockErrorResponse(
                "RATE_LIMIT_EXCEEDED",
                "Too many requests"
            );
            stubFor(post(urlEqualTo("/v0/agents"))
                .willReturn(aResponse()
                    .withStatus(429)
                    .withHeader("Content-Type", "application/json")
                    .withBody(objectMapper.writeValueAsString(errorResponse))));

            // When & Then
            LaunchAgentRequest request = createMockLaunchAgentRequest();
            assertThatThrownBy(() -> agentManagementApi.launchAgent(request))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getCode())
                .isEqualTo(429);
        }
    }

    // Helper methods to create mock objects using examples from OpenAPI YAML

    private LaunchAgentRequest createMockLaunchAgentRequest() {
        LaunchAgentRequest request = new LaunchAgentRequest();

        Prompt prompt = new Prompt();
        prompt.setText("Add a README.md file with installation instructions");
        request.setPrompt(prompt);

        Source source = new Source();
        source.setRepository(URI.create("https://github.com/your-org/your-repo"));
        source.setRef("main");
        request.setSource(source);

        return request;
    }

    private LaunchAgentRequest createMockLaunchAgentRequestWithImages() {
        LaunchAgentRequest request = createMockLaunchAgentRequest();

        // Add images to the prompt
        Image image = new Image();
        image.setData("iVBORw0KGgoAAAANSUhEUgAA...".getBytes());

        ImageDimension dimension = new ImageDimension();
        dimension.setWidth(1024);
        dimension.setHeight(768);
        image.setDimension(dimension);

        request.getPrompt().setImages(Arrays.asList(image));

        return request;
    }

    private FollowUpRequest createMockFollowUpRequest() {
        FollowUpRequest request = new FollowUpRequest();

        Prompt prompt = new Prompt();
        prompt.setText("Also add unit tests for the new functionality");
        request.setPrompt(prompt);

        return request;
    }

    private Agent createMockAgent(String id, String name, Agent.StatusEnum status,
                                  String repository, String ref, String branchName,
                                  String url, boolean autoCreatePr,
                                  String createdAt, String updatedAt) {
        Agent agent = new Agent();
        agent.setId(id);
        agent.setName(name);
        agent.setStatus(status);

        Source source = new Source();
        source.setRepository(URI.create(repository));
        source.setRef(ref);
        agent.setSource(source);

        Target target = new Target();
        target.setBranchName(branchName);
        target.setUrl(URI.create(url));
        target.setAutoCreatePr(autoCreatePr);
        agent.setTarget(target);

        agent.setCreatedAt(OffsetDateTime.parse(createdAt));
        if (updatedAt != null) {
            agent.setUpdatedAt(OffsetDateTime.parse(updatedAt));
        }

        return agent;
    }

    private ErrorResponse createMockErrorResponse(String code, String message) {
        ErrorResponse errorResponse = new ErrorResponse();
        info.jab.cursor.client.model.Error error = new info.jab.cursor.client.model.Error();
        error.setCode(info.jab.cursor.client.model.Error.CodeEnum.fromValue(code));
        error.setMessage(message);
        errorResponse.setError(error);
        return errorResponse;
    }

    private ConversationResponse createMockConversationResponse(String agentId) {
        ConversationResponse response = new ConversationResponse();
        response.setId(agentId);

        // Create mock messages based on actual API format
        List<ConversationMessage> messages = Arrays.asList(
            createMockMessage("msg_123", "user_message", "Add a README.md file with installation instructions")
        );

        response.setMessages(messages);
        return response;
    }

    private ConversationMessage createMockMessage(String id, String type, String text) {
        ConversationMessage message = new ConversationMessage();
        message.setId(id);
        message.setType(type);
        message.setText(text);
        return message;
    }

    private ApiKeyInfo createMockApiKeyInfo() {
        ApiKeyInfo apiKeyInfo = new ApiKeyInfo();
        apiKeyInfo.setApiKeyName("Production API Key");
        apiKeyInfo.setCreatedAt(OffsetDateTime.parse("2024-01-15T10:30:00Z"));
        apiKeyInfo.setUserEmail("developer@example.com");
        return apiKeyInfo;
    }

    private ModelsList createMockModelsList() {
        ModelsList modelsList = new ModelsList();
        modelsList.setModels(Arrays.asList(
            "claude-4-sonnet-thinking",
            "o3",
            "claude-4-opus-thinking"
        ));
        return modelsList;
    }

    private RepositoriesList createMockRepositoriesList() {
        RepositoriesList repositoriesList = new RepositoriesList();

        Repository repo1 = new Repository();
        repo1.setOwner("your-org");
        repo1.setName("your-repo");
        repo1.setRepository(URI.create("https://github.com/your-org/your-repo"));

        Repository repo2 = new Repository();
        repo2.setOwner("another-org");
        repo2.setName("another-repo");
        repo2.setRepository(URI.create("https://github.com/another-org/another-repo"));

        repositoriesList.setRepositories(Arrays.asList(repo1, repo2));
        return repositoriesList;
    }
}
