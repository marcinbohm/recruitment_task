package io.getint.recruitment_task.client;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

public interface HttpRequestFactory {
    HttpGet createGetRequest(String path);
    HttpPost createPostRequest(String path);
}
