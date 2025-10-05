package info.jab.cursor;

import info.jab.cursor.client.model.ApiKeyInfo;
import info.jab.cursor.client.model.RepositoriesList;
import java.util.List;

/**
 * Interface for general endpoints operations.
 *
 * @see <a href="https://docs.cursor.com/en/background-agent/api/api-key-info">API Key Info API</a>
 * @see <a href="https://docs.cursor.com/en/background-agent/api/list-models">List Models API</a>
 * @see <a href="https://docs.cursor.com/en/background-agent/api/list-repositories">List Repositories API</a>
 */
public interface CursorAgentGeneralEndpoints {

    /**
     * Gets the API key used by this agent.
     *
     * @return The API key
     */
    ApiKeyInfo getApiKeyInfo();

    /**
     * Gets the models available for this agent.
     *
     * @return The models
     */
    List<String> getModels();

    /**
     * Gets the repositories available for this agent.
     *
     * @return The repositories
     */
    RepositoriesList getRepositories();
}
