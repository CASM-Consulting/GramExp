package uk.ac.susx.tag.gramexp.ast;

import java.util.List;

/**
 * Created by simon on 27/05/16.
 */
public class ArgumentsNode<T extends SuperNode> extends SuperNode {

    public ArgumentsNode(List<T> nodes) {
        super(nodes);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
