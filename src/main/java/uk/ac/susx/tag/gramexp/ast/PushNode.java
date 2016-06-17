package uk.ac.susx.tag.gramexp.ast;

/**
 * Created by simon on 27/05/16.
 */
public class PushNode extends SuperNode {

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
