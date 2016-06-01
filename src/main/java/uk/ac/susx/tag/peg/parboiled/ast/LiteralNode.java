package uk.ac.susx.tag.peg.parboiled.ast;

/**
 * Created by simon on 27/05/16.
 */
public class LiteralNode extends SuperNode {

    private final String lit;

    public LiteralNode(String lit) {
        this.lit = lit;
    }

    public String getLiteral() {
        return lit;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
