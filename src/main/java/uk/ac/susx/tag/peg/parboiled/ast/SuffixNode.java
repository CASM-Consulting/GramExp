package uk.ac.susx.tag.peg.parboiled.ast;

/**
 * Created by simon on 27/05/16.
 */
public class SuffixNode extends SuperNode {

    private Literal optional;
    private SuperNode pp;

    public SuffixNode(PrimaryNode primaryNode, Literal optional, SuperNode pp) {
        super(primaryNode);

        this.optional = optional;
        this.pp = pp;
    }

    public Literal getOptional() {

        return optional;
    }

    public SuperNode getPP() {
        return pp;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
