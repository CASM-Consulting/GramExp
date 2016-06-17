package uk.ac.susx.tag.gramexp.ast;

/**
 * Created by simon on 27/05/16.
 */
public class CharNode extends SuperNode {

    private final String chr;

    public CharNode(String chr) {
        this.chr = chr;
    }

    public String getChar() {
        return chr;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
