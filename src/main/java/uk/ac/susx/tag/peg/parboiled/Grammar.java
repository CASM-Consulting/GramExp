package uk.ac.susx.tag.peg.parboiled;

import uk.ac.susx.tag.peg.parboiled.loading.Wrapper;

import java.lang.ref.WeakReference;

/**
 * Created by simon on 09/06/16.
 */
public class Grammar {

    private final WeakReference<Wrapper> weakRef;

    private final Wrapper wrapper;

    private Grammar(String grammar) {

        wrapper = new Wrapper(grammar);

        weakRef = new WeakReference<Wrapper>(wrapper);

        weakRef.
    }

    public static final Grammar match(String grammar) {

    }

}
