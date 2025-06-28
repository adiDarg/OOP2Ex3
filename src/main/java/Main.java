import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        CSVReader reader = new CSVReader();
        reader.chooseFile();
        try {
            reader.read();
            Metadata metadata = reader.convertToMetadata();
            System.out.println(metadata);
            searchComponents(metadata);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
    public static void searchComponents(Metadata metadata){
        while (true){
            Scanner in = new Scanner(System.in);
            System.out.println("Enter component name(-1 to exit):");
            String componentName = in.nextLine();
            if (componentName.equals("-1")) {
                System.exit(0);
            }
            Component component = metadata.getComponent(componentName);
            System.out.println(component);
            for (Relation relation: metadata.getRelations()){
                if (relation.source.equals(component) || relation.target.equals(component)){
                    System.out.println(relation);
                }
            }
        }
    }
}
