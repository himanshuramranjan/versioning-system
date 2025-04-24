package service;

import models.Branch;
import models.Commit;
import models.Document;
import models.Repository;
import strategy.DiffStrategy;
import strategy.LineDiffStrategy;

import java.util.Map;

public class VersionManager {
    private final DiffStrategy diffStrategy = new LineDiffStrategy();

    public void commit(Repository repo, String message) {
        Commit newCommit = new Commit(message, repo.getCurrentBranch().getHead(), repo.getWorkingDirectory());
        repo.getCurrentBranch().setHead(newCommit);
    }

    public void log(Repository repo) {
        Commit head = repo.getCurrentBranch().getHead();
        while(head != null) {
            System.out.println("Commit: " + head.getId());
            System.out.println("Message: " + head.getMessage());
            System.out.println("Time: " + head.getTimestamp());
            System.out.println("-----------------------------");
            head = head.getParent();
        }
    }

    public void diff(Commit a, Commit b) {
        for(String fileName : a.getSnapshot().keySet()) {
            String contentA = a.getSnapshot().get(fileName).getContent();
            String contentB = b.getSnapshot().getOrDefault(fileName, new Document(fileName, "")).getContent();
            System.out.println("Diff for " + fileName + ":");
            System.out.println(diffStrategy.calculateDiff(contentA, contentB));
        }
    }

    public void mergeBranches(Repository repo, String sourceBranchName) throws CloneNotSupportedException {
        Branch target = repo.getCurrentBranch();
        Branch source = repo.getBranches().get(sourceBranchName);

        if(source == null) {
            throw new IllegalArgumentException("Source branch doesnt exist");
        }

        Commit base = source.getHead();
        Map<String, Document> sourceFiles = base.getSnapshot();
        Map<String, Document> targetFiles = target.getHead().getSnapshot();

        for(String name : sourceFiles.keySet()) {
            Document sourceDoc = sourceFiles.get(name);
            repo.getWorkingDirectory().put(name, sourceDoc.clone());
        }

        commit(repo, "Merged branch " + sourceBranchName);
    }

}
