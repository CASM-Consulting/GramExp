package uk.ac.susx.tag.gramexp.ast;

import java.util.List;

/**
 * Created by simon on 27/05/16.
 */
public class GrammarNode extends SuperNode {

    private ModeNode mode;

    public GrammarNode(List<DefinitionNode> defs, ModeNode modeNode) {
        super(defs);
        mode = modeNode;

    }

    public String getMode() {
        return mode == null ? "" : mode.getMode();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
