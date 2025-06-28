public enum RelationType {
    Association("Associative"),
    Grouping("Grouping");
    private final String name;
    RelationType(String name) {
        this.name = name;
    }
    public String toString(){
        return this.name;
    }
}
