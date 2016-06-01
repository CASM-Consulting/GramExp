package uk.ac.susx.tag.peg.parboiled.ast;

/**
 * Created by simon on 01/06/16.
 */
public class Literal extends SuperNode {

    public static class SLASHNode extends Literal {}
    public static class ANDNode extends Literal {}
    public static class NOTNode extends Literal {}
    public static class QUESTIONNode extends Literal {}
    public static class STARNode extends Literal {}
    public static class PLUSNode extends Literal {}
    public static class OPENNode extends Literal {}
    public static class CLOSENode extends Literal {}
    public static class DOTNode extends Literal {
        @Override
        public void accept(Visitor visitor) {
            visitor.visit(this);
        }

    }
}
