package uk.ac.susx.tag.peg.parboiled.ast;

/**
 * Created by simon on 27/05/16.
 */
public interface Visitor {

    void visit(SuperNode node);
    void visit(GrammarNode node);
    void visit(DefinitionNode node);
    void visit(ExpressionNode node);
    void visit(SequenceNode node);
    void visit(PrefixNode node);
    void visit(SuffixNode node);
    void visit(PrimaryNode node);
    void visit(IdentifierNode node);
    void visit(ArgumentsNode node);
    void visit(LiteralNode node);
    void visit(ClassNode node);
    void visit(CharNode node);
    void visit(Literal.DOTNode node);
    void visit(EndOfFileNode node);
    void visit(Literal.EMPTYNode node);
    void visit(Literal.NOTHINGNode node);
    void visit(PushNode node);
    void visit(PopNode node);


}
