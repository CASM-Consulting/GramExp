package uk.ac.susx.tag.gramexp;

import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressSubnodes;
import uk.ac.susx.tag.gramexp.matchers.StartOfLineMatcher;

import static org.parboiled.support.ParseTreeUtils.printNodeTree;

/**
 * Created by simon on 26/05/16.
 */
@BuildParseTree
abstract public class AbstractNlpParser extends CapturingParser {


    protected Rule NUM() {
        return CharRange('0', '9');
    }
    protected Rule CI() {
        return FirstOf(UPPER(), LOWER());
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

    public Rule S() {
        return AnyOf(new char[]{' ', '\t'});
    }

    @SuppressSubnodes
    public Rule Nl() {
        return Sequence(Optional('\r'), Ch('\n'));
    }

    protected Rule W() {
        return FirstOf(
                CharRange('\u0000', '\u001F'),
//                CharRange('\u0030', '\u0039'),
                CharRange('\u0041', '\u005A'),
                CharRange('\u0061', '\u007A'),
                CharRange('\u007F', '\u009F'),
                CharRange('\u00C0', '\u1FFF'),
                CharRange('\u2070', '\u2DFF'),
                CharRange('\u2E80', '\uFFCF')
        );
//        return Sequence(TestNot(FirstOf(PUNCT(), S(), Nl())), ANY);
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
    public Rule Ic(String str) {
        return IgnoreCase(str);
    }
    @SuppressSubnodes
    public Rule Ic(char... str) {
        return IgnoreCase(str);
    }

}
