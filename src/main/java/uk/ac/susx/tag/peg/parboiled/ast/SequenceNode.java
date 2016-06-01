package uk.ac.susx.tag.peg.parboiled.ast;

import java.util.List;

/**
 * Created by simon on 27/05/16.
 */
public class SequenceNode extends SuperNode {

    public SequenceNode(List<PrefixNode> prefs) {
        super(prefs);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
