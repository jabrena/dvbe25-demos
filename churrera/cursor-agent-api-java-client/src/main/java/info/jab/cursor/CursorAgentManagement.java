package info.jab.cursor;

import info.jab.cursor.client.model.Agent;
import info.jab.cursor.client.model.FollowUpResponse;

/**
 * Interface for launching Cursor agents with simplified parameters.
 * This interface provides an abstraction layer for interacting with the Cursor API
 * to launch coding agents on repositories.
 *
 * @see <a href="https://docs.cursor.com/en/background-agent/api/launch-an-agent">Launch Agent API</a>
 * @see <a href="https://docs.cursor.com/en/background-agent/api/add-followup">Add Follow-up API</a>
 * @see <a href="https://docs.cursor.com/en/background-agent/api/delete-an-agent">Delete Agent API</a>
 */
public interface CursorAgentManagement {

    /**
     * Launches a Cursor agent with the specified parameters.
     *
     * @param prompt The prompt/instructions for the agent to execute
     * @param model The LLM model to use (e.g., "claude-4-sonnet")
     * @param repository The repository URL where the agent should work
     * @return Agent instance representing the launched agent
     * @throws Exception if the agent launch fails
     */
    Agent launch(String prompt, String model, String repository) throws Exception;

    /**
     * Gets the current status of an agent.
     * This method performs a single status check.
     *
     * @param agentId The ID of the agent to check
     * @return The current Agent instance with updated status
     * @throws Exception if status check fails
     */
    FollowUpResponse followUp(String agentId, String prompt) throws Exception;

    /**
     * Deletes a Cursor agent by its ID.
     *
     * @param agentId The ID of the agent to delete
     * @throws Exception if deletion fails
     */
    void delete(String agentId) throws Exception;
}
