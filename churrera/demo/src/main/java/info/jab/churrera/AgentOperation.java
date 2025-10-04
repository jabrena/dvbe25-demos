package info.jab.churrera;

/**
 * Enum representing different types of agent operations.
 */
public enum AgentOperation {
    /**
     * Launch operation - creates a new agent with an initial prompt.
     */
    LAUNCH,

    /**
     * Update operation - sends a follow-up prompt to an existing agent.
     */
    UPDATE
}
