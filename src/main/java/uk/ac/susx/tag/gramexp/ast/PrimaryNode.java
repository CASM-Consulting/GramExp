package uk.ac.susx.tag.gramexp.ast;

/**
 * Created by simon on 27/05/16.
 */
public class PrimaryNode<T extends SuperNode> extends SuperNode {

    public PrimaryNode(T node) {
        super(node);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
