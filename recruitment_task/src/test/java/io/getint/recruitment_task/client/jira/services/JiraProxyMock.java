package io.getint.recruitment_task.client.jira.services;

import io.getint.recruitment_task.client.jira.dto.BulkMoveTasksRequestDto;
import io.getint.recruitment_task.client.jira.exceptions.JiraClientException;

public class JiraProxyMock extends JiraProxy {

    private String searchIssuesResult = "{\"issues\":[]}";
    private String moveIssuesBulkResult = "{\"status\": \"success\"}";
    private Exception exceptionToThrow;

    public JiraProxyMock() {
        super(null, null); // Nie używamy httpClient ani httpRequestFactory
    }

    public void setSearchIssuesResult(String result) {
        this.searchIssuesResult = result;
    }

    public void setMoveIssuesBulkResult(String result) {
        this.moveIssuesBulkResult = result;
    }

    public void setExceptionToThrow(Exception exception) {
        this.exceptionToThrow = exception;
    }

    @Override
    public String searchIssues(String jqlQuery, int maxIssuesToMove) throws JiraClientException {
        if (exceptionToThrow != null) {
            if (exceptionToThrow instanceof JiraClientException)
                throw (JiraClientException) exceptionToThrow;
            else
                throw new RuntimeException(exceptionToThrow);
        }
        return searchIssuesResult;
    }

    @Override
    public String moveIssuesBulk(BulkMoveTasksRequestDto requestDto) throws JiraClientException {
        if (exceptionToThrow != null) {
            if (exceptionToThrow instanceof JiraClientException)
                throw (JiraClientException) exceptionToThrow;
            else
                throw new RuntimeException(exceptionToThrow);
        }
        return moveIssuesBulkResult;
    }
}