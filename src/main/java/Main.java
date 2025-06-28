
public class Main {
    public static void main(String[] args) {
        CSVReader reader = new CSVReader();
        reader.chooseFile();
        try {
            reader.read();
            System.out.println(reader.convertToMetadata());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
