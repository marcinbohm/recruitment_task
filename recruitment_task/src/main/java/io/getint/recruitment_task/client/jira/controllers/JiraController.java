package io.getint.recruitment_task.client.jira.controllers;

import io.getint.recruitment_task.client.jira.services.JiraSynchronizer;
import io.getint.recruitment_task.client.jira.exceptions.JiraClientException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/jira")
@AllArgsConstructor
public class JiraController {
    private static final Logger log = LoggerFactory.getLogger(JiraController.class);

    private final JiraSynchronizer jiraSynchronizer;

    /**
     * Endpoint to synchronize tasks from one Jira project to another.
     *
     * @param sourceProjectKey Key of the source project from which to move tasks.
     * @param targetProjectKey Key of the target project to which tasks are moved.
     * @param maxIssuesToMove Maximum number of issues to move.
     * @param issueTypeNames List of issue types to be moved (optional).
     * @return ResponseEntity with status and message.
     */
    @PostMapping("/sync-tasks")
    public ResponseEntity<String> syncTasks(@RequestParam String sourceProjectKey,
                                            @RequestParam String targetProjectKey,
                                            @RequestParam int maxIssuesToMove,
                                            @RequestParam(required = false) List<String> issueTypeNames) {
        try {
            if (issueTypeNames == null) {
                issueTypeNames = Collections.emptyList();
            }
            jiraSynchronizer.moveTasksToOtherProject(sourceProjectKey, targetProjectKey, maxIssuesToMove, issueTypeNames);
            return ResponseEntity.ok("Tasks synchronization initiated successfully.");
        } catch (JiraClientException e) {
            log.error("Error during tasks synchronization", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to synchronize tasks: " + e.getMessage());
        }
    }
}