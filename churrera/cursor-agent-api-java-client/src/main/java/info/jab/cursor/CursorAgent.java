package info.jab.cursor;

import info.jab.cursor.client.model.Agent;
import info.jab.cursor.client.ApiClient;
import info.jab.cursor.client.api.AgentInformationApi;
import info.jab.cursor.client.api.AgentManagementApi;
import info.jab.cursor.client.model.LaunchAgentRequest;
import info.jab.cursor.client.model.Prompt;
import info.jab.cursor.client.model.Source;
import info.jab.cursor.client.model.TargetRequest;
import info.jab.cursor.client.model.FollowUpRequest;
import info.jab.cursor.client.model.FollowUpResponse;
import info.jab.cursor.client.model.AgentsList;
import info.jab.cursor.client.model.ConversationResponse;
import info.jab.cursor.client.model.ApiKeyInfo;
import info.jab.cursor.client.model.ModelsList;
import info.jab.cursor.client.model.RepositoriesList;
import info.jab.cursor.client.api.GeneralEndpointsApi;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;

/**
 * Implementation of the Agent interface that provides a simplified abstraction
 * for launching Cursor agents using the Cursor Background Agents API.
 *
 * This class encapsulates the complexity of creating API requests and provides
 * a clean interface for launching agents with basic parameters.
 */
public class CursorAgent implements CursorAgentManagement, CursorAgentInformation, CursorAgentGeneralEndpoints {

    private static final String DEFAULT_API_BASE_URL = "https://api.cursor.com";
    private static final String DEFAULT_BRANCH = "main";

    private final String apiKey;
    private final String apiBaseUrl;
    private final AgentManagementApi agentManagementApi;
    private final AgentInformationApi agentInformationApi;
    private final GeneralEndpointsApi generalEndpointsApi;

    /**
     * Creates a new CursorAgent with the specified API key.
     * Uses the default API base URL.
     *
     * @param apiKey The API key for authentication with Cursor API
     */
    public CursorAgent(String apiKey) {
        this(apiKey, DEFAULT_API_BASE_URL);
    }

    /**
     * Creates a new CursorAgent with the specified API key and base URL.
     *
     * @param apiKey The API key for authentication with Cursor API
     * @param apiBaseUrl The base URL for the Cursor API
     */
    public CursorAgent(String apiKey, String apiBaseUrl) {
        this.apiKey = apiKey;
        this.apiBaseUrl = apiBaseUrl;

        // Initialize API client with HTTP logging
        ApiClient apiClient = new ApiClient();
        apiClient.updateBaseUri(apiBaseUrl);

        this.agentManagementApi = new AgentManagementApi(apiClient);
        this.agentInformationApi = new AgentInformationApi(apiClient);
        this.generalEndpointsApi = new GeneralEndpointsApi(apiClient);
    }

    // Methods from CursorAgentManagement interface

    /**
     * Launches a Cursor agent with the specified parameters.
     *
     * @param prompt The prompt/instructions for the agent to execute
     * @param model The LLM model to use (e.g., "claude-4-sonnet")
     * @param repository The repository URL where the agent should work
     * @return Agent instance representing the launched agent
     * @throws Exception if the agent launch fails
     */
    @Override
    public Agent launch(String prompt, String model, String repository) {
        // Validate inputs
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new IllegalArgumentException("Prompt cannot be null or empty");
        }
        if (model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Model cannot be null or empty");
        }
        if (repository == null || repository.trim().isEmpty()) {
            throw new IllegalArgumentException("Repository cannot be null or empty");
        }

        // Create the prompt
        Prompt promptObj = new Prompt();
        promptObj.setText(prompt);

        // Create the source (repository and branch)
        Source source = new Source();
        source.setRepository(URI.create(repository));
        source.setRef(DEFAULT_BRANCH);

        // Create the target configuration (optional)
        TargetRequest target = new TargetRequest();
        target.setAutoCreatePr(true);  // Automatically create PR when agent completes

        // Create the launch request
        LaunchAgentRequest request = new LaunchAgentRequest();
        request.setPrompt(promptObj);
        request.setSource(source);
        request.setModel(model);
        request.setTarget(target);

        // Prepare authentication headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);

        // Launch the agent
        try {
            return agentManagementApi.launchAgent(request, headers);
        } catch (Exception e) {
            //info.jab.cursor.client.ApiException;
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the API key used by this agent.
     *
     * @return The API key
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Gets the API base URL used by this agent.
     *
     * @return The API base URL
     */
    public String getApiBaseUrl() {
        return apiBaseUrl;
    }


    @Override
    public FollowUpResponse followUp(String agentId, String prompt) {
        // Create the prompt
        Prompt promptObj = new Prompt();
        promptObj.setText(prompt);

        // Create the follow-up request
        FollowUpRequest request = new FollowUpRequest();
        request.setPrompt(promptObj);

        // Prepare authentication headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);

        // Follow-up the agent
        try {
            return agentManagementApi.addFollowUp(agentId, request, headers);
        } catch (Exception e) {
            //info.jab.cursor.client.ApiException;
            throw new RuntimeException(e);
        }
    }


    @Override
    public void delete(String agentId) {
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    // Methods from CursorAgentInformation interface

    /**
     * Gets a list of agents with optional pagination.
     *
     * @param limit Maximum number of agents to return (optional, can be null)
     * @param cursor Pagination cursor for retrieving next page (optional, can be null)
     * @return AgentsList containing the list of agents
     * @throws Exception if the operation fails
     */
    @Override
    public AgentsList getAgents(Integer limit, String cursor) {
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    /**
     * Gets the current status of an agent.
     * This method performs a single status check.
     *
     * @param agentId The ID of the agent to check
     * @return The current Agent instance with updated status
     * @throws Exception if status check fails
     */
    @Override
    public Agent getStatus(String agentId) {
        if (agentId == null || agentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Agent ID cannot be null or empty");
        }

        // Prepare authentication headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);

        try {
            // Get current agent status - single API call
            return agentInformationApi.getAgent(agentId, headers);
        } catch (Exception statusException) {
            // If status parsing fails due to unknown enum value, try to handle gracefully
            if (statusException.getMessage() != null && statusException.getMessage().contains("Unexpected value")) {
                // For now, re-throw the exception. The calling layer can handle unknown statuses
                throw new RuntimeException("Agent status contains unknown value: " + statusException.getMessage(), statusException);
            } else {
                throw new RuntimeException(statusException);
            }
        }
    }

    /**
     * Checks if the given status represents a terminal state (completed, failed, etc.).
     */
    private boolean isTerminalStatus(Agent.StatusEnum status) {
        if (status == null) {
            return false;
        }

        // Terminal statuses that indicate the agent has finished
        return status == Agent.StatusEnum.COMPLETED ||
               status == Agent.StatusEnum.FAILED ||
               status == Agent.StatusEnum.EXPIRED ||
               status == Agent.StatusEnum.CANCELLED;
    }

    /**
     * Checks if the given status string represents a terminal state.
     * This handles both known enum values and API-specific statuses like "FINISHED".
     */
    private boolean isTerminalStatusString(String status) {
        if (status == null || status.trim().isEmpty()) {
            return false;
        }

        String upperStatus = status.toUpperCase().trim();

        // Known terminal statuses
        return "COMPLETED".equals(upperStatus) ||
               "FAILED".equals(upperStatus) ||
               "CANCELLED".equals(upperStatus) ||
               "FINISHED".equals(upperStatus) ||
               "EXPIRED".equals(upperStatus) ||
               "ERROR".equals(upperStatus);
    }

    /**
     * Extracts the status value from Jackson parsing error messages.
     * Handles errors like "Unexpected value 'FINISHED'"
     */
    private String extractStatusFromError(String errorMessage) {
        if (errorMessage == null) {
            return "UNKNOWN";
        }

        // Look for pattern: Unexpected value 'STATUS'
        Pattern pattern = Pattern.compile("Unexpected value '([^']+)'");
        Matcher matcher = pattern.matcher(errorMessage);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "UNKNOWN";
    }

    /**
     * Gets the conversation history for a specific agent.
     *
     * @param agentId The ID of the agent to retrieve conversation for
     * @return ConversationResponse containing the agent's conversation history
     */
    @Override
    public ConversationResponse getAgentConversation(String agentId) {
        if (agentId == null || agentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Agent ID cannot be null or empty");
        }

        // Prepare authentication headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);

        try {
            return agentInformationApi.getAgentConversation(agentId, headers);
        } catch (Exception e) {
            //info.jab.cursor.client.ApiException;
            throw new RuntimeException(e);
        }
    }

    // Methods from CursorAgentGeneralEndpoints interface

    @Override
    public ApiKeyInfo getApiKeyInfo() {
        // Prepare authentication headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);

        try {
           return generalEndpointsApi.getApiKeyInfo(headers);
        } catch (Exception e) {
            //info.jab.cursor.client.ApiException;
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getModels() {

        // Prepare authentication headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);

        try {
            return generalEndpointsApi.listModels(headers).getModels();
        } catch (Exception e) {
            //info.jab.cursor.client.ApiException;
            throw new RuntimeException(e);
        }
    }

    @Override
    public RepositoriesList getRepositories() {

        // Prepare authentication headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);

        try {
            return generalEndpointsApi.listRepositories(headers);
        } catch (Exception e) {
            //info.jab.cursor.client.ApiException;
            throw new RuntimeException(e);
        }
    }

}
