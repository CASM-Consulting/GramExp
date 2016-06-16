package uk.ac.susx.tag.peg.parboiled.ast;

/**
 * Created by simon on 27/05/16.
 */
public class PushNode extends SuperNode {

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
