package org.greengin.sciencetoolkit.model.serialize;

import org.greengin.sciencetoolkit.model.Model;

public interface ModelVersionManager {
	public int getCurrentVersion();
	public void updateRootModel(String key, Model model, int version);
}
