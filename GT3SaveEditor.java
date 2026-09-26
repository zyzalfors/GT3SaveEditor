package GT3SaveEditor;

public class GT3SaveEditor {

    public static void main(String[] args) {
        new GT3SaveEditorForm(args.length > 0 ? args[0] : null);
    }
}