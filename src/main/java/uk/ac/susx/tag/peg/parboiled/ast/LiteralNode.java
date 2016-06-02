package uk.ac.susx.tag.peg.parboiled.ast;

/**
 * Created by simon on 27/05/16.
 */
public class LiteralNode extends SuperNode {

    private final String lit;

    public LiteralNode(String lit) {
        char q = lit.length()>1 ? '"' : '\'';

        this.lit = q + lit + q;
    }

    public String getLiteral() {
        return lit;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
