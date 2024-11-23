package GT3SaveEditor;

public class Main {
    
    public static void main(String[] args) {
        String path = args.length > 0 ? args[0] : null;
        new Form(path);
    }

}