package org.greengin.sciencetoolkit.ui.remote;

import org.apache.http.client.methods.HttpRequestBase;

public interface RemoteAction {
	
	HttpRequestBase[] createRequests(String urlBase);
	void result(int request, String result);
	void close();

}
