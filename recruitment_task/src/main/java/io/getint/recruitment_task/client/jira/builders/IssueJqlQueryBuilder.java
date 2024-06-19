package io.getint.recruitment_task.client.jira.builders;

import io.getint.recruitment_task.client.jira.utils.JqlQueryFields;

/**
 * Builder class for constructing JQL queries specifically for issues.
 */
public class IssueJqlQueryBuilder extends AbstractJqlQueryBuilder<IssueJqlQueryBuilder> {

    /**
     * Returns the builder instance.
     *
     * @return the builder instance
     */
    @Override
    protected IssueJqlQueryBuilder self() {
        return this;
    }

    /**
     * Sets the order by created date for the JQL query.
     *
     * @param descending true for descending order, false for ascending order
     * @return the builder instance
     */
    public IssueJqlQueryBuilder setCreatedOrder(boolean descending) {
        return setOrderBy(JqlQueryFields.CREATED, descending);
    }

    /**
     * Sets the order by updated date for the JQL query.
     *
     * @param descending true for descending order, false for ascending order
     * @return the builder instance
     */
    public IssueJqlQueryBuilder setUpdatedOrder(boolean descending) {
        return setOrderBy(JqlQueryFields.UPDATED, descending);
    }
}