package uk.ac.susx.tag.gramexp.ast;

import java.util.List;

/**
 * Created by simon on 27/05/16.
 */
public class ClassNode extends SuperNode {

    private final List<String> cls;

    public ClassNode(List<String> cls) {
        this.cls = cls;
    }

    public List<String> getCls() {
        return cls;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
