package org.greengin.sciencetoolkit.ui.remote;

import org.apache.http.client.methods.HttpRequestBase;

public abstract class RemoteAction {

	public abstract HttpRequestBase[] createRequests(String urlBase);

	public void aboutToRun(int request) {
	}

	public void result(int request, String result) {
	}

	public void close() {
	}

	public void error(String error) {
	}	
}
