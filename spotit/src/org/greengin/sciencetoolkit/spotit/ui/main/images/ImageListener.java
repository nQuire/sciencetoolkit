package org.greengin.sciencetoolkit.spotit.ui.main.images;

import org.greengin.sciencetoolkit.common.model.Model;

public interface ImageListener {
	public void imageUpload(Model observation);
	public void imageDelete(Model observation);
}
