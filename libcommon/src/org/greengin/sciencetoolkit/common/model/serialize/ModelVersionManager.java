package org.greengin.sciencetoolkit.common.model.serialize;

import org.greengin.sciencetoolkit.common.model.Model;

public interface ModelVersionManager {
	public int getCurrentVersion();
	public void updateRootModel(String key, Model model, int version);
}
