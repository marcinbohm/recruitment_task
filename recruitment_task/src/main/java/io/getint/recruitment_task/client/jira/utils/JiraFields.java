package io.getint.recruitment_task.client.jira.utils;

public class JiraFields {
    // Fields related to project information
    public static final String PROJECT = "project";
    public static final String KEY = "key";

    // Fields related to issue details
    public static final String SUMMARY = "summary";
    public static final String DESCRIPTION = "description";
    public static final String PRIORITY = "priority";
    public static final String STATUS = "status";
    public static final String ISSUE_TYPE = "issuetype";
    public static final String PARENT = "parent";
    public static final String SUBTASK = "subtask";

    // Fields related to identifiers
    public static final String ID = "id";
    public static final String NAME = "name";

    // Fields related to comments
    public static final String COMMENT = "comment";
    public static final String BODY = "body";

    // Additional JSON fields commonly used in Jira API responses
    public static final String ISSUES = "issues"; // Field in JSON response containing an array of issues
    public static final String FIELDS = "fields"; // Field in JSON response containing details of an issue
}