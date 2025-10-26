package models;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Commit {
    private final String id;
    private final String message;
    private final Instant timestamp;
    private final Commit parent;
    private final Map<String, Document> snapshot; // immutable copy of the working directory createad at commit time

    public Commit(String message, Commit parent, Map<String, Document> workingDir) {
        this.id = UUID.randomUUID().toString();
        this.message = message;
        this.timestamp = Instant.now();
        this.parent = parent;
        this.snapshot = new HashMap<>();
        // Create snapshot and update working directory file states
        workingDir.forEach((k, v) -> {
            snapshot.put(k, v.clone());
            v.setState(FileState.UNMODIFIED); // Update state after committing
        });
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Commit getParent() {
        return parent;
    }

    public Map<String, Document> getSnapshot() {
        return snapshot;
    }
}
