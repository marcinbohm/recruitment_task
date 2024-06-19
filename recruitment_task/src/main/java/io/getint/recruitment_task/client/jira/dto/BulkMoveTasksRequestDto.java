package io.getint.recruitment_task.client.jira.dto;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class BulkMoveTasksRequestDto {
    private Map<String, TargetToSourcesMapping> targetToSourcesMapping;

    public static class BulkMoveTasksRequestDtoBuilder {
        private Map<String, TargetToSourcesMapping> targetToSourcesMapping = new HashMap<>();

        public BulkMoveTasksRequestDtoBuilder addTargetToSourcesMapping(String key, TargetToSourcesMapping value) {
            this.targetToSourcesMapping.put(key, value);
            return this;
        }

        public Map<String, TargetToSourcesMapping> getTargetToSourcesMapping() {
            return targetToSourcesMapping;
        }
    }

    @Data
    @Builder
    public static class TargetToSourcesMapping {
        @Builder.Default
        private boolean inferFieldDefaults = true;
        @Builder.Default
        private boolean inferStatusDefaults = true;
        @Builder.Default
        private boolean inferSubtaskTypeDefault = true;
        private List<String> issueIdsOrKeys;
    }
}