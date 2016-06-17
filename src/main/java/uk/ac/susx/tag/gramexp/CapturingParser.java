package uk.ac.susx.tag.gramexp;

import org.parboiled.BaseParser;

import java.util.*;

/**
 * Created by simon on 15/06/16.
 */
abstract public class CapturingParser extends BaseParser<Object> {

    protected final Set<String> groups = new HashSet<>();

    public Set<String> getGroups() {
        return groups;
    }

    public boolean addGroup(String group) {
        groups.add(group);
        return true;
    }

    public boolean matchPop(String match) {
        List<Capture> captures = new ArrayList<>();
        boolean result = false;

        while(true) {
            try {
                Object val = pop();
                if(val instanceof Capture) {
                    captures.add((Capture)val);
                } else {
                    result = val.equals(match);
                    break;
                }
            }catch (IllegalArgumentException e){
                break;
            }
        }

        Collections.reverse(captures);

        for(Capture c: captures) {
            push(c);
        }

        return result;
    }
}
