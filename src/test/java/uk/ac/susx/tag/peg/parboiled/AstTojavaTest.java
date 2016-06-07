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

        String clsName = "Peg";


        AstToJava astToJava = new AstToJava(clsName);
        String java = astToJava.toJava(parser.parse(peg));

        assertEquals(expected, java);
    }

    private static final String expected = "import org.parboiled.BaseParser;\n" +
            "import org.parboiled.Rule;\n" +
            "import org.parboiled.annotations.BuildParseTree;\n" +
            "@BuildParseTree\n" +
            "public class Peg extends BaseParser<Object> {\n" +
            "public Rule Grammar( ) {\n" +
            "  return Sequence(Spacing(),Optional(Mode()),OneOrMore(Definition()),EndOfFile());\n" +
            " }\n" +
            " \n" +
            " public Rule Mode( ) {\n" +
            "  return Sequence('/',OneOrMore(Sequence(TestNot('/'),Char())),'/',Spacing());\n" +
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
            "  return Sequence(AOPEN(),OneOrMore(FirstOf(Identifier(),Literal())),ACLOSE());\n" +
            " }\n" +
            " \n" +
            " public Rule Literal( ) {\n" +
            "  return FirstOf(Sequence(\"'\",ZeroOrMore(Sequence(TestNot(\"'\"),Char())),\"'\",Spacing()),Sequence('\"',ZeroOrMore(Sequence(TestNot('\"'),Char())),'\"',Spacing()));\n" +
            " }\n" +
            " \n" +
            " public Rule Class( ) {\n" +
            "  return Sequence('[',ZeroOrMore(Sequence(TestNot(']'),Range())),']',Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule Range( ) {\n" +
            "  return FirstOf(Sequence(Char(),'-',Char()),Char());\n" +
            " }\n" +
            " \n" +
            " public Rule Char( ) {\n" +
            "  return FirstOf(Sequence(\"\\\\\",AnyOf(\"nrt'\\\"[]\\\\\")),Sequence(\"\\\\\",CharRange('0','2'),CharRange('0','7'),CharRange('0','7')),Sequence(\"\\\\\",CharRange('0','7'),Optional(CharRange('0','7'))),Sequence(TestNot(\"\\\\\"),ANY));\n" +
            " }\n" +
            " \n" +
            " public Rule LEFTARROW( ) {\n" +
            "  return Sequence(\"<-\",Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule SLASH( ) {\n" +
            "  return Sequence('/',Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule AND( ) {\n" +
            "  return Sequence('&',Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule NOT( ) {\n" +
            "  return Sequence('!',Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule QUESTION( ) {\n" +
            "  return Sequence('?',Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule STAR( ) {\n" +
            "  return Sequence('*',Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule PLUS( ) {\n" +
            "  return Sequence('+',Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule OPEN( ) {\n" +
            "  return Sequence('(',Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule CLOSE( ) {\n" +
            "  return Sequence(')',Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule DOT( ) {\n" +
            "  return Sequence('.',Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule EMPTY( ) {\n" +
            "  return Sequence(':',Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule NOTHING( ) {\n" +
            "  return Sequence('~',Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule COPEN( ) {\n" +
            "  return Sequence('{',Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule CCLOSE( ) {\n" +
            "  return Sequence('}',Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule AOPEN( ) {\n" +
            "  return Sequence('<',Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule ACLOSE( ) {\n" +
            "  return Sequence('>',Spacing());\n" +
            " }\n" +
            " \n" +
            " public Rule Spacing( ) {\n" +
            "  return ZeroOrMore(FirstOf(Space(),Comment()));\n" +
            " }\n" +
            " \n" +
            " public Rule Comment( ) {\n" +
            "  return Sequence('#',ZeroOrMore(Sequence(TestNot(EndOfLine()),ANY)),EndOfLine());\n" +
            " }\n" +
            " \n" +
            " public Rule Space( ) {\n" +
            "  return FirstOf(' ',\"\\t\",EndOfLine());\n" +
            " }\n" +
            " \n" +
            " public Rule EndOfLine( ) {\n" +
            "  return FirstOf(\"\\r\\n\",\"\\n\",\"\\r\");\n" +
            " }\n" +
            " \n" +
            " public Rule EndOfFile( ) {\n" +
            "  return TestNot(ANY);\n" +
            " }\n" +
            " \n" +
            " }";
}