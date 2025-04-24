import models.Document;
import models.Repository;
import models.User;
import service.VersionManager;

public class Main {
    public static void main(String[] args) throws CloneNotSupportedException {

        User user = new User("u1", "Alice");
        Repository repo = new Repository("project-docs", user);
        VersionManager versionManager = new VersionManager();

        // Make some changes
        repo.getWorkingDirectory().put("readme.md", new Document("readme.md", "Version 1"));
        versionManager.commit(repo, "Initial Readme");

        repo.getWorkingDirectory().get("readme.md").setContent("Version 2");
        versionManager.commit(repo, "Updated Readme");

        versionManager.log(repo);

        // Create new branch
        repo.createBranch("feature-x");
        repo.switchBranch("feature-x");

        repo.getWorkingDirectory().get("readme.md").setContent("Feature X changes");
        versionManager.commit(repo, "Added feature X section");

        // Switch back and merge
        repo.switchBranch("main");
        versionManager.mergeBranches(repo, "feature-x");
        versionManager.log(repo);
    }
}