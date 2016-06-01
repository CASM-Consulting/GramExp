package uk.ac.susx.tag.peg.parboiled.ast;

/**
 * Created by simon on 27/05/16.
 */
public class PrefixNode extends SuperNode {

    private final Literal optional;

    public PrefixNode(Literal optional, SuffixNode suffixNode)
    {
        super(suffixNode);
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
