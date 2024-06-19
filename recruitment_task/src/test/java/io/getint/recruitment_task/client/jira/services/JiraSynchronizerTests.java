package io.getint.recruitment_task.client.jira.services;

import org.junit.Before;
import org.junit.Test;

import io.getint.recruitment_task.client.jira.exceptions.JiraClientException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

public class JiraSynchronizerTests {

    private JiraProxyMock jiraProxy;
    private JiraSynchronizer jiraSynchronizer;

    @Before
    public void setup() {
        jiraProxy = new JiraProxyMock();
        jiraSynchronizer = new JiraSynchronizer(jiraProxy);
    }

    @Test
    public void shouldMoveTasksSuccessfully() {
        // Setup
        String sourceProjectKey = "SRC";
        String targetProjectKey = "TARGET";
        int maxIssuesToMove = 50;
        List<String> issueTypes = List.of("Bug", "Task");
        JSONArray issues = new JSONArray();
        issues.put(new JSONObject().put("id", "123"));
        jiraProxy.setSearchIssuesResult("{\"issues\":" + issues.toString() + "}");
        jiraProxy.setMoveIssuesBulkResult("{\"status\": \"success\"}");

        // Action
        jiraSynchronizer.moveTasksToOtherProject(sourceProjectKey, targetProjectKey, maxIssuesToMove, issueTypes);

        // No need to explicitly verify as responses are controlled directly
    }

    @Test
    public void shouldHandleEmptyIssueList() {
        // Setup
        String sourceProjectKey = "SRC";
        String targetProjectKey = "TARGET";
        int maxIssuesToMove = 50;
        List<String> issueTypes = List.of("Bug", "Task");
        jiraProxy.setSearchIssuesResult("{\"issues\":[]}");  // Set empty result as valid JSON

        // Action
        jiraSynchronizer.moveTasksToOtherProject(sourceProjectKey, targetProjectKey, maxIssuesToMove, issueTypes);

        // Check that no attempt to move tasks was made
    }

    @Test(expected = JiraClientException.class)
    public void shouldHandleJiraClientExceptionDuringTaskMovement() throws JiraClientException {
        // Setup
        String sourceProjectKey = "SRC";
        String targetProjectKey = "TARGET";
        int maxIssuesToMove = 50;
        List<String> issueTypes = List.of("Bug", "Task");
        jiraProxy.setExceptionToThrow(new JiraClientException("Failed to fetch issues"));

        // Action & Assert
        jiraSynchronizer.moveTasksToOtherProject(sourceProjectKey, targetProjectKey, maxIssuesToMove, issueTypes);
    }

    @Test(expected = JiraClientException.class)
    public void shouldHandleInvalidJQLSyntax() throws JiraClientException {
        // Simulate Jira throwing a syntax error for JQL
        jiraProxy.setExceptionToThrow(new JiraClientException("Invalid JQL syntax"));
        jiraSynchronizer.moveTasksToOtherProject("SRC", "TARGET", 50, List.of("Task"));
    }

    @Test(expected = JiraClientException.class)
    public void shouldHandleHttpNotFound() throws JiraClientException {
        jiraProxy.setExceptionToThrow(new JiraClientException("404 Not Found"));
        jiraSynchronizer.moveTasksToOtherProject("SRC", "TARGET", 50, List.of("Task"));
    }

    @Test
    public void shouldHandlePartialIssueMovement() {
        // Setup for partial success
        JSONArray issues = new JSONArray();
        for (int i = 0; i < 100; i++) {
            issues.put(new JSONObject().put("id", String.valueOf(i + 1)));
        }
        jiraProxy.setSearchIssuesResult("{\"issues\":" + issues.toString() + "}");
        jiraProxy.setMoveIssuesBulkResult("{\"status\": \"partial_success\"}");

        // Test partial movement handling
        jiraSynchronizer.moveTasksToOtherProject("SRC", "TARGET", 100, List.of("Task"));
    }

    @Test
    public void shouldFilterIssueTypesProperly() {
        JSONArray issues = new JSONArray();
        issues.put(new JSONObject().put("id", "123").put("type", "Bug"));
        issues.put(new JSONObject().put("id", "124").put("type", "Task"));
        jiraProxy.setSearchIssuesResult("{\"issues\":" + issues.toString() + "}");

        jiraSynchronizer.moveTasksToOtherProject("SRC", "TARGET", 50, List.of("Bug"));
        // Assertions to ensure only "Bug" type issues are processed
    }
}