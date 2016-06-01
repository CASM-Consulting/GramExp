package uk.ac.susx.tag.peg.parboiled;

/**
 * Encapsulates basic string output functionality.
 */
public class Printer {
    public final StringBuilder sb;
    public int indent;

    public Printer() {
        this(new StringBuilder());
    }

    public Printer(StringBuilder sb) {
        this.sb = sb;
    }

    public Printer indent(int delta) {
        indent += delta;
        return this;
    }

    public Printer print(String string) {
        sb.append(string);
        return this;
    }


    public Printer print(char c) {
        sb.append(c);
        return this;
    }

    public Printer println() {
        if (sb.length() > 0) print('\n');
        for (int i = 0; i < indent; i++) print(' ');
        return this;
    }

    public Printer printchkln() {
        if (!endsWithNewLine()) {
            if (sb.length() > 0) print('\n');
            for (int i = 0; i < indent; i++) print(' ');
        }
        return this;
    }

    public Printer printchkln(boolean printNewLine) {
        if (printNewLine) {
            if (sb.length() > 0) print('\n');
            for (int i = 0; i < indent; i++) print(' ');
        }
        return this;
    }

    public boolean endsWithNewLine() {
        int iMax = sb.length();

        for (int i = iMax; i-- > 0; ) {
           if (sb.charAt(i) != ' ') {
               return sb.charAt(i) == '\n';
           }
        }
        // all leading spaces
        return false;
    }

    public String getString() {
        return sb.toString();
    }
    
    public Printer clear() {
        sb.setLength(0);
        return this;
    }
}
