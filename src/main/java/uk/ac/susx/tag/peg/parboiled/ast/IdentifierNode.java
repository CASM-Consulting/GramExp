package uk.ac.susx.tag.peg.parboiled.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simon on 27/05/16.
 */
public class IdentifierNode extends SuperNode {

    private final String id;

    public IdentifierNode(String id, ArgumentsNode args) {
        super(args);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
