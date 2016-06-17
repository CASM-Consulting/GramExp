package uk.ac.susx.tag.gramexp.ast;

import java.util.List;

/**
 * Created by simon on 27/05/16.
 */
public class ExpressionNode extends SuperNode {

    public ExpressionNode(List<SequenceNode> seqs) {
        super(seqs);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
