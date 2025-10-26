package models;

public enum FileState {
    UNMODIFIED, // File is tracked and unchanged
    MODIFIED, // File is tracked and has changes
    UNTRACKED // File is not being tracked
}