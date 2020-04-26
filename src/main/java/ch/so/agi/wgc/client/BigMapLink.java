package ch.so.agi.wgc.client;

import org.jboss.elemento.Attachable;
import org.jboss.elemento.IsElement;

import elemental2.dom.HTMLElement;
import elemental2.dom.MutationRecord;

import static org.jboss.elemento.Elements.*;

public class BigMapLink implements IsElement<HTMLElement>, Attachable {
    
    private final HTMLElement root;
    
    public BigMapLink(WgcMap map) {
        
        
        
        root = div().id("bigMapLink").css("bigMapLink").element();
    }
    
    @Override
    public void attach(MutationRecord mutationRecord) {
        
    }

    @Override
    public HTMLElement element() {
        return root;
    }

}
