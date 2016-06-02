package uk.ac.susx.tag.peg.parboiled.ast;

/**
 * Created by simon on 27/05/16.
 */
public class LiteralNode extends SuperNode {

    private final String lit;
    private final boolean stringify;

    public LiteralNode(String lit) {
        char q = lit.length()>1 ? '"' : '\'';

        this.lit = q + lit + q;
        this.stringify = true;
    }

    public String getLiteral() {
        return lit;
    }

    public boolean isStringify() {
        return stringify;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
