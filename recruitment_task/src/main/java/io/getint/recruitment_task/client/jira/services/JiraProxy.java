package io.getint.recruitment_task.client.jira.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.getint.recruitment_task.client.HttpRequestFactory;
import io.getint.recruitment_task.client.jira.dto.BulkMoveTasksRequestDto;
import io.getint.recruitment_task.client.jira.exceptions.JiraClientException;
import io.getint.recruitment_task.client.jira.exceptions.JiraCommunicationException;
import io.getint.recruitment_task.client.jira.utils.JiraApiEndpoints;
import io.getint.recruitment_task.client.jira.utils.JiraFields;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Service class responsible for interacting with the JIRA API.
 */
@Component
public class JiraProxy {
    private static final Logger log = LoggerFactory.getLogger(JiraProxy.class);
    private static final String JIRA_CONN_FAIL = "Failed to communicate with JIRA API";
    private final CloseableHttpClient httpClient;
    private final HttpRequestFactory httpRequestFactory;

    /**
     * Constructs a new JiraProxy.
     *
     * @param httpClient the HTTP client used for making requests
     * @param httpRequestFactory the factory used for creating HTTP requests
     */
    public JiraProxy(@Qualifier("jiraHttpClient") CloseableHttpClient httpClient, HttpRequestFactory httpRequestFactory) {
        this.httpClient = httpClient;
        this.httpRequestFactory = httpRequestFactory;
    }

    /**
     * Searches for issues in JIRA using the provided JQL query.
     *
     * @param jqlQuery the JQL query to execute
     * @param maxIssuesToMove the maximum number of issues to return
     * @return a JSON string representing the search results
     * @throws JiraClientException if an error occurs while communicating with the JIRA API
     */
    public String searchIssues(String jqlQuery, int maxIssuesToMove) throws JiraClientException {
        log.info("Searching issues with JQL: {}", jqlQuery);
        try {
            URI uri = new URIBuilder(JiraApiEndpoints.SEARCH_ISSUES)
                    .addParameter(JiraApiEndpoints.JQL_PARAM, jqlQuery)
                    .addParameter(JiraApiEndpoints.MAX_RESULTS_PARAM, String.valueOf(maxIssuesToMove))
                    .addParameter(JiraApiEndpoints.FIELDS_PARAM, Arrays.asList(JiraFields.ID, JiraFields.ISSUE_TYPE, JiraFields.SUMMARY, JiraFields.STATUS).toString())
                    .build();
            HttpGet request = httpRequestFactory.createGetRequest(uri.toString());
            return executeRequest(request);
        } catch (URISyntaxException e) {
            log.error("Invalid URI syntax: {}", e.getMessage());
            throw new JiraClientException("Invalid URI syntax: " + e.getMessage(), e);
        }
    }

    /**
     * Moves issues in bulk to a different JIRA project.
     *
     * @param requestDto the request data transfer object containing the bulk move details
     * @return a JSON string representing the response from the bulk move operation
     * @throws JiraClientException if an error occurs while communicating with the JIRA API
     */
    public String moveIssuesBulk(BulkMoveTasksRequestDto requestDto) throws JiraClientException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonPayload = objectMapper.writeValueAsString(requestDto);
            log.info("Bulk move payload: {}", jsonPayload);
            HttpPost post = httpRequestFactory.createPostRequest(JiraApiEndpoints.BULK_MOVE_ISSUES);
            post.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));
            return executeRequest(post);
        } catch (IOException e) {
            throw new JiraClientException("Failed to execute bulk move", e);
        }
    }

    /**
     * Executes the given HTTP request.
     *
     * @param request the HTTP request to execute
     * @return a JSON string representing the response from the JIRA API
     * @throws JiraClientException if an error occurs while communicating with the JIRA API
     */
    private String executeRequest(HttpUriRequest request) throws JiraClientException {
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                throw new JiraClientException("No response body received from the server");
            }

            String responseBody = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);

            int statusCode = response.getStatusLine().getStatusCode();
            log.info("HTTP Status: {}", statusCode);
            log.info("Response body: {}", responseBody);

            return switch (statusCode) {
                case HttpStatus.SC_OK -> responseBody;
                case HttpStatus.SC_NOT_FOUND -> throw new JiraClientException("No issues found or endpoint does not exist: Status Code " + statusCode);
                case HttpStatus.SC_UNAUTHORIZED, HttpStatus.SC_FORBIDDEN -> throw new JiraClientException("Authentication or permission issue: Status Code " + statusCode);
                default -> throw new JiraClientException("Unexpected response from JIRA API: HTTP " + statusCode + " with body " + responseBody);
            };
        } catch (IOException e) {
            log.error(JIRA_CONN_FAIL, e);
            throw new JiraCommunicationException(JIRA_CONN_FAIL, e);
        }
    }
}