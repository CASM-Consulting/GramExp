package uk.ac.susx.tag.peg.parboiled;

import com.sun.org.apache.bcel.internal.generic.POP;
import org.parboiled.Parboiled;
import org.parboiled.common.StringUtils;
import uk.ac.susx.tag.peg.parboiled.ast.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.parboiled.support.ParseTreeUtils.printNodeTree;

/**
 * Created by simon on 27/05/16.
 */
public class AstToJava implements Visitor {

    private final Printer printer;
    private final String clsName;
    private final Set<String> groups;

    public AstToJava(String clsName) {
        printer = new Printer();
        this.clsName = clsName;
        groups = new HashSet<>();
    }

    @Override
    public void visit(GrammarNode node) {
//        printer.print("package "+pkg+";");
//        printer.println();
        printer.print("import org.parboiled.Action;");
        printer.println();
        printer.print("import org.parboiled.Context;");
        printer.println();
        printer.print("import org.parboiled.Rule;");
        printer.println();
        printer.print("import org.parboiled.annotations.BuildParseTree;");
        printer.println();
        printer.print("import java.util.Set;");
        printer.println();
        printer.print("import java.util.HashSet;");
        printer.println();
        printer.print("import uk.ac.susx.tag.peg.parboiled.CapturingParser;");
        printer.println();
        printer.print("@BuildParseTree");
        printer.println();

        String cls;
        switch (node.getMode()) {
            case "/nlp/":
                cls = AbstractNlpParser.class.getCanonicalName();
                break;
            default:
                cls = "CapturingParser<Object>";
                break;
        }

        printer.print("public class ");
        printer.print(clsName);
        printer.print(" extends ");
        printer.print(cls);
        printer.print(" {");
        printer.println();
        printer.indent(1);
//        printer.print("private final Set<String> groups = new HashSet<>();");
//        printer.println();
//        printer.print("@Override");
//        printer.println();
//        printer.print("public Set<String> getGroups() {return groups;}");
        printer.println();
        visitChildren(node);

        printer.println();
        printer.print("{");
        printer.println();
        for(String group : groups) {
            printer.print("addGroup("+group+");");
            printer.println();
        }
        printer.print("}");
        printer.println();

        printer.indent(-1);
        printer.print("}");
    }

    @Override
    public void visit(SuperNode node) {
        visitChildren(node);
    }

    @Override
    public void visit(DefinitionNode node) {

        printer.print("public Rule ");
        printer.print(node.name());
        printer.print("( ) {");
        printer.indent(1);
        printer.println();
        printer.print("return toRule(");
        visitChildren(node);
        printer.print(");");
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
        CaptureNode capture = node.getCapture();
        if(optional != null) {
            if(optional instanceof Literal.ANDNode) {
                printer.print("Test(");
            } else if(optional instanceof Literal.NOTNode) {
                printer.print("TestNot(");
            }
        }
        if(capture != null) {
            printer.print("Sequence(");
        }
        visitChildren(node);

        if(capture != null) {
            String ref = capture.getRef();
            printer.print(",addCapture("+ref+", match()))");
            groups.add(ref);
        }
        if(optional != null) {
            printer.print(")");
        }
    }

    @Override
    public void visit(SuffixNode node) {

        SuperNode pp = node.getPP();

        if(pp!=null) {
            printer.print("Sequence(");
        }

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

        if(pp != null) {
            printer.print(",");
            pp.accept(this);
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
        printer.print("(");
        List<Node> children = node.getChildren();
        visitChildren(node);
        printer.print(")");
    }

    @Override
    public void visit(ArgumentsNode node) {
        List<Node> children = node.getChildren();
        for( int i = 0; i < children.size() ; ++i) {
            children.get(i).accept(this);
            if(notLast(children, i)) {
                printer.print(",");
            }
        }
    }

    @Override
    public void visit(LiteralNode node) {
//        if(node.hasSiblings()) {
//            printer.print("String(");
//        }
        printer.print(node.getLiteral());
//        if(node.hasSiblings()) {
//            printer.print(")");
//        }
    }

    @Override
    public void visit(ClassNode node) {
        List<String> clss = node.getCls();
        List<String> chrs = new ArrayList<>();
        List<String> ranges = new ArrayList<>();

        for(String str : clss) {
            if(str.length() < 3) {
                chrs.add(str);
            } else {
                ranges.add(str);
            }
        }
        int n = 0;
        n += chrs.isEmpty() ? 0 : 1;
        n += ranges.size();

        if(n > 1) {
            printer.print("FirstOf(");
        }

        if(chrs.size() == 1) {
            char q;
            String chr = chrs.get(0);
            if (chr.equals("\'") || chr.length() > 1) {
                q = '"';
            } else {
                q = '\'';
            }
            printer.print(q);
            printer.print(chr);
            printer.print(q);
        } else if(chrs.size()>1) {
            printer.print("AnyOf(\"");
            printer.print(StringUtils.join(chrs, "")
                    .replaceAll("\"","\\\\\"")
                    .replaceAll("\\\\\\[","[")
                    .replaceAll("\\\\\\]","]")
            );
            printer.print("\")");

        }

        if(chrs.size() > 1) {
            printer.print(",");
        }

        for(int i = 0; i < ranges.size(); ++i) {
            String[] c = ranges.get(i).split("-");

            printer.print("CharRange('");
            printer.print(c[0]);
            printer.print("','");
            printer.print(c[1]);
            printer.print("')");

            if(notLast(ranges, i)) {
                printer.print(",");
            }

        }

        if(n > 1) {
            printer.print(")");
        }
    }

    @Override
    public void visit(PushNode node) {
        printer.print("push(match())");
    }

    @Override
    public void visit(PopNode node) {
        printer.print("pop().equals(match())");
    }

    @Override
    public void visit(CharNode node) {
        printer.print(node.getChar());
    }

    @Override
    public void visit(Literal.DOTNode node) {
        printer.print("ANY");
    }

    @Override
    public void visit(Literal.EMPTYNode node) {
        printer.print("EMPTY");
    }

    @Override
    public void visit(Literal.NOTHINGNode node) {
        printer.print("NOTHING");
    }

    @Override
    public void visit(EndOfFileNode node) {
        printer.print("EOI");
    }

    public String toJava(GrammarNode grammar) {
        grammar.accept(this);
        String java = printer.getString();
        printer.clear();
        return java;
    }


    protected void visitChildren(SuperNode node) {
        for (Object child : node.getChildren()) {
            if(child != null) {
                ((Node)child).accept(this);
            }
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

        String peg = "";

        try (
                BufferedReader reader = Files.newBufferedReader(Paths.get(arg[0]))
        ) {
            String line = "";
            while ((line = reader.readLine())!=null) {

                peg += line + "\n";
            }
        }


        AstToJava astToJava = new AstToJava(arg[1]);
        String java = astToJava.toJava(parser.parse(peg));

        try (
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(arg[1]+".java"))
        ) {
            writer.write(java);
        }
    }
}
