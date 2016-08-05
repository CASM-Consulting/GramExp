package uk.ac.susx.tag.gramexp;

import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressSubnodes;

import static org.parboiled.support.ParseTreeUtils.printNodeTree;

/**
 * Created by simon on 26/05/16.
 */
@BuildParseTree
abstract public class AbstractNlpParser extends CapturingParser {


    public Rule S() {
        return AnyOf(" \t");
    }
    protected Rule NUM() {
        return CharRange('0', '9');
    }
    protected Rule UPPER() {
        return FirstOf(
                CharRange('A', 'Z'),
                CharRange('\u00C0', '\u00DE')
        );
    }
    protected Rule LOWER() {
        return FirstOf(
                CharRange('a', 'z'),
                CharRange('\u00DF', '\u00FF')
        );
    }
    protected Rule CI() {
        return FirstOf(UPPER(), LOWER());
    }
    protected Rule PUNCT() {
        return FirstOf(
                CharRange('\u0021', '\u002F'),
                CharRange('\u003A', '\u0040'),
                CharRange('\u005B', '\u0060'),
                CharRange('\u007B', '\u007E'),
                CharRange('\u00A0', '\u00BF'),
                CharRange('\u2000', '\u206F')
        );
    }
    protected Rule W() {
        return FirstOf(CI(), NUM());
    }
    @SuppressSubnodes
    public Rule Text() {
        return OneOrMore(FirstOf(W(), PUNCT(), S(), Nl()));
    }

    @SuppressSubnodes
    public Rule Text(Object until) {
        return OneOrMore(FirstOf(TestNot(until), NOTHING), FirstOf(W(), PUNCT(), S(), Nl()));
    }

    @SuppressSubnodes
    public Rule Nl() {
        return Sequence(Optional('\r'), Ch('\n'));
    }

    @SuppressSubnodes
    public Rule Ic(String str) {
        return IgnoreCase(str);
    }
    @SuppressSubnodes
    public Rule Ic(char... str) {
        return IgnoreCase(str);
    }

}
