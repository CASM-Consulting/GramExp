package uk.ac.susx.tag.peg.parboiled.ast;

/**
 * Created by simon on 27/05/16.
 */
public class IdentifierNode extends SuperNode {

    private final String id;

    public IdentifierNode(String start, String id) {
        this.id = start + id;
    }

    public String getId() {
        return id;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
