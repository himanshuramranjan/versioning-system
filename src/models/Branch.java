package models;

import java.util.*;

public class Branch {
    private final String name;
    private Commit head; // kind of linked list of commits to chain them together
    private final Queue<Map<String, Document>> stash;
    private final Map<String, Document> branchDirectory;

    public Branch(String name, Repository repository) {
        this.name = name;
        this.head = new Commit("new branch", null, repository.getWorkingDirectory());
        this.stash = new ArrayDeque<>();
        this.branchDirectory = new HashMap<>();

        repository.getWorkingDirectory().forEach((k, v) -> {
            v.setState(FileState.UNMODIFIED);
            branchDirectory.put(k, v.clone());
        });
    }

    public void stashChanges() {
        Map<String, Document> stashEntry = new HashMap<>();
        branchDirectory.forEach((k, v) -> stashEntry.put(k, v.clone()));
        stash.offer(stashEntry);

        branchDirectory.clear();
        head.getSnapshot().forEach((k, v) -> branchDirectory.put(k, v.clone()));

    }

    public void popStash() {
        Map<String, Document> stashedChanges = stash.poll();

        branchDirectory.clear();
        stashedChanges.forEach((k, v) -> branchDirectory.put(k, v.clone()));
    }

    public boolean hasNoStash() {
        return stash.isEmpty();
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

    public Map<String, Document> getBranchDirectory() {
        return branchDirectory;
    }

    public void addChanges(String fileName, String content) {
        this.branchDirectory.put(fileName, new Document(fileName, content));
    }

    public void modifyChanges(String fileName, String content) {
        Document doc = branchDirectory.get(fileName);
        doc.setContent(content);
        doc.setState(FileState.MODIFIED);
    }
}
