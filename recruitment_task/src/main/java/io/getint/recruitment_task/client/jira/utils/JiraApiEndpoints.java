package io.getint.recruitment_task.client.jira.utils;

public class JiraApiEndpoints {
    public static final String SEARCH_ISSUES = "/rest/api/3/search";
    public static final String BULK_MOVE_ISSUES = "/rest/api/3/bulk/issues/move";

    //params
    public static final String JQL_PARAM = "jql";
    public static final String MAX_RESULTS_PARAM = "maxResults";
    public static final String FIELDS_PARAM = "fields";
}