package GT3SaveEditor;
import java.util.Locale;

public class GT3SaveEditor {

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        new GT3SaveEditorForm(args.length > 0 ? args[0] : null);
    }
}