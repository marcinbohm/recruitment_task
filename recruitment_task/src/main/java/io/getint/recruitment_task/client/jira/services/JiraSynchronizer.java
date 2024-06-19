package io.getint.recruitment_task.client.jira.services;

import io.getint.recruitment_task.client.jira.builders.IssueJqlQueryBuilder;
import io.getint.recruitment_task.client.jira.dto.BulkMoveTasksRequestDto;
import io.getint.recruitment_task.client.jira.exceptions.JiraClientException;
import io.getint.recruitment_task.client.jira.utils.JiraFields;
import lombok.AllArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Service class responsible for synchronizing tasks between JIRA projects.
 */
@Component
@AllArgsConstructor
public class JiraSynchronizer {
    private static final Logger log = LoggerFactory.getLogger(JiraSynchronizer.class);
    private final JiraProxy jiraProxy;
    private static final int MAX_BULK_OPERATION_SIZE = 1000;

    /**
     * Moves tasks from one JIRA project to another.
     *
     * @param sourceProjectKey the key of the source project
     * @param targetProjectKey the key of the target project
     * @param maxIssuesToMove  the maximum number of issues to move
     * @param issueTypeNames   the list of issue types to move
     */
    public void moveTasksToOtherProject(String sourceProjectKey, String targetProjectKey, int maxIssuesToMove, List<String> issueTypeNames) throws JiraClientException {
        String jqlQuery = buildJqlQuery(sourceProjectKey, issueTypeNames);
        log.info("Executing JQL: {}", jqlQuery);
        try {
            JSONArray issues = fetchIssues(jqlQuery, maxIssuesToMove);
            List<JSONArray> chunks = chunkIssues(issues, MAX_BULK_OPERATION_SIZE);
            for (JSONArray chunk : chunks) {
                BulkMoveTasksRequestDto requestDto = createBulkMoveDto(chunk, targetProjectKey);
                log.info("Sending bulk move request: {}", requestDto);
                String response = jiraProxy.moveIssuesBulk(requestDto);
                log.info("Bulk move response: {}", response);
            }
        } catch (JiraClientException e) {
            log.error("Error during bulk move operation", e);
            throw e;
        }
    }

    /**
     * Chunks the given JSONArray into smaller arrays of specified maximum size.
     *
     * @param issues the JSONArray to chunk
     * @param maxSize the maximum size of each chunk
     * @return a list of JSONArrays, each containing a subset of the original array's elements
     */
    private List<JSONArray> chunkIssues(JSONArray issues, int maxSize) {
        List<JSONArray> chunks = new ArrayList<>();
        JSONArray currentChunk = new JSONArray();
        for (int i = 0; i < issues.length(); i++) {
            currentChunk.put(issues.get(i));
            if ((i + 1) % maxSize == 0 || i + 1 == issues.length()) {
                chunks.add(currentChunk);
                currentChunk = new JSONArray();
            }
        }
        return chunks;
    }

    /**
     * Creates a BulkMoveTasksRequestDto for moving the given issues to the target project.
     *
     * @param issues the issues to move
     * @param targetProjectKey the key of the target project
     * @return the BulkMoveTasksRequestDto for the bulk move operation
     */
    private BulkMoveTasksRequestDto createBulkMoveDto(JSONArray issues, String targetProjectKey) {
        BulkMoveTasksRequestDto.BulkMoveTasksRequestDtoBuilder builder = BulkMoveTasksRequestDto.builder();

        for (int i = 0; i < issues.length(); i++) {
            JSONObject issue = issues.getJSONObject(i);
            log.info("Processing issue: {}", issue);

            String key = getKeyForIssue(issue, targetProjectKey);
            if (key == null) {
                continue;
            }

            log.info("Mapping key: {}", key);

            BulkMoveTasksRequestDto.TargetToSourcesMapping.TargetToSourcesMappingBuilder targetBuilder =
                    BulkMoveTasksRequestDto.TargetToSourcesMapping.builder();

            BulkMoveTasksRequestDto.TargetToSourcesMapping mapping = builder.getTargetToSourcesMapping().getOrDefault(key,
                    targetBuilder.issueIdsOrKeys(new ArrayList<>()).build());

            mapping.getIssueIdsOrKeys().add(issue.getString(JiraFields.ID));

            builder.addTargetToSourcesMapping(key, mapping);
        }

        return builder.build();
    }

    /**
     * Generates a unique key for the given issue in the context of the target project.
     *
     * @param issue the issue to generate the key for
     * @param targetProjectKey the key of the target project
     * @return the generated key, or null if the issue is invalid
     */
    private String getKeyForIssue(JSONObject issue, String targetProjectKey) {
        if (!issue.has(JiraFields.FIELDS)) {
            log.warn("Issue {} does not have a 'fields' field", issue.getString(JiraFields.ID));
            return null;
        }

        JSONObject fields = issue.getJSONObject(JiraFields.FIELDS);

        if (!fields.has(JiraFields.ISSUE_TYPE)) {
            log.warn("Issue {} does not have an 'issuetype' field in 'fields'", issue.getString(JiraFields.ID));
            return null;
        }

        JSONObject issueType = fields.getJSONObject(JiraFields.ISSUE_TYPE);
        String key = targetProjectKey + "," + issueType.getString(JiraFields.ID);

        if (issueType.optBoolean(JiraFields.SUBTASK, false)) {
            if (!fields.has(JiraFields.PARENT)) {
                log.warn("Issue {} is a subtask but has no parent field", issue.getString(JiraFields.ID));
                return null;
            }
            JSONObject parent = fields.getJSONObject(JiraFields.PARENT);
            key += "," + parent.getString(JiraFields.ID);
        }

        return key;
    }

    /**
     * Builds a JQL query to retrieve issues from the source project.
     *
     * @param sourceProjectKey the key of the source project
     * @param issueTypeNames the list of issue types to include in the query
     * @return the constructed JQL query
     */
    private String buildJqlQuery(String sourceProjectKey, List<String> issueTypeNames) {
        return new IssueJqlQueryBuilder()
                .setProjectKey(sourceProjectKey)
                .setIssueTypes(issueTypeNames)
                .setCreatedOrder(true)
                .build();
    }

    /**
     * Fetches issues from JIRA using the given JQL query.
     *
     * @param jqlQuery the JQL query to execute
     * @param maxIssuesToMove the maximum number of issues to fetch
     * @return a JSONArray of issues
     * @throws JiraClientException if an error occurs while communicating with the JIRA API
     */
    private JSONArray fetchIssues(String jqlQuery, int maxIssuesToMove) throws JiraClientException {
        String issuesResponse = jiraProxy.searchIssues(jqlQuery, maxIssuesToMove);
        JSONObject issuesJson = new JSONObject(issuesResponse);
        return issuesJson.getJSONArray(JiraFields.ISSUES);
    }
}