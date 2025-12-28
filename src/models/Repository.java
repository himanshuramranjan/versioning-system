package models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Repository {
    private final String id;
    private final String name;
    private final User owner;
    private final Map<String, Branch> branches;
    private final Map<String, Document> workingDirectory; // the actual files you see and edit in your local directory, changes here are made before committing

    public Repository(String name, User owner) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.owner = owner;
        this.branches = new HashMap<>();
        this.workingDirectory = new HashMap<>();

        Branch main = new Branch("main", this);
        branches.put("main", main);
        switchBranch("main");
    }

    public Branch switchBranch(String targetBranchName) {
        Branch target = getBranches().get(targetBranchName);

        if (target == null) {
            throw new IllegalArgumentException("Target branch doesn't exist");
        }

        // Check for uncommitted changes in working directory
        boolean hasUncommittedChanges = false;
        for (Document doc : getWorkingDirectory().values()) {
            if (doc.getState() == FileState.MODIFIED || doc.getState() == FileState.UNTRACKED) {
                hasUncommittedChanges = true;
                break;
            }
        }

        // Prevent switch if there are uncommitted changes
        if (hasUncommittedChanges) {
            System.out.println("Error: You have uncommitted changes. Commit or stash them before switching branches.");
            return null;
        }

        System.out.println("Switched to branch " + targetBranchName);
        return this.getBranches().get(targetBranchName);
    }

    public void createBranch(String name) {
        branches.put(name, new Branch(name, this));
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public User getOwner() {
        return owner;
    }

    public Map<String, Branch> getBranches() {
        return branches;
    }

    public Map<String, Document> getWorkingDirectory() {
        return workingDirectory;
    }
}
