package org;

import java.util.Iterator;
public class OHQueue implements Iterable<OHRequest> {
    private OHRequest queue;
    public OHQueue (OHRequest queue) {
        this.queue = queue;
    }
    @Override
    public Iterator<OHRequest> iterator() {
        return new TYIterator(queue);
    }
    public static void main(String [] args) {
        OHRequest s5 = new OHRequest("cfsvgsiu", "s5", null);
        OHRequest s4 = new OHRequest("dahukcx", "s4", s5);
        OHRequest s3 = new OHRequest("xa", "s3", s4);
        OHRequest s2 = new OHRequest("thank u", "s2", s3);
        OHRequest s1 = new OHRequest("thank u", "s1", s2);
        OHQueue s = new OHQueue(s1);
        for (OHRequest x: s) {
            System.out.println(x.name);
        }
        }
}
