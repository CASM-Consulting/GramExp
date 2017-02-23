package uk.ac.susx.tag.gramexp;

import org.parboiled.*;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.common.Tuple2;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.Var;
import uk.ac.susx.tag.gramexp.ast.*;
import uk.ac.susx.tag.gramexp.ast.Node;

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

        GrammarNode grammarNode = (GrammarNode) result.resultValue;

        if(grammarNode == null){
            throw new ParseException(ErrorUtils.printParseError(result.parseErrors.get(0)));
        }

        return grammarNode;
    }

    public Rule Grammar () {
        Var<List<DefinitionNode>> defs = new Var<>(new ArrayList<>());
        Var<ModeNode> optional = new Var<>();
        return Sequence(Spacing(), Optional(Mode(), optional.set((ModeNode)pop())), OneOrMore(Definition(), defs.get().add((DefinitionNode)pop())), push(new GrammarNode(defs.get(), optional.get())), EOI);
    }

    public Rule Mode() {
        return Sequence(Sequence('/',OneOrMore(Sequence(TestNot(String('/')),Char())),'/'),push(new ModeNode(match())), Spacing());
    }

    public Rule Definition() {
        return Sequence(
                push(new Tuple2(false, true)),Identifier(),
                LEFTARROW(),
                Expression(), push(new DefinitionNode((ExpressionNode) pop(), (IdentifierNode) pop()))
        );
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
        Var<Literal> optionalLiteral = new Var<>();
        Var<CaptureNode> optionalCapture = new Var<>();
        return Sequence(
                Optional(FirstOf(AND(), NOT()), optionalLiteral.set((Literal) pop())),
                FirstOf(
                        StartOfLine(),
                        Suffix(),
                        Sequence(AOPEN(), Suffix(), Literal(), ACLOSE(), optionalCapture.set(new CaptureNode((LiteralNode) pop())))
                ),
                push(new PrefixNode(optionalCapture.get(), optionalLiteral.get(), (SuperNode) pop()))
        );
    }

    public Rule Suffix() {
        Var<Literal> optional = new Var<>();
        Var<SuperNode> pp = new Var<>();
        return Sequence(Primary(),
                Optional(
                        Sequence(FirstOf(
                                QUESTION(),
                                STAR(),
                                PLUS()
                        ), optional.set((Literal) pop()))
                ),
                Optional(
                        Sequence(FirstOf(
                                PUSH(),
                                POP()
                        ), pp.set((SuperNode)pop()))
                ),
                push(new SuffixNode((PrimaryNode)pop(), optional.get(), pp.get()))
        );
    }

    public Rule Primary() {
        return Sequence(FirstOf(
                Sequence(push(new Tuple2(false,false)), Identifier(), TestNot(LEFTARROW())),
                Sequence(OPEN(), Expression(), CLOSE()),
                Literal(),
                Class(),
                DOT(),
                EMPTY(),
                NOTHING(),
                EndOfFile()
                ), push(new PrimaryNode((SuperNode)pop())));
    }

    @SuppressSubnodes
    public Rule Identifier() {
        Var<StringBuilder> id = new Var<>(new StringBuilder());
        Var<ArgumentsNode> optional = new Var<>();

        return Sequence(
                IdentStart(), (id.get().append(match())!=null),
                ZeroOrMore(IdentCont(), (id.get().append(match())!=null)),
                Optional(Arguments(), optional.set((ArgumentsNode)pop())),
                push(new IdentifierNode(id.get().toString(), optional.get(), (Tuple2)pop())),
                Spacing()
        );
    }

    public Rule IdentStart() {
        return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), '_');
    }

    public Rule IdentCont() {
        return FirstOf(IdentStart(), CharRange('0', '9'));
    }

    public Rule Arguments() {
        Var<List<SuperNode>> args = new Var<>(new ArrayList<>());
        return Sequence(
                AOPEN(),
                OneOrMore(FirstOf(Sequence(push(new Tuple2(true, ((Tuple2)peek()).b)),Identifier()), Literal()), args.get().add((SuperNode)pop())),
                ACLOSE(),
                push(new ArgumentsNode<>(args.get()))
        );
    }

    public Rule Literal() {
        Var<StringBuilder> lit = new Var<>(new StringBuilder());
		return Sequence(FirstOf(
                Sequence('\'', ZeroOrMore(Sequence(TestNot(AnyOf("'")), ANY, (lit.get().append(match())!=null)) ), '\'', Spacing()),
                Sequence('"', ZeroOrMore(Sequence(TestNot(AnyOf("\"")), ANY, (lit.get().append(match())!=null)) ), '"', Spacing())
        ),push(new LiteralNode(lit.get().toString())));
    }

    public Rule Class() {
        Var<List<String>> range = new Var<>(new ArrayList<>());
        return Sequence('[', ZeroOrMore(Sequence(TestNot(']'), Range(), range.get().add((String)pop()))), ']', push(new ClassNode(range.get())), Spacing());
    }

    public Rule Range() {
        return Sequence(FirstOf(Sequence(Char(), '-', TestNot(']'), Char()), Char()), push(match()));
    }

    @SuppressSubnodes
    public Rule Char() {
        return FirstOf(
                Sequence("\\", AnyOf("nrt'\"[]\\")),
                Sequence("\\", CharRange('0','2'), CharRange('0','7'), CharRange('0','7')),
                Sequence("\\", CharRange('0','7'), Optional(CharRange('0','7'))),
                Sequence(TestNot("\\"), ANY)
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
    public Rule AOPEN() {
        return Sequence('<'/*,push(new Literal.OPENNode())*/,Spacing());
    }

    @SuppressSubnodes
    public Rule ACLOSE() {
        return Sequence('>'/*,push(new Literal.CLOSENode())*/, Spacing());
    }

    @SuppressSubnodes
    public Rule COPEN( ) {
        return Sequence('{',Spacing());
    }

    @SuppressSubnodes
    public Rule CCLOSE( ) {
        return Sequence('}',Spacing());
    }


    public Rule PUSH( ) {
        return toRule(Sequence("push", push(new PushNode()), Spacing()));
    }

    public Rule POP( ) {
        return toRule(Sequence("pop", push(new PopNode()), Spacing()));
    }

    @SuppressSubnodes
    public Rule DOT() {
        return Sequence('.',  push(new Literal.DOTNode()), Spacing());
    }

    @SuppressSubnodes
    public Rule EMPTY() {
        return Sequence(':',  push(new Literal.EMPTYNode()), Spacing());
    }

    @SuppressSubnodes
    public Rule NOTHING() {
        return Sequence('~',  push(new Literal.NOTHINGNode()), Spacing());
    }

    @SuppressSubnodes
    public Rule Spacing() {
        return ZeroOrMore(FirstOf(Space(), Comment()));
    }

    @SuppressSubnodes
    public Rule Space() {
        return FirstOf(' ', '\t', EndOfLine());
    }

    @SuppressSubnodes
    public Rule Comment() {
		return Sequence("#", ZeroOrMore(TestNot(EndOfLine()), ANY), EndOfLine());
    }

    @SuppressSubnodes
    public Rule EndOfLine() {
        return FirstOf(Sequence('\r', '\n'), Ch('\r'), Ch('\n'));
    }

    @SuppressSubnodes
    public Rule EndOfFile() {
        return Sequence("$", push(new EndOfFileNode<>()), Spacing());
    }

    @SuppressSubnodes
    public Rule StartOfLine() {
        return Sequence("^", push(new StartOfLineNode<>()), Spacing());
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

        }
    }
}
