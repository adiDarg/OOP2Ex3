import java.util.LinkedList;
import java.util.List;

public class Relation {
    String name;
    RelationType type;
    Component source;
    Component target;
    List<Attribute> attributes;
    public Relation(String name, RelationType type, Component source, Component target) {
        this.name = name;
        this.type = type;
        this.source = source;
        this.target = target;
        attributes = new LinkedList<>();
    }
    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(type.toString())
                .append(") ").append(source.getType())
                .append("----").append(name)
                .append("---->").append(target.getType())
                .append("\n");
        for (Attribute attribute : attributes) {
            sb.append(attribute.toString()).append("\n");
        }
        return sb.toString();
    }
}
