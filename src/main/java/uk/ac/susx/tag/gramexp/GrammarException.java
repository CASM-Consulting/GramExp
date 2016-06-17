package uk.ac.susx.tag.gramexp;

/**
 * Created by sw206 on 23/05/2016.
 */
public class GrammarException extends RuntimeException {

    public GrammarException() {
        super();
    }
    public GrammarException(String msg) {
        super(msg);
    }
    public GrammarException(String msg, Throwable t) {
        super(msg, t);
    }
    public GrammarException(Throwable t) {
        super(t);
    }
}
