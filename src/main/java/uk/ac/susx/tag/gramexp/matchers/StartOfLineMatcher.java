package uk.ac.susx.tag.gramexp.matchers;

import org.parboiled.MatcherContext;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.buffers.InputBufferUtils;
import org.parboiled.matchers.CustomMatcher;

/**
 * Created by simon on 28/09/16.
 */
public class StartOfLineMatcher extends CustomMatcher{

    public StartOfLineMatcher() {
        super("StartOfLine");
    }

    @Override
    public boolean isSingleCharMatcher() {
        return true;
    }

    @Override
    public boolean canMatchEmpty() {
        return true;
    }

    @Override
    public boolean isStarterChar(char c) {
        return c == '\n';
    }

    @Override
    public char getStarterChar() {
        return '\n';
    }

    @Override
    public <V> boolean match(MatcherContext<V> context) {
        int cur = context.getCurrentIndex();
        if(cur == 0) {
            context.createNode();
            return true;
        }
        InputBuffer buffer = context.getInputBuffer();

        if(buffer.charAt(cur-1)=='\n') {
            context.createNode();
            return true;
        }

        return false;
    }
}
