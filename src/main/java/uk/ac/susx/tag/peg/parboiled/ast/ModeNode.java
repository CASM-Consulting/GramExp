package uk.ac.susx.tag.peg.parboiled.ast;

/**
 * Created by simon on 27/05/16.
 */
public class ModeNode extends SuperNode {

    private final String mode;

    public ModeNode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
