package uk.ac.susx.tag.gramexp.matchers;

import org.parboiled.MatcherContext;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.buffers.InputBufferUtils;
import org.parboiled.matchers.CustomMatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by simon on 29/09/16.
 */
public class RegularExpressionMatcher extends CustomMatcher {

    private final Pattern pattern;

    public RegularExpressionMatcher(String re) {
        super("Regexp");
        pattern = Pattern.compile(re);
    }

    @Override
    public boolean isSingleCharMatcher() {
        return false;
    }

    @Override
    public boolean canMatchEmpty() {
        return pattern.matcher("").find();
    }

    @Override
    public boolean isStarterChar(char c) {
        Matcher m = pattern.matcher(c+"");
        return m.find() && m.start()==0;
    }

    @Override
    public char getStarterChar() {
        return 0;
    }

    @Override
    public <V> boolean match(MatcherContext<V> context) {

        int cur = context.getCurrentIndex();

        InputBuffer buffer = context.getInputBuffer();
        String content = InputBufferUtils.collectContent(buffer);
        content = content.substring(cur);
        Matcher m = pattern.matcher(content);

        if(m.find()) {

            int start = m.start();
            if(start == 0) {
                int end = m.end();

                int delta = end - start;

                context.advanceIndex(delta);

                context.createNode();
                return true;

            } else {
                return false;
            }
        } else {

            return false;
        }

    }
}
