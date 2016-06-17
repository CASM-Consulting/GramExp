package uk.ac.susx.tag.gramexp.ast;

/**
 * Created by simon on 27/05/16.
 */
public class EndOfFileNode<T extends SuperNode> extends SuperNode {

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
