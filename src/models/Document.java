package models;

public class Document {
    private final String name;
    private String content;
    private FileState state;

    public Document(String name, String content) {
        this.name = name;
        this.content = content;
        this.state = FileState.UNTRACKED;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        if (this.state == FileState.UNMODIFIED) {
            this.state = FileState.MODIFIED;
        }
    }

    public FileState getState() {
        return state;
    }

    public void setState(FileState state) {
        this.state = state;
    }

    public Document clone() {
        Document clone = new Document(this.name, this.content);
        // Inherit the state of the original document
        clone.setState(this.state);
        return clone;
    }
}
