package uk.ac.susx.tag.gramexp;

import org.junit.Test;
import org.parboiled.Parboiled;
import uk.ac.susx.tag.gramexp.AstToJava;
import uk.ac.susx.tag.gramexp.PegParser;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * Created by simon on 07/06/16.
 */
public class AstTojavaTest {

    @Test
    public void testToJava() throws Exception {
        PegParser parser  = Parboiled.createParser(PegParser.class);

        String peg = "";

        try (
                BufferedReader reader = Files.newBufferedReader(Paths.get("src/main/resources/peg.peg"))
        ) {
            String line = "";
            while ((line = reader.readLine())!=null) {

                peg += line + "\n";
            }
        }

        String clsName = "GramExp";


        AstToJava astToJava = new AstToJava(clsName);
        String java = astToJava.toJava(parser.parse(peg));

        assertEquals(expected, java);
    }

    private static final String expected = "import org.parboiled.Action;\n" +
            "import org.parboiled.Context;\n" +
            "import org.parboiled.Rule;\n" +
            "import org.parboiled.annotations.BuildParseTree;\n" +
            "import java.util.Set;\n" +
            "import java.util.HashSet;\n" +
            "import uk.ac.susx.tag.gramexp.CapturingParser;\n" +
            "import uk.ac.susx.tag.gramexp.Capture;\n" +
            "@BuildParseTree\n" +
            "public class GramExp extends CapturingParser<Object> {\n" +
            "\n" +
            " public Rule Grammar( ) {\n" +
            "  return toRule(Sequence(Spacing(),Optional(Mode()),OneOrMore(Definition()),EndOfFile()));\n" +
            " }\n" +
            " \n" +
            " public Rule Mode( ) {\n" +
            "  return toRule(Sequence('/',OneOrMore(Sequence(TestNot('/'),Char())),'/',Spacing()));\n" +
            " }\n" +
            " \n" +
            " public Rule Definition( ) {\n" +
            "  return toRule(Sequence(Identifier(),LEFTARROW(),Expression()));\n" +
            " }\n" +
            " \n" +
            " public Rule Expression( ) {\n" +
            "  return toRule(Sequence(Sequence(),ZeroOrMore(Sequence(SLASH(),Sequence()))));\n" +
            " }\n" +
            " \n" +
            " public Rule Sequence( ) {\n" +
            "  return toRule(ZeroOrMore(Prefix()));\n" +
            " }\n" +
            " \n" +
            " public Rule Prefix( ) {\n" +
            "  return toRule(FirstOf(Sequence(Optional(FirstOf(AND(),NOT())),Suffix()),Sequence(AOPEN(),Suffix(),Literal(),ACLOSE())));\n" +
            " }\n" +
            " \n" +
            " public Rule Suffix( ) {\n" +
            "  return toRule(Sequence(Primary(),Optional(FirstOf(QUESTION(),STAR(),PLUS())),Optional(FirstOf(PUSH(),POP()))));\n" +
            " }\n" +
            " \n" +
            " public Rule Primary( ) {\n" +
            "  return toRule(FirstOf(Sequence(Identifier(),TestNot(LEFTARROW())),Sequence(OPEN(),Expression(),CLOSE()),Literal(),Class(),DOT(),EMPTY(),NOTHING()));\n" +
            " }\n" +
            " \n" +
            " public Rule Identifier( ) {\n" +
            "  return toRule(Sequence(IdentStart(),ZeroOrMore(IdentCont()),Optional(Arguments()),Spacing()));\n" +
            " }\n" +
            " \n" +
            " public Rule IdentStart( ) {\n" +
            "  return toRule(FirstOf('_'CharRange('a','z'),CharRange('A','Z')));\n" +
            " }\n" +
            " \n" +
            " public Rule IdentCont( ) {\n" +
            "  return toRule(FirstOf(IdentStart(),CharRange('0','9')));\n" +
            " }\n" +
            " \n" +
            " public Rule Arguments( ) {\n" +
            "  return toRule(Sequence(AOPEN(),OneOrMore(FirstOf(Identifier(),Literal())),ACLOSE()));\n" +
            " }\n" +
            " \n" +
            " public Rule Literal( ) {\n" +
            "  return toRule(FirstOf(Sequence(\"'\",ZeroOrMore(Sequence(TestNot(\"'\"),Char())),\"'\",Spacing()),Sequence('\"',ZeroOrMore(Sequence(TestNot('\"'),Char())),'\"',Spacing())));\n" +
            " }\n" +
            " \n" +
            " public Rule Class( ) {\n" +
            "  return toRule(Sequence('[',ZeroOrMore(Sequence(TestNot(']'),Range())),']',Spacing()));\n" +
            " }\n" +
            " \n" +
            " public Rule Range( ) {\n" +
            "  return toRule(FirstOf(Sequence(Char(),'-',TestNot(']'),Char()),Char()));\n" +
            " }\n" +
            " \n" +
            " public Rule Char( ) {\n" +
            "  return toRule(FirstOf(Sequence(\"\\\\\",AnyOf(\"nrt'\\\"[]\\\\\"),),Sequence(\"\\\\\",CharRange('0','2'),CharRange('0','7'),CharRange('0','7')),Sequence(\"\\\\\",CharRange('0','7'),Optional(CharRange('0','7'))),Sequence(TestNot(\"\\\\\"),ANY)));\n" +
            " }\n" +
            " \n" +
            " public Rule LEFTARROW( ) {\n" +
            "  return toRule(Sequence(\"<-\",Spacing()));\n" +
            " }\n" +
            " \n" +
            " public Rule SLASH( ) {\n" +
            "  return toRule(Sequence('/',Spacing()));\n" +
            " }\n" +
            " \n" +
            " public Rule AND( ) {\n" +
            "  return toRule(Sequence('&',Spacing()));\n" +
            " }\n" +
            " \n" +
            " public Rule NOT( ) {\n" +
            "  return toRule(Sequence('!',Spacing()));\n" +
            " }\n" +
            " \n" +
            " public Rule QUESTION( ) {\n" +
            "  return toRule(Sequence('?',Spacing()));\n" +
            " }\n" +
            " \n" +
            " public Rule STAR( ) {\n" +
            "  return toRule(Sequence('*',Spacing()));\n" +
            " }\n" +
            " \n" +
            " public Rule PLUS( ) {\n" +
            "  return toRule(Sequence('+',Spacing()));\n" +
            " }\n" +
            " \n" +
            " public Rule OPEN( ) {\n" +
            "  return toRule(Sequence('(',Spacing()));\n" +
            " }\n" +
            " \n" +
            " public Rule CLOSE( ) {\n" +
            "  return toRule(Sequence(')',Spacing()));\n" +
            " }\n" +
            " \n" +
            " public Rule DOT( ) {\n" +
            "  return toRule(Sequence('.',Spacing()));\n" +
            " }\n" +
            " \n" +
            " public Rule EMPTY( ) {\n" +
            "  return toRule(Sequence(':',Spacing()));\n" +
            " }\n" +
            " \n" +
            " public Rule NOTHING( ) {\n" +
            "  return toRule(Sequence('~',Spacing()));\n" +
            " }\n" +
            " \n" +
            " public Rule COPEN( ) {\n" +
            "  return toRule(Sequence('{',Spacing()));\n" +
            " }\n" +
            " \n" +
            " public Rule CCLOSE( ) {\n" +
            "  return toRule(Sequence('}',Spacing()));\n" +
            " }\n" +
            " \n" +
            " public Rule AOPEN( ) {\n" +
            "  return toRule(Sequence('<',Spacing()));\n" +
            " }\n" +
            " \n" +
            " public Rule ACLOSE( ) {\n" +
            "  return toRule(Sequence('>',Spacing()));\n" +
            " }\n" +
            " \n" +
            " public Rule PUSH( ) {\n" +
            "  return toRule(Sequence(\"push\",Spacing()));\n" +
            " }\n" +
            " \n" +
            " public Rule POP( ) {\n" +
            "  return toRule(Sequence(\"pop\",Spacing()));\n" +
            " }\n" +
            " \n" +
            " public Rule Spacing( ) {\n" +
            "  return toRule(ZeroOrMore(FirstOf(Space(),Comment())));\n" +
            " }\n" +
            " \n" +
            " public Rule Comment( ) {\n" +
            "  return toRule(Sequence('#',ZeroOrMore(Sequence(TestNot(EndOfLine()),ANY)),EndOfLine()));\n" +
            " }\n" +
            " \n" +
            " public Rule Space( ) {\n" +
            "  return toRule(FirstOf(' ',\"\\t\",EndOfLine()));\n" +
            " }\n" +
            " \n" +
            " public Rule EndOfLine( ) {\n" +
            "  return toRule(FirstOf(\"\\r\\n\",\"\\n\",\"\\r\"));\n" +
            " }\n" +
            " \n" +
            " public Rule EndOfFile( ) {\n" +
            "  return toRule(TestNot(ANY));\n" +
            " }\n" +
            " \n" +
            " \n" +
            " {\n" +
            " }\n" +
            " }";
}