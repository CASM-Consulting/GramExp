package uk.ac.susx.tag.peg.parboiled;

import org.junit.Test;
import org.parboiled.Parboiled;

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

        String clsName = "Test";


        AstToJava astToJava = new AstToJava(clsName);
        String java = astToJava.toJava(parser.parse(peg));

        assertEquals(expected, java);
    }

    private static final String expected = "import org.parboiled.BaseParser;\n" +
            "import org.parboiled.Rule;\n" +
            "import org.parboiled.annotations.BuildParseTree;\n" +
            "@BuildParseTree\n" +
            "public class Test extends BaseParser<Object> {\n" +
            "public Rule Grammar( ) {\n" +
            "  return Sequence(Spacing(),Optional(Mode()),OneOrMore(Definition()),EndOfFile());\n" +
            " }\n" +
            " \n" +
            " public Rule Mode( ) {\n" +
            "  return Sequence(String('/'),OneOrMore(Sequence(TestNot(String('/')),Char())),String('/'),Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule Definition( ) {\n" +
            "  return Sequence(Identifier(),LEFTARROW(),Expression());\n" +
            " }\n" +
            " \n" +
            " public Rule Expression( ) {\n" +
            "  return Sequence(Sequence(),ZeroOrMore(Sequence(SLASH(),Sequence())));\n" +
            " }\n" +
            " \n" +
            " public Rule Sequence( ) {\n" +
            "  return ZeroOrMore(Prefix());\n" +
            " }\n" +
            " \n" +
            " public Rule Prefix( ) {\n" +
            "  return Sequence(Optional(FirstOf(AND(),NOT())),Suffix());\n" +
            " }\n" +
            " \n" +
            " public Rule Suffix( ) {\n" +
            "  return Sequence(Primary(),Optional(FirstOf(QUESTION(),STAR(),PLUS())));\n" +
            " }\n" +
            " \n" +
            " public Rule Primary( ) {\n" +
            "  return FirstOf(Sequence(Identifier(),TestNot(LEFTARROW())),Sequence(OPEN(),Expression(),CLOSE()),Literal(),Class(),DOT(),EMPTY(),NOTHING());\n" +
            " }\n" +
            " \n" +
            " public Rule Identifier( ) {\n" +
            "  return Sequence(IdentStart(),ZeroOrMore(IdentCont()),Optional(Arguments()),Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule IdentStart( ) {\n" +
            "  return FirstOf('_',CharRange('a','z'),CharRange('A','Z'));\n" +
            " }\n" +
            " \n" +
            " public Rule IdentCont( ) {\n" +
            "  return FirstOf(IdentStart(),CharRange('0','9'));\n" +
            " }\n" +
            " \n" +
            " public Rule Arguments( ) {\n" +
            "  return Sequence(AOPEN(),OneOrMore(Identifier()),ACLOSE());\n" +
            " }\n" +
            " \n" +
            " public Rule Literal( ) {\n" +
            "  return FirstOf(Sequence(\"'\",ZeroOrMore(Sequence(TestNot(\"'\"),Char())),\"'\",Spacing()),Sequence('\"',ZeroOrMore(Sequence(TestNot('\"'),Char())),'\"',Spacing()));\n" +
            " }\n" +
            " \n" +
            " public Rule Class( ) {\n" +
            "  return Sequence(String('['),ZeroOrMore(Sequence(TestNot(String(']')),Range())),String(']'),Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule Range( ) {\n" +
            "  return FirstOf(Sequence(Char(),String('-'),Char()),Char());\n" +
            " }\n" +
            " \n" +
            " public Rule Char( ) {\n" +
            "  return FirstOf(Sequence(String(\"\\\\\"),AnyOf(\"nrt'\\\"[]\\\\\")),Sequence(String(\"\\\\\"),CharRange('0','2'),CharRange('0','7'),CharRange('0','7')),Sequence(String(\"\\\\\"),CharRange('0','7'),Optional(CharRange('0','7'))),Sequence(TestNot(String(\"\\\\\")),ANY));\n" +
            " }\n" +
            " \n" +
            " public Rule LEFTARROW( ) {\n" +
            "  return Sequence(String(\"<-\"),Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule SLASH( ) {\n" +
            "  return Sequence(String('/'),Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule AND( ) {\n" +
            "  return Sequence(String('&'),Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule NOT( ) {\n" +
            "  return Sequence(String('!'),Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule QUESTION( ) {\n" +
            "  return Sequence(String('?'),Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule STAR( ) {\n" +
            "  return Sequence(String('*'),Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule PLUS( ) {\n" +
            "  return Sequence(String('+'),Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule OPEN( ) {\n" +
            "  return Sequence(String('('),Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule CLOSE( ) {\n" +
            "  return Sequence(String(')'),Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule DOT( ) {\n" +
            "  return Sequence(String('.'),Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule EMPTY( ) {\n" +
            "  return Sequence(String('_'),Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule NOTHING( ) {\n" +
            "  return Sequence(String('~'),Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule COPEN( ) {\n" +
            "  return Sequence(String('{'),Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule CCLOSE( ) {\n" +
            "  return Sequence(String('}'),Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule AOPEN( ) {\n" +
            "  return Sequence(String('<'),Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule ACLOSE( ) {\n" +
            "  return Sequence(String('>'),Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule Spacing( ) {\n" +
            "  return ZeroOrMore(FirstOf(Space(),Comment()));\n" +
            " }\n" +
            " \n" +
            " public Rule Comment( ) {\n" +
            "  return Sequence(String('#'),ZeroOrMore(Sequence(TestNot(EndOfLine()),ANY)),EndOfLine());\n" +
            " }\n" +
            " \n" +
            " public Rule Space( ) {\n" +
            "  return FirstOf(String(' '),String(\"\\t\"),EndOfLine());\n" +
            " }\n" +
            " \n" +
            " public Rule EndOfLine( ) {\n" +
            "  return FirstOf(String(\"\\r\\n\"),String(\"\\n\"),String(\"\\r\"));\n" +
            " }\n" +
            " \n" +
            " public Rule EndOfFile( ) {\n" +
            "  return TestNot(ANY);\n" +
            " }\n" +
            " \n" +
            " }";
}