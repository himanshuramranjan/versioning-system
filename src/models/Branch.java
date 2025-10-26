package models;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class Branch {
    private final String name;
    private Commit head;
    private final Queue<Map<String, Document>> stash;

    public Branch(String name, Commit head) {
        this.name = name;
        this.head = head;
        this.stash = new ArrayDeque<>();
    }

    public void stashChanges(Map<String, Document> workingDirectory) {
        Map<String, Document> stashEntry = new HashMap<>();
        workingDirectory.forEach((k, v) -> stashEntry.put(k, v.clone()));
        stash.offer(stashEntry);
    }

    public Map<String, Document> popStash() {
        return stash.poll();
    }

    public boolean hasStash() {
        return !stash.isEmpty();
    }

    public String getName() {
        return name;
    }

    public Commit getHead() {
        return head;
    }

    public void setHead(Commit head) {
        this.head = head;
    }
}
