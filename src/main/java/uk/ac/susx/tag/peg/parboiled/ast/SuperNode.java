package uk.ac.susx.tag.peg.parboiled.ast;

import java.util.ArrayList;
import java.util.List;

public class SuperNode<T extends SuperNode<T>> extends AbstractNode<T> {
    private final List<T> children = new ArrayList<>();

    public SuperNode() {
    }

    public SuperNode(T child) {
        children.add(child);
    }
    
    public SuperNode(List<T> children) {
        this.children.addAll(children);
    }

    public List<T> getChildren() {
        return children;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
    
}
