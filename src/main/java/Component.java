import java.util.ArrayList;
import java.util.List;

public class Component{
    private final String name;
    private final String type;
    private final List<Attribute> attributes;
    public Component(String name,String type) {
        this.name = name;
        this.type = type;
        this.attributes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append(name).append(": ").append(type).append("\n");
        for (Attribute attr : attributes){
            str.append(attr.toString()).append("\n");
        }
        return str.toString();
    }
}
