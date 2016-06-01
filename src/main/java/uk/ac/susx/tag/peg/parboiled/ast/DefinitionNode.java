package uk.ac.susx.tag.peg.parboiled.ast;

/**
 * Created by simon on 27/05/16.
 */
public class DefinitionNode extends SuperNode {

    private final String id;

    public DefinitionNode(ExpressionNode expressionNode, IdentifierNode id)
    {
        super(expressionNode);
        this.id = id.getId();
    }

    public String name() {
        return id;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
