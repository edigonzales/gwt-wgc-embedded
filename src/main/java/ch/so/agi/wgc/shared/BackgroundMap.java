package ch.so.agi.wgc.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class BackgroundMap implements Serializable, IsSerializable {
    private static final long serialVersionUID = 1L;

    public BackgroundMap() {}
    
    private String id;
    private String title;
    private String url;
    private String layer;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getLayer() {
        return layer;
    }
    public void setLayer(String layer) {
        this.layer = layer;
    }
    
//    @Override
//    public String toString() {
//        return "BackgroundMap [id=" + id + ", title=" + title + ", url=" + url + ", layer=" + layer + "]";
//    }
}
