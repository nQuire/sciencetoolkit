package org.greengin.sciencetoolkit.ui.dataviewer;

import java.io.File;

import org.greengin.sciencetoolkit.common.model.Model;

public interface SeriesListListener {
	void seriesDelete(Model profile, File series);
	void seriesShare(Model profile, File series);
	void seriesUpload(Model profile, File series);
	void seriesSelected(Model profile, File series);
	void seriesEdit(Model profile, File series);
	boolean seriesResetUpload(Model profile, File series);
}
