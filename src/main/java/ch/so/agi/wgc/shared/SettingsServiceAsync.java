package ch.so.agi.wgc.shared;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SettingsServiceAsync {
    void configServer(AsyncCallback<SettingsResponse> callback)
            throws IllegalArgumentException;
}
