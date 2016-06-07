package uk.ac.susx.tag.peg.parboiled;

/**
 * Created by simon on 07/06/16.
 */
public class ParseException extends RuntimeException {

    public ParseException() {
        super();
    }
    public ParseException(String msg) {
        super(msg);
    }
    public ParseException(String msg, Throwable t) {
        super(msg, t);
    }

}
