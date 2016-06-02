package uk.ac.susx.tag.peg.parboiled;

import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

import static org.parboiled.support.ParseTreeUtils.printNodeTree;

/**
 * Created by simon on 26/05/16.
 */
@BuildParseTree
abstract public class AbstractNlpParser extends BaseParser<Object> {


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
        return OneOrMore(FirstOf(W(), PUNCT(), S()));
    }

    @SuppressSubnodes
    public Rule Text(Rule exclude) {
        return OneOrMore(FirstOf(TestNot(exclude), NOTHING), FirstOf(W(), PUNCT(), S()));
    }

    @SuppressSubnodes
    public Rule Nl() {
        return Sequence(Optional('\r'), Ch('\n'));
    }


    abstract public Rule Begin();


    public static void test(Class<? extends AbstractNlpParser> cls, String input) {
        AbstractNlpParser parser = Parboiled.createParser(cls);

        ReportingParseRunner runner = new ReportingParseRunner(parser.Begin());

        ParsingResult<?> result = runner.run(input);

        if (!result.parseErrors.isEmpty())
            System.out.println(ErrorUtils.printParseError(result.parseErrors.get(0)));
        else {
            System.out.println(printNodeTree(result) + '\n');

//            long tic = new Date().getTime();
//            for(int i = 0; i < 1000; ++i) {
//                runner.run(input);
//            }
//            long toc = new Date().getTime();
//            System.out.println(""+(toc-tic));
        }

    }

    public static void main(String[] args) {
        class Test extends AbstractNlpParser {
            @Override
            public Rule Begin() {
                return Text(Ch('A'));
            }
        };

        System.out.println(Character.UnicodeBlock.GENERAL_PUNCTUATION);
    }
}
