package uk.ac.susx.tag.peg.parboiled;

/**
 * Created by simon on 14/06/16.
 */
public class Capture {

    private final String match;
    private final String ref;

    public static Capture of(String ref, String match) {
        return new Capture(ref, match);
    }

    public Capture(String ref, String match) {
        this.match = match;
        this.ref = ref;
    }

    public String match() {
        return match;
    }

    public String ref() {
        return ref;
    }

    @Override
    public String toString () {
        return ref + "=" + match;
    }
}
