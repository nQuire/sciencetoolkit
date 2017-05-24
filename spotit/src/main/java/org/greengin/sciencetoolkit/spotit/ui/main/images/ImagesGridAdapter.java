package org.greengin.sciencetoolkit.spotit.ui.main.images;

import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Vector;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.spotit.R;
import org.greengin.sciencetoolkit.spotit.model.ProjectManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ImagesGridAdapter extends BaseAdapter {

	ImageListener listener;
	LayoutInflater inflater;
	Vector<Model> data;
	HashMap<String, CachedImage> cache;

	public ImagesGridAdapter(LayoutInflater inflater, ImageListener listener) {
		this.inflater = inflater;
		this.cache = new HashMap<String, CachedImage>();
		this.listener = listener;
        this.data = new Vector<Model>();
		updateData();
	}

	public void updateData() {
		data.clear();
		data.addAll(ProjectManager.get().getNewImageContainer().getModels("date", true));

		Model project = ProjectManager.get().getActiveProject();
		if (project != null) {
			data.addAll(project.getModel("data", true).getModels("date", true));
		}

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
		Model project = ProjectManager.get().getActiveProject();
		Model imageData = data.get(position);

		boolean newView = convertView == null;

		View view = newView ? inflater.inflate(R.layout.view_images_item,
				parent, false) : convertView;
		
		ImageButton upload = (ImageButton) view.findViewById(R.id.observation_upload);
		ImageButton discard = (ImageButton) view.findViewById(R.id.observation_discard);
		
		if (newView) {
			discard.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					listener.imageDelete((Model) v.getTag());
				}
			});
			upload.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					listener.imageUpload((Model) v.getTag());
				}
			});
		}
		
		upload.setTag(imageData);
		discard.setTag(imageData);
		
		upload.setEnabled(project != null && imageData.getInt("uploaded", 0) == 0);
		int drawable = imageData.getInt("uploaded", 0) == 1 ? R.drawable.ic_action_upload : R.drawable.project_button_cloud;
		upload.setImageDrawable(parent.getResources().getDrawable(drawable));
		
		CachedImage cached = cache.get(imageData.getString("uri"));
		if (cached == null) {
			clearImage(view);
			new DownloadImageTask(view).execute(imageData);
		} else {
			setImage(view, cached);
		}

		return view;
	}

	public class CachedImage {
		public Model observation = null;
		public Bitmap bitmap = null;
	}
	
	private void setImage(View view, CachedImage image) {
		ImageView imageView = (ImageView) view
				.findViewById(R.id.observation_picture);

		imageView.setImageBitmap(image.bitmap);
	}
	
	private void clearImage(View view) {
		ImageView imageView = (ImageView) view
				.findViewById(R.id.observation_picture);

		imageView.setImageBitmap(null);
	}

	private class DownloadImageTask extends
			AsyncTask<Model, Void, CachedImage> {
		View itemView;

		public DownloadImageTask(View bmImage) {
			this.itemView = bmImage;
		}

		protected CachedImage doInBackground(Model... observations) {
			CachedImage result = new CachedImage();
			result.observation = observations[0];
			String filename = result.observation.getString("uri");

			Log.d("stk images", filename + " " + itemView.getWidth() + " "
					+ itemView.getHeight());

			int iMaxWidth = itemView.getWidth() > 0 ? itemView.getWidth() : 250;
			int iMaxHeigth = itemView.getHeight() > 0 ? itemView.getHeight()
					: 250;

			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(filename, options);

				if (options.outWidth > 0 && options.outHeight > 0) {

					ExifInterface exif = new ExifInterface(filename);
					exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

					int angle;
					boolean transpose;

					switch (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
							1)) {
					case ExifInterface.ORIENTATION_ROTATE_90:
						angle = 90;
						transpose = true;
						break;
					case ExifInterface.ORIENTATION_ROTATE_180:
						angle = 180;
						transpose = false;
						break;
					case ExifInterface.ORIENTATION_ROTATE_270:
						angle = 270;
						transpose = true;
						break;
					default:
						angle = 0;
						transpose = false;
						break;
					}

					options.inJustDecodeBounds = false;
					options.inSampleSize = 1;

					int oiWidth = transpose ? options.outHeight
							: options.outWidth;
					int oiHeigth = transpose ? options.outWidth
							: options.outHeight;

					while (oiWidth / options.inSampleSize > iMaxWidth
							|| oiHeigth / options.inSampleSize > iMaxHeigth) {
						options.inSampleSize *= 2;
					}

					Bitmap original = BitmapFactory.decodeFile(filename,
							options);
					Matrix matrix = new Matrix();
					matrix.postRotate(angle);
					result.bitmap = Bitmap.createBitmap(original, 0, 0,
							original.getWidth(), original.getHeight(), matrix,
							true);
				}
			} catch (Exception e) {
				Log.e("stk image", e.getMessage());
				e.printStackTrace();
			}

			return result;
		}

		protected void onPostExecute(CachedImage result) {
			if (result.bitmap != null) {
				cache.put(result.observation.getString("uri"), result);
				setImage(itemView, result);
			}
		}
	}
	
	
}