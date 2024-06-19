package io.getint.recruitment_task.client.jira.exceptions;

public class JiraClientException extends RuntimeException {
    public JiraClientException(String message) {
        super(message);
    }

    public JiraClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
