

package uk.ac.susx.tag.gramexp.ast;

import org.parboiled.trees.GraphNode;

public interface Node<T extends Node<T>> extends GraphNode<T> {

    /**
     * @return the index of the first character in the underlying buffer that is covered by this node 
     */
    int getStartIndex();

    /**
     * @return the index of the character after the last one in the underlying buffer that is covered by this node
     */
    int getEndIndex();
    
    void accept(Visitor visitor);
}
