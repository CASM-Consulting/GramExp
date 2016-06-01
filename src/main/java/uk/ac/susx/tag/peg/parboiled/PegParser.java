package uk.ac.susx.tag.peg.parboiled;

import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.common.ImmutableList;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.Var;
import uk.ac.susx.tag.peg.parboiled.ast.*;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.parboiled.support.ParseTreeUtils.printNodeTree;


/**
 * Created by simon on 26/05/16.
 */
@BuildParseTree
public class PegParser extends BaseParser<Object> {


    public GrammarNode parse(String input) {

        ReportingParseRunner runner = new ReportingParseRunner(Grammar());

        ParsingResult<Node> result = runner.run(input);

        GrammarNode grammarNode = (GrammarNode) result.resultValue;;

        return grammarNode;
    }

    public Rule Grammar () {
        List<DefinitionNode> defs = new ArrayList<>();
        return Sequence(Spacing(), ZeroOrMore(Definition(), defs.add((DefinitionNode)pop())), push(new GrammarNode(defs)), EOI);
    }

    public Rule Definition() {
        return Sequence(
                Identifier(),
                LEFTARROW(),
                Expression(), push(new DefinitionNode((ExpressionNode) pop(), (IdentifierNode) pop()))
        );
    }

    Object _pop() {
        Object pop = pop();
        return pop;
    }

    public Rule Expression() {
        Var<List<SequenceNode>> seqs = new Var<>(new ArrayList<>());
        return Sequence(Sequence(), seqs.get().add((SequenceNode) pop()),
                ZeroOrMore(Sequence(SLASH(), Sequence()), seqs.get().add((SequenceNode) pop())), push(new ExpressionNode(seqs.get())));
    }

    public Rule Sequence() {
        Var<List<PrefixNode>> pres = new Var<>(new ArrayList<>());
        return Sequence(ZeroOrMore(Prefix(), pres.get().add((PrefixNode) pop())), push(new SequenceNode(pres.get())));
    }

    public Rule Prefix() {
        Var<Literal> optional = new Var<>();
        return Sequence(
                Optional(
                        Sequence(FirstOf(
                                AND(),
                                NOT()
                        ), optional.set((Literal) pop()))
                ), Suffix(), push(new PrefixNode(optional.get(), (SuffixNode)pop()))
        );
    }

    public Rule Suffix() {
        Var<Literal> optional = new Var<>();
        return Sequence(Primary(),
                Optional(
                        Sequence(FirstOf(
                                QUESTION(),
                                STAR(),
                                PLUS()
                        ), optional.set((Literal) pop()))
                ), push(new SuffixNode((PrimaryNode)pop(), optional.get()))
        );
    }

    public Rule Primary() {
        return Sequence(FirstOf(
                Sequence(Identifier(), TestNot(LEFTARROW())),
                Sequence(OPEN(), Expression(), CLOSE()),
                Literal(),
                Class(),
                DOT()
        ), push(new PrimaryNode((SuperNode)pop())));
    }

    @SuppressSubnodes
    public Rule Identifier() {
        return Sequence(IdentStart(), push(match()), ZeroOrMore(IdentCont()), push(new IdentifierNode((String)pop(), match())), Spacing());
    }

    public Rule IdentStart() {
        return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), '_');
    }

    public Rule IdentCont() {
        return FirstOf(IdentStart(), CharRange('0', '9'));
    }

    public Rule Literal() {
		return Sequence(FirstOf(
                Sequence('\'', ZeroOrMore(Sequence(TestNot(AnyOf("'")), ANY)), '\'', Spacing()),
                Sequence('"', ZeroOrMore(Sequence(TestNot(AnyOf("\"")), ANY)), '"', Spacing())
        ),push(new LiteralNode(match())));
    }

    public Rule Class() {
        Var<List<String>> range = new Var<>(new ArrayList<>());
        return Sequence('[', ZeroOrMore(Sequence(TestNot(']'), Range(), range.get().add((String)pop()))), ']', push(new ClassNode(range.get())), Spacing());
    }

    public Rule Range() {
        return Sequence(FirstOf(Sequence(Char(), '-', Char()), Char()), push(match()));
    }

    @SuppressSubnodes
    public Rule Char() {
        return FirstOf(
                Sequence('\\', AnyOf("'\"[]\\")),
                Sequence('\\', CharRange('0','2'), CharRange('0','7'), CharRange('0','7')),
                Sequence('\\', CharRange('0','7'), Optional(CharRange('0','7'))),
                Sequence(TestNot('\\'), ANY)
        );
    }

    @SuppressSubnodes
    public Rule LEFTARROW() {
        return Sequence(String("<-"), Spacing());
    }

    @SuppressSubnodes
    public Rule SLASH() {
		return Sequence('/', /*push(new Literal.SLASHNode()),*/ Spacing());
    }

    @SuppressSubnodes
    public Rule AND() {
		return Sequence('&', push(new Literal.ANDNode()),  Spacing());
    }

    @SuppressSubnodes
    public Rule NOT() {
		return Sequence('!', push(new Literal.NOTNode()), Spacing());
    }

    @SuppressSubnodes
    public Rule QUESTION() {
		return Sequence('?', push(new Literal.QUESTIONNode()), Spacing());
    }

    @SuppressSubnodes
    public Rule STAR() {
		return Sequence('*', push(new Literal.STARNode()), Spacing());
    }

    @SuppressSubnodes
    public Rule PLUS() {
		return Sequence('+', push(new Literal.PLUSNode()), Spacing());
    }

    @SuppressSubnodes
    public Rule OPEN() {

		return Sequence('('/*,push(new Literal.OPENNode())*/,Spacing());
    }

    @SuppressSubnodes
    public Rule CLOSE() {
		return Sequence(')'/*,push(new Literal.CLOSENode())*/, Spacing());
    }

    @SuppressSubnodes
    public Rule DOT() {
		return Sequence('.',  push(new Literal.DOTNode()), Spacing());
    }

    @SuppressSubnodes
    public Rule Spacing() {
        return ZeroOrMore(FirstOf(Space(), Comment()));
    }

    @SuppressSubnodes
    public Rule Space() {
        return FirstOf(' ', '\t', EOL());
    }

    @SuppressSubnodes
    public Rule Comment() {
		return Sequence("#", ZeroOrMore(TestNot(EOL()), ANY), EOL());
    }

    @SuppressSubnodes
    public Rule EOL() {
        return FirstOf(Sequence('\r', '\n'), Ch('\r'), Ch('\n'));
    }

    @SuppressSubnodes
    public Rule EOF() {
        return TestNot(ANY);
    }


    public static void main(String[] args) throws Exception {
        PegParser parser = Parboiled.createParser(PegParser.class);

        ReportingParseRunner runner = new ReportingParseRunner(parser.Grammar());

        String input = "";

        try (
                BufferedReader reader = Files.newBufferedReader(Paths.get("src/main/resources/peg.peg"))
        ) {
            String line = "";
            while ((line = reader.readLine())!=null) {

                input += line + "\n";
            }
        }

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
}
