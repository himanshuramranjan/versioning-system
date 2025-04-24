package models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Repository {
    private final String id;
    private final String name;
    private final User owner;
    private final Map<String, Branch> branches;
    private Branch currentBranch;
    private final Map<String, Document> workingDirectory;

    public Repository(String name, User owner) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.owner = owner;
        this.branches = new HashMap<>();
        this.workingDirectory = new HashMap<>();

        Commit initialCommit = new Commit("Initial Commit", null, workingDirectory);
        Branch main = new Branch("main", initialCommit);
        branches.put("main", main);
        currentBranch = main;
    }

    public void switchBranch(String branchName) {
        if(branches.containsKey(branchName)) {
            currentBranch = branches.get(branchName);
        } else {
            throw new IllegalArgumentException("Branch not found : " + branchName);
        }
    }

    public void createBranch(String name) {
        branches.put(name, new Branch(name, currentBranch.getHead()));
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

    public Branch getCurrentBranch() {
        return currentBranch;
    }

    public Map<String, Document> getWorkingDirectory() {
        return workingDirectory;
    }
}
