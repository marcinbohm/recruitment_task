package io.getint.recruitment_task.client.jira.builders;

import io.getint.recruitment_task.client.jira.utils.JqlQueryFields;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract builder class for constructing JQL (JIRA Query Language) queries.
 *
 * @param <T> the type of the concrete builder extending this abstract builder
 */
public abstract class AbstractJqlQueryBuilder<T extends AbstractJqlQueryBuilder<T>> {
    protected StringBuilder query;

    /**
     * Constructor initializing the query builder.
     */
    protected AbstractJqlQueryBuilder() {
        this.query = new StringBuilder();
    }

    /**
     * Sets the project key for the JQL query.
     *
     * @param projectKey the key of the project
     * @return the builder instance
     */
    public T setProjectKey(String projectKey) {
        query.append(JqlQueryFields.PROJECT).append("=").append(projectKey);
        return self();
    }

    /**
     * Sets the issue types for the JQL query.
     *
     * @param issueTypes the list of issue types
     * @return the builder instance
     */
    public T setIssueTypes(List<String> issueTypes) {
        if (!issueTypes.isEmpty()) {
            query.append(" AND (");
            String issueTypeFilter = issueTypes.stream()
                    .map(type -> JqlQueryFields.ISSUE_TYPE + "=" + type)
                    .collect(Collectors.joining(" OR "));
            query.append(issueTypeFilter).append(")");
        }
        return self();
    }

    /**
     * Sets the order by field for the JQL query.
     *
     * @param field      the field to order by
     * @param descending true for descending order, false for ascending order
     * @return the builder instance
     */
    public T setOrderBy(String field, boolean descending) {
        query.append(" ").append(JqlQueryFields.ORDER_BY).append(" ").append(field);
        if (descending) {
            query.append(" ").append(JqlQueryFields.DESC);
        } else {
            query.append(" ").append(JqlQueryFields.ASC);
        }
        return self();
    }

    /**
     * Returns the builder instance.
     *
     * @return the builder instance
     */
    protected abstract T self();

    /**
     * Builds the JQL query as a string.
     *
     * @return the constructed JQL query string
     */
    public String build() {
        return query.toString();
    }
}
