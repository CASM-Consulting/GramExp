package uk.ac.susx.tag.peg.parboiled.ast;

/**
 * Created by simon on 27/05/16.
 */
public class EndOfFileNode<T extends SuperNode> extends SuperNode {

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
