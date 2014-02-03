package org.greengin.sciencetoolkit.ui.dataviewer;

import java.io.File;

public interface SeriesListListener {
	void seriesDelete(File series);
	void seriesUpload(File series);
	void seriesSelected(File series, boolean selected);
}
