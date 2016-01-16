package org.greengin.sciencetoolkit.ui.base.dlgs.editprofile;

import org.greengin.sciencetoolkit.common.model.Model;

public interface ProfileActionListener {
	void profileDelete(Model profile);
	void profileTitleEditComplete(Model profile, String title);
}
