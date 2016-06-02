package uk.ac.susx.tag.peg.parboiled;

import org.parboiled.Parboiled;
import uk.ac.susx.tag.peg.parboiled.ast.*;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.parboiled.support.ParseTreeUtils.printNodeTree;

/**
 * Created by simon on 27/05/16.
 */
public class AstTojava implements Visitor {

    private Printer printer;

    public AstTojava() {
        printer = new Printer();
    }

    @Override
    public void visit(GrammarNode node) {
        printer.print("import org.parboiled.BaseParser;");
        printer.println();
        printer.print("import org.parboiled.Rule;");
        printer.println();
        printer.print("@BuildParseTree");
        printer.println();
        printer.print("public class Grammar extends BaseParser<Object> {");
        printer.println();
        printer.indent(1);
        visitChildren(node);
        printer.indent(-1);
        printer.print("}");
    }

    @Override
    public void visit(SuperNode node) {
        visitChildren(node);
    }

    @Override
    public void visit(DefinitionNode node) {

        printer.print("Rule ");
        printer.print(node.name());
        printer.print("( ) {");
        printer.indent(1);
        printer.println();
        printer.print("return ");
        visitChildren(node);
        printer.print(";");
        printer.indent(-1);
        printer.println();
        printer.print("}");
        printer.println();
        printer.println();

    }

    @Override
    public void visit(ExpressionNode node) {

        List<Node> children = node.getChildren();

        if(onePlus(children)) {
            printer.print("FirstOf(");
        }

        for (int i = 0; i < children.size(); ++i) {
            children.get(i).accept(this);
            if ( notLast(children, i) ) {
                printer.print(",");
            }
        }

        if(onePlus(children)) {
            printer.print(")");
        }

    }

    @Override
    public void visit(SequenceNode node) {
        List<Node> children = node.getChildren();

        if(onePlus(children)) {
            printer.print("Sequence(");
        }

        for (int i = 0; i < children.size(); ++i) {
            children.get(i).accept(this);
            if ( notLast(children, i) ) {
                printer.print(",");
            }
        }

        if(onePlus(children)) {
            printer.print(")");
        }


    }

    @Override
    public void visit(PrefixNode node) {
        Literal optional = node.getOptional();
        if(optional != null) {
            if(optional instanceof Literal.ANDNode) {
                printer.print("Test(");
            } else if(optional instanceof Literal.NOTNode) {
                printer.print("TestNot(");
            }
        }
        visitChildren(node);

        if(optional!=null) {
            printer.print(")");
        }
    }

    @Override
    public void visit(SuffixNode node) {

        Literal optional = node.getOptional();
        if(optional != null ) {

            if(optional instanceof Literal.QUESTIONNode) {
                printer.print("Optional(");
            } else if(optional instanceof Literal.STARNode) {
                printer.print("ZeroOrMore(");
            } else if(optional instanceof Literal.PLUSNode) {
                printer.print("OneOrMore(");
            }
        }

        visitChildren(node);

        if(optional !=null) {
            printer.print(")");
        }
    }

    @Override
    public void visit(PrimaryNode node) {
        visitChildren(node);
    }

    @Override
    public void visit(IdentifierNode node) {
        printer.print(node.getId());
        printer.print("()");
    }

    @Override
    public void visit(LiteralNode node) {
        printer.print(node.getLiteral());
    }

    @Override
    public void visit(ClassNode node) {
        List<String> clss = node.getCls();
        if(onePlus(clss)) {
            printer.print("FirstOf(");
        }

        for(int i = 0; i < clss.size(); ++i) {
            String[] c = clss.get(i).split("-");
            if(c.length == 1) {
                char q;
                if(c[0].equals("\'") || c[0].length() > 1) {
                    q = '"';
                } else {
                    q = '\'';
                }
                printer.print(q);
                printer.print(c[0]);
                printer.print(q);
            } else if(c.length > 1) {
                printer.print("CharRange('");
                printer.print(c[0]);
                printer.print("','");
                printer.print(c[1]);
                printer.print("')");
            }

            if(notLast(clss, i)) {
                printer.print(",");
            }

        }

        if(onePlus(clss)) {
            printer.print(")");
        }
    }

    @Override
    public void visit(CharNode node) {
        printer.print(node.getChar());
    }

    @Override
    public void visit(Literal.DOTNode node) {
        printer.print("ANY");
    }

    public String toJava(GrammarNode grammar) {
        grammar.accept(this);
        return printer.getString();
    }


    protected void visitChildren(SuperNode node) {
        for (Object child : node.getChildren()) {
            ((Node)child).accept(this);
        }
    }

    private boolean onePlus(List lst) {
        return lst.size() > 1;
    }

    private boolean notLast(List lst, int i) {
        return  i < lst.size() -1;
    }

    public static void main(String[] arg) throws Exception{
        PegParser parser  = Parboiled.createParser(PegParser.class);

        String input = "";

        try (
                BufferedReader reader = Files.newBufferedReader(Paths.get("src/main/resources/peg.peg"))
        ) {
            String line = "";
            while ((line = reader.readLine())!=null) {

                input += line + "\n";
            }
        }


        AstTojava astTojava = new AstTojava();
        System.out.println(astTojava.toJava(parser.parse(input)));
    }
}
