package service;

import models.*;
import strategy.DiffStrategy;
import strategy.LineDiffStrategy;

import java.util.Map;

public class VersionManager {
    // Strategy pattern for different diff implementations
    private final DiffStrategy diffStrategy = new LineDiffStrategy();

    // Creates a new commit from the current working directory state.
    public void commit(Repository repo, String message) {
        Commit newCommit = new Commit(message, repo.getCurrentBranch().getHead(), repo.getWorkingDirectory());
        repo.getCurrentBranch().setHead(newCommit);
    }

    // Displays the commit history starting from the current branch's HEAD.
    // Traverses the commit chain using parent references.
    public void log(Repository repo) {
        Commit head = repo.getCurrentBranch().getHead();
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
    public void mergeBranches(Repository repo, String sourceBranchName) throws CloneNotSupportedException {
        Branch source = repo.getBranches().get(sourceBranchName);

        if (source == null) {
            throw new IllegalArgumentException("Source branch doesnt exist");
        }

        // Get the snapshots from both branches
        Commit base = source.getHead();
        Map<String, Document> sourceFiles = base.getSnapshot();

        // Copy files from source to working directory
        for (String name : sourceFiles.keySet()) {
            Document sourceDoc = sourceFiles.get(name);
            Document newDoc = sourceDoc.clone();
            newDoc.setState(FileState.MODIFIED); // Changes from merge should be marked as modified
            repo.getWorkingDirectory().put(name, newDoc);
        }

        // Create merge commit
        commit(repo, "Merged branch " + sourceBranchName);
    }

    // Shows the current state of files in the working directory.
    // Displays which files are modified or untracked.
    public void status(Repository repo) {
        Map<String, Document> workingDir = repo.getWorkingDirectory();

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
    public void stash(Repository repo) {
        Branch currentBranch = repo.getCurrentBranch();
        currentBranch.stashChanges(repo.getWorkingDirectory());
        repo.getWorkingDirectory().clear();
        System.out.println("Changes stashed successfully");
    }

    // Restores the most recently stashed changes.
    // Similar to 'git stash pop' command.
    public void stashPop(Repository repo) {
        Branch currentBranch = repo.getCurrentBranch();
        if (!currentBranch.hasStash()) {
            System.out.println("No stash found");
            return;
        }

        Map<String, Document> stashedChanges = currentBranch.popStash();
        repo.getWorkingDirectory().putAll(stashedChanges);
        System.out.println("Stashed changes applied successfully");
    }
}
