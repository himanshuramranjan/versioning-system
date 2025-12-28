package service;

import models.*;
import strategy.DiffStrategy;
import strategy.LineDiffStrategy;

import java.util.Map;

public class VersionManager {
    // Strategy pattern for different diff implementations
    private final DiffStrategy diffStrategy = new LineDiffStrategy();

    // Creates a new commit from the current working directory state.
    public void commit(Branch branch, String message) {
        // can validate if there are any changes or not
        Commit newCommit = new Commit(message, branch.getHead(), branch.getBranchDirectory());
        branch.setHead(newCommit);
        System.out.println("Changes committed successfully on the branch " + branch.getName());
    }

    // Displays the commit history starting from the current branch's HEAD.
    // Traverses the commit chain using parent references.
    public void log(Branch branch) {
        Commit head = branch.getHead();
        while (head != null) {
            System.out.println("Commit: " + head.getId());
            System.out.println("Message: " + head.getMessage());
            System.out.println("Time: " + head.getTimestamp());
            System.out.println("-----------------------------");
            head = head.getParent();
        }
    }

    // Compares two commits and shows the differences between their snapshots.
    // Uses the configured diff strategy to calculate differences.
    public void diff(Commit a, Commit b) {
        for (String fileName : a.getSnapshot().keySet()) {
            String contentA = a.getSnapshot().get(fileName).getContent();
            String contentB = b.getSnapshot().getOrDefault(fileName, new Document(fileName, "")).getContent();
            System.out.println("Diff for " + fileName + ":");
            System.out.println(diffStrategy.calculateDiff(contentA, contentB));
        }
    }

    // Merges changes from a source branch into the current branch.
    // Implements a simplified merge strategy (no conflict resolution).
    public void mergeBranches(Repository repo, String sourceBranchName, String targetBranchName) throws CloneNotSupportedException {
        Branch source = repo.getBranches().get(sourceBranchName);
        Branch target = repo.getBranches().get(targetBranchName);

        if (source == null || target == null) {
            throw new IllegalArgumentException("Source or Target branch doesn't exist");
        }

        // Get the snapshots from both branches
        Commit base = source.getHead();
        Map<String, Document> sourceFiles = base.getSnapshot();
        target.getBranchDirectory().clear();

        // Copy files from source to working directory
        for (String name : sourceFiles.keySet()) {
            Document sourceDoc = sourceFiles.get(name);
            Document newDoc = sourceDoc.clone();
            newDoc.setState(FileState.MODIFIED); // Changes from merge should be marked as modified
            target.getBranchDirectory().put(name, newDoc);
        }

        // Create merge commit
        commit(target, "Merged branch " + sourceBranchName);
    }

    // Shows the current state of files in the working directory.
    // Displays which files are modified or untracked.
    public void status(Branch branch) {
        Map<String, Document> workingDir = branch.getBranchDirectory();

        System.out.println("Changes in working directory:");
        for (Map.Entry<String, Document> entry : workingDir.entrySet()) {
            String fileName = entry.getKey();
            Document doc = entry.getValue();
            FileState state = doc.getState();

            switch (state) {
                case MODIFIED:
                    System.out.println("modified: " + fileName);
                    break;
                case UNTRACKED:
                    System.out.println("untracked: " + fileName);
                    break;
                case UNMODIFIED:
                    // Don't show unmodified files in status
                    break;
            }
        }
    }

    // Temporarily stores current working directory changes.
    // Similar to 'git stash' command.
    public void stash(Branch branch) {
        // check if there are any changes first
        branch.stashChanges();
        System.out.println("Changes stashed successfully");
    }

    // Restores the most recently stashed changes.
    // Similar to 'git stash pop' command.
    public void stashPop(Branch branch) {

        if (branch.hasNoStash()) {
            System.out.println("No stash found");
            return;
        }

        branch.popStash();
        System.out.println("Stashed changes applied successfully");
    }
}
