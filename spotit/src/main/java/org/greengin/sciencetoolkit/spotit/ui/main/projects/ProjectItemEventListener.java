package org.greengin.sciencetoolkit.spotit.ui.main.projects;

import org.greengin.sciencetoolkit.common.model.Model;

public interface ProjectItemEventListener {
	void projectSelected(Model project);
	void projectDelete(Model project);
}

