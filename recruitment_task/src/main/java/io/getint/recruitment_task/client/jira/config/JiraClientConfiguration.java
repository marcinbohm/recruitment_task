package io.getint.recruitment_task.client.jira.config;

import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

/**
 * Configuration class for setting up the JIRA HTTP client.
 */
@Configuration
public class JiraClientConfiguration {

    @Value("${jira.api.username}")
    private String jiraApiUsername;

    @Value("${jira.api.token}")
    private String jiraApiToken;

    @Value("${jira.api.connectTimeoutMs}")
    private int connectTimeoutMs;

    @Value("${jira.api.socketTimeoutMs}")
    private int socketTimeoutMs;

    @Value("${jira.api.connectionRequestTimeoutMs}")
    private int connectionRequestTimeoutMs;

    @Value("${jira.api.maxTotalConnections}")
    private int maxTotalConnections;

    @Value("${jira.api.maxConnectionsPerRoute}")
    private int maxConnectionsPerRoute;

    /**
     * Bean definition for the JIRA HTTP client.
     *
     * @return a configured {@link CloseableHttpClient} instance
     */
    @Bean(name = "jiraHttpClient")
    public CloseableHttpClient jiraHttpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectTimeoutMs)
                .setSocketTimeout(socketTimeoutMs)
                .setConnectionRequestTimeout(connectionRequestTimeoutMs)
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxTotalConnections);
        connectionManager.setDefaultMaxPerRoute(maxConnectionsPerRoute);

        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .addInterceptorFirst(new RequestAcceptEncoding())
                .addInterceptorFirst(new ResponseContentEncoding())
                .addInterceptorFirst(this::addAuthorizationHeader)
                .build();
    }

    /**
     * Adds the authorization header to the HTTP request.
     *
     * @param request the HTTP request
     * @param context the HTTP context
     */
    private void addAuthorizationHeader(HttpRequest request, HttpContext context) {
        String auth = jiraApiUsername + ":" + jiraApiToken;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        String authHeader = "Basic " + encodedAuth;
        request.addHeader(HttpHeaders.AUTHORIZATION, authHeader);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    }
}