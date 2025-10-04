package info.jab.cursor;

import info.jab.cursor.client.model.Agent;
import info.jab.cursor.client.model.AgentsList;
import info.jab.cursor.client.model.ConversationResponse;

/**
 * Interface for agent information operations.
 *
 * @see <a href="https://docs.cursor.com/en/background-agent/api/list-agents">List Agents API</a>
 * @see <a href="https://docs.cursor.com/en/background-agent/api/agent-status">Agent Status API</a>
 * @see <a href="https://docs.cursor.com/en/background-agent/api/agent-conversation">Agent Conversation API</a>
 */
public interface CursorAgentInformation {

    /**
     * Gets a list of agents with optional pagination.
     *
     * @param limit Maximum number of agents to return (optional, can be null)
     * @param cursor Pagination cursor for retrieving next page (optional, can be null)
     * @return AgentsList containing the list of agents
     * @throws Exception if the operation fails
     */
    AgentsList getAgents(Integer limit, String cursor) throws Exception;

    /**
     * Gets the current status of a specific agent.
     *
     * @param agentId The ID of the agent to retrieve status for
     * @return Agent instance with current status information
     * @throws Exception if the operation fails
     */
    Agent getStatus(String agentId) throws Exception;

    /**
     * Gets the conversation history for a specific agent.
     *
     * @param agentId The ID of the agent to retrieve conversation for
     * @return ConversationResponse containing the agent's conversation history
     * @throws Exception if the operation fails
     */
    ConversationResponse getAgentConversation(String agentId) throws Exception;

}
