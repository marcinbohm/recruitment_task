package io.getint.recruitment_task.client.jira.exceptions;

public class JiraCommunicationException extends JiraClientException {
    public JiraCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
