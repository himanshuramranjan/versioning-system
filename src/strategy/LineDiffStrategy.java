package strategy;

public class LineDiffStrategy implements DiffStrategy {

    @Override
    public String calculateDiff(String original, String updated) {
        StringBuilder diff = new StringBuilder();
        String[] originalLines = original.split("\\n");
        String[] updatedLines = updated.split("\\n");
        int max = Math.max(originalLines.length, updatedLines.length);

        for(int i = 0; i < max; i++) {
            String orig = i < originalLines.length ? originalLines[i] : "";
            String upd = i < updatedLines.length ? updatedLines[i] : "";

            if(!orig.equals(upd)) {
                diff.append("Line ").append(i + 1).append(":\n");
                diff.append("- ").append(orig).append("\n");
                diff.append("+ ").append(upd).append("\n");
            }
        }
        return diff.toString();
    }
}
