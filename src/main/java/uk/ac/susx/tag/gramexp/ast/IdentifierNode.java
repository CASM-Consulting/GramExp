package uk.ac.susx.tag.gramexp.ast;

import org.parboiled.common.Tuple2;

/**
 * Created by simon on 27/05/16.
 */
public class IdentifierNode extends SuperNode {

    private final String id;
    private final boolean argument;
    private final boolean definition;

    public IdentifierNode(String id, ArgumentsNode args, Tuple2<Boolean,Boolean> argdef) {
        super(args);
        this.id = id;
        this.argument = argdef.a;
        this.definition = argdef.b;
    }

    public String getId() {
        return id;
    }
    public boolean isArgument() {
        return this.argument;
    }

    public boolean isDefinition() {
        return this.definition;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
