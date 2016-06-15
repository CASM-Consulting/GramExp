package uk.ac.susx.tag.peg.parboiled;

import org.parboiled.BaseParser;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by simon on 15/06/16.
 */
abstract public class CapturingParser<T> extends BaseParser<T> {

    protected final Set<String> groups = new HashSet<>();

    public Set<String> getGroups() {
        return groups;
    }

    public boolean addGroup(String group) {
        groups.add(group);
        return true;
    }
}
