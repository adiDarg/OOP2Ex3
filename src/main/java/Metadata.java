import java.util.*;

public class Metadata {
    List<Component> components;
    List<Relation> relations;
    public Metadata() {
        components = new LinkedList<>();
        relations = new LinkedList<>();
    }
    public void addComponent(Component c) {
        components.add(c);
    }
    public List<Component> getComponents() {
        return new LinkedList<>(components);
    }
    public List<Relation> getRelations(){
        return relations;
    }
    public Component getComponent(String name) {
        for (Component c : components) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }
    public void addRelation(Relation r) {
        relations.add(r);
    }
    public String toString(){
        StringBuilder sb = new StringBuilder();
        HashMap<Component,List<Relation>> map = new HashMap<>();
        for(Component c : getComponents()){
            List<Relation> relationsOfC = new LinkedList<>();
            for(Relation r : relations){
                if (r.source.equals(c)) {
                    relationsOfC.add(r);
                }
            }
            map.put(c, relationsOfC);
        }
        for (Component c : getComponents()) {
            sb.append(c.toString()).append("\n");
            for (Relation r : map.get(c)) {
                sb.append(r.toString()).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
