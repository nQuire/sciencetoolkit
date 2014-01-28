package org.greengin.sciencetoolkit.ui.base.widgets;

import java.io.IOException;
import java.io.InputStream;

import org.greengin.sciencetoolkit.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class GifView extends View {

	Movie movie;
	long moviestart;

	public GifView(Context context) throws IOException {
		super(context);
	}

	public GifView(Context context, AttributeSet attrs) throws IOException {
		super(context, attrs);
		this.loadGifAttr(attrs);
	}

	public GifView(Context context, AttributeSet attrs, int defStyle) throws IOException {
		super(context, attrs, defStyle);
		this.loadGifAttr(attrs);
	}
	
	private void loadGifAttr(AttributeSet attrs) {
		TypedArray array = this.getContext().obtainStyledAttributes(attrs, R.styleable.GifView);
		int v = array.getResourceId(R.styleable.GifView_src, 0);
		array.recycle();
		loadGIFResource(getContext(), v);
		return;
	}

	public void loadGIFResource(Context context, int id) {
		// turn off hardware acceleration
		//this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		InputStream is = context.getResources().openRawResource(id);
		movie = Movie.decodeStream(is);
		this.invalidate();
	}

	public void loadGIFAsset(Context context, String filename) {
		InputStream is;
		try {
			is = context.getResources().getAssets().open(filename);
			movie = Movie.decodeStream(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (movie == null) {
			return;
		}

		long now = android.os.SystemClock.uptimeMillis();

		if (moviestart == 0)
			moviestart = now;

		int relTime;
		relTime = (int) ((now - moviestart) % movie.duration());
		movie.setTime(relTime);
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		float scale = (1f * this.getWidth()) / movie.width();
		Log.d("stk gif", "" + movie.width() + " " + this.getWidth() + " " + scale + " " + movie.duration() + " " + relTime);
		canvas.scale(scale, scale);
		movie.draw(canvas, 0, 0);
		canvas.restore();
		invalidate();
	}
}