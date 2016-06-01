package uk.ac.susx.tag.peg.parboiled.ast;

import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import org.parboiled.common.ImmutableList;

import java.util.List;

/**
 * Created by simon on 27/05/16.
 */
public class GrammarNode extends SuperNode {


    public GrammarNode(List<DefinitionNode> defs) {
        super(defs);
    }



    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
