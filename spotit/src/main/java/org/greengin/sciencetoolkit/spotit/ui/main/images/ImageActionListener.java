package org.greengin.sciencetoolkit.spotit.ui.main.images;

import org.greengin.sciencetoolkit.common.model.Model;

import java.io.File;

public interface ImageActionListener {
	void imageDeleted(Model observation);
	void imageUploaded(Model observation, boolean uploadLocation);
}
