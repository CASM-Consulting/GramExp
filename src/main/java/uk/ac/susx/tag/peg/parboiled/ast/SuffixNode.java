package uk.ac.susx.tag.peg.parboiled.ast;

/**
 * Created by simon on 27/05/16.
 */
public class SuffixNode extends SuperNode {

    private Literal optional;

    public SuffixNode(PrimaryNode primaryNode, Literal optional) {
        super(primaryNode);

        this.optional = optional;

    }

    public Literal getOptional() {
        return optional;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
