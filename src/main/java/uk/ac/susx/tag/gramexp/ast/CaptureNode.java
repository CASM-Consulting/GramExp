package uk.ac.susx.tag.gramexp.ast;

/**
 * Created by simon on 27/05/16.
 */
public class CaptureNode extends SuperNode {

    private final String ref;

    public CaptureNode(LiteralNode literalNode) {
        this.ref = literalNode.getLiteral();
    }

    public String getRef() {
        return ref;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
