package uk.ac.susx.tag.gramexp.ast;

/**
 * Created by simon on 27/05/16.
 */
public class DefinitionNode extends SuperNode {

    private final IdentifierNode id;

    public DefinitionNode(ExpressionNode expressionNode, IdentifierNode id)
    {
        super(expressionNode);
        this.id = id;
    }

    public String name() {
        return id.getId();
    }
    public IdentifierNode getIdentifier() {
        return id;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
