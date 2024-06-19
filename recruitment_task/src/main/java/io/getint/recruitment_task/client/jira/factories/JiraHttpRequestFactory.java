package io.getint.recruitment_task.client.jira.factories;

import io.getint.recruitment_task.client.HttpRequestFactory;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Factory class for creating HTTP requests to interact with the JIRA API.
 */
@Component
public class JiraHttpRequestFactory implements HttpRequestFactory {

    private static final Logger log = LoggerFactory.getLogger(JiraHttpRequestFactory.class);

    @Value("${jira.api.url}")
    private String baseUrl;

    /**
     * Creates an HTTP GET request with the specified path.
     *
     * @param path the path to append to the base URL
     * @return the constructed HTTP GET request
     */
    @Override
    public HttpGet createGetRequest(String path) {
        String fullUrl = baseUrl + path;
        log.debug("Creating GET request for URL: {}", fullUrl);
        HttpGet request = new HttpGet(fullUrl);
        commonHeaders(request);
        return request;
    }

    /**
     * Creates an HTTP POST request with the specified path.
     *
     * @param path the path to append to the base URL
     * @return the constructed HTTP POST request
     */
    @Override
    public HttpPost createPostRequest(String path) {
        String fullUrl = baseUrl + path;
        log.debug("Creating POST request for URL: {}", fullUrl);
        HttpPost post = new HttpPost(fullUrl);
        commonHeaders(post);
        post.setHeader("Content-type", "application/json");
        return post;
    }

    /**
     * Adds common headers to the given HTTP request.
     *
     * @param request the HTTP request to add headers to
     */
    private void commonHeaders(HttpRequestBase request) {
        request.setHeader("Accept", "application/json");
        //TODO Add more common headers here if needed in the future
    }
}