package org;

import java.util.NoSuchElementException;

public class TYIterator extends OHIterator {
    public TYIterator(OHRequest queue) {
        super(queue);
    }
    @Override
    public OHRequest next() {
        OHRequest res = super.next();
        if (res != null && res.description.contains("thank u")) {
            curr = curr.next;
        }
        return res;
    }
}

