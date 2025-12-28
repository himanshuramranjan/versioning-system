import models.Branch;
import models.Document;
import models.Repository;
import models.User;
import service.VersionManager;

/**
 * This class demonstrates the implementation of a basic version control system
 * similar to Git, but simplified model of its core functionalities.
 */
public class Main {
    public static void main(String[] args) throws CloneNotSupportedException {
        // Create a user and initialize a new repository
        User user = new User("u1", "Alice");
        Repository repo = new Repository("project-docs", user);
        Branch mainBranch = repo.getBranches().get("main");
        VersionManager versionManager = new VersionManager();

        // Example 1: Adding a new file
        // - File goes into working directory first (state: UNTRACKED)
        mainBranch.addChanges("readme.md", "Version 1");

        // - When we commit, it creates a snapshot of working directory
        // - All files in working directory become UNMODIFIED
        versionManager.commit(mainBranch, "Initial Readme");

        // Example 2: Check status when no changes made
        // - Should show no modified/untracked files
        versionManager.status(mainBranch);

        // Example 3: Modify existing file
        // - Changes are made in working directory
        // - File state changes to MODIFIED
        mainBranch.modifyChanges("readme.md", "Version 2");
        versionManager.status(mainBranch);

        // Example 4: Stash Operations
        // - Saves current working directory state
        // - Clears working directory
        versionManager.stash(mainBranch);
        versionManager.status(mainBranch);
//
//        // Example 5: Branch Operations
//        // - Each branch points to a commit
//        // - Branch's HEAD commit contains a snapshot
        repo.createBranch("feature-x");
        Branch featureX = repo.switchBranch("feature-x");
//
//        // Example 6: Restore stashed changes
//        // - Tries to apply stashed changes back to working directory
//        // - NOTE: In this implementation, stash is per-branch.
//        // Since we stashed in 'main' and switched to 'feature-x',
//        // there is no stash available in 'feature-x', so this will print "No stash
//        // found".
        versionManager.stashPop(featureX);
        versionManager.status(featureX);
//
//        // Example 7: Commit in new branch
//        // - Creates new snapshot in feature branch
        featureX.addChanges("newFeature", "Introducing new feature to the app");
        versionManager.commit(featureX, "Added feature X section");
//
//        // Example 7b: Restore stash in main branch
//        // - Switch back to 'main' branch
//        // - Now, pop the stash that was saved earlier in 'main'
//        // - This will restore the stashed changes to the working directory in 'main'
        repo.switchBranch("main");
        versionManager.stashPop(mainBranch);
        versionManager.status(mainBranch);
//
//        // Example 8: Merge Operation
//        // 1. Switch to target branch (main)
//        // 2. Copy files from source branch's HEAD snapshot to working directory
//        // (This is a simple overwrite: all files from 'feature-x' will replace those in
//        // 'main' working directory)
//        // 3. Create a new merge commit in 'main' with the merged content
        versionManager.mergeBranches(repo, "feature-x", "main");
//
//        // Show commit history
//        // - Displays chain of commits with their snapshots
        versionManager.log(mainBranch);
    }
}