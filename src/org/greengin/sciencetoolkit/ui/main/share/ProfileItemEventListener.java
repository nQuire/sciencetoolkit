package org.greengin.sciencetoolkit.ui.main.share;

public interface ProfileItemEventListener {
	void profileSelected(String profileId);
	void profileView(String profileId);
	void profileDelete(String profileId);
}

