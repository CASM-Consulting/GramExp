package uk.ac.susx.tag.gramexp.ast;

/**
 * Created by simon on 27/05/16.
 */
public class PrefixNode extends SuperNode {

    private final Literal optional;
    private final CaptureNode capture;

    public PrefixNode(CaptureNode capture, Literal optional, SuffixNode suffixNode)
    {
        super(suffixNode);
        this.optional = optional;
        this.capture = capture;
    }

    public Literal getOptional() {
        return optional;
    }

    public CaptureNode getCapture() {
        return capture;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
