package uk.ac.susx.tag.peg.parboiled.ast;

import java.util.ArrayList;
import java.util.List;

public class SuperNode<T extends SuperNode<T>> extends AbstractNode<T> {
    private final List<T> children = new ArrayList<>();
    private SuperNode parent;

    public SuperNode() {
    }

    public SuperNode(T child) {
        if(child!=null)  {
            child.setParent(this);
            children.add(child);
        }

    }
    
    public SuperNode(List<T> children) {
        children.forEach(child -> {
            if(child!=null) child.setParent(this);
            this.children.add(child);
        });
    }

    public List<T> getChildren() {
        return children;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public boolean hasSiblings () {
        return parent != null && parent.getChildren().size() > 1;
    }

    public T setParent(SuperNode parent) {
        this.parent = parent;
        return (T)this;
    }
    
}
