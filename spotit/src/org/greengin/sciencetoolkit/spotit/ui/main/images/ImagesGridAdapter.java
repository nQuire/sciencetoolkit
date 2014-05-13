package org.greengin.sciencetoolkit.spotit.ui.main.images;

import java.util.Vector;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.spotit.R;
import org.greengin.sciencetoolkit.spotit.model.ProjectManager;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImagesGridAdapter extends BaseAdapter {

	LayoutInflater inflater;
	Vector<Model> data;
	String selectedProjectId;

	public ImagesGridAdapter(LayoutInflater inflater) {
		this.inflater = inflater;
		updateData();
	}

	private void updateData() {
		Model project = ProjectManager.get().getActiveProject();
		data = project != null ? project.getModel("data", true).getModels()
				: new Vector<Model>();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Model getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Model imageData = data.get(position);
		String uri = imageData.getString("uri");

		boolean newView = convertView == null;

		View view = newView ? inflater.inflate(R.layout.view_images, parent, false) : convertView;

		ImageView image = (ImageView) view.findViewById(R.id.image_view);
		image.setImageURI(Uri.parse(uri));
		
		return view;
	}

}