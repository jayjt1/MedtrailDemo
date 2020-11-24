package com.medtrail.medtraildemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.medtrail.medtraildemo.adapter.FlickrAdapter;

import java.util.ArrayList;

public class MedtrailFlickrActivity extends Activity {

	public final String LAST_IMAGE = "lastImage";
	public UIHandler uihandler;
	private ArrayList<ImageInfo> imageList;

	private Button downloadPhotos;
	private EditText editText;
	RecyclerView recyclerFlickr;
	FlickrAdapter flickrAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_medtrail_flickr);

		uihandler = new UIHandler();

		downloadPhotos = (Button) findViewById(R.id.button1);
		editText = (EditText) findViewById(R.id.editText1);

		recyclerFlickr = (RecyclerView) findViewById(R.id.recyclerLauncher);
		recyclerFlickr.setLayoutManager(new GridLayoutManager(this, 3));

		downloadPhotos.setOnClickListener(onSearchButtonListener);
		imageList = (ArrayList<ImageInfo>) getLastNonConfigurationInstance();
		if (imageList != null) {

			imageList = flickrAdapter.getImageInfo();
			flickrAdapter = new FlickrAdapter(this, imageList);
			recyclerFlickr.setAdapter(flickrAdapter);
		}
	}

	/**
	 * Saving information about images
	 */
	@Override
	public Object onRetainNonConfigurationInstance() {
		if (flickrAdapter != null)
			return this.flickrAdapter.getImageInfo();
		else
			return null;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// Saving index of selected item in Gallery
		//outState.putInt(LAST_IMAGE, r.getSelectedItemPosition());
		super.onSaveInstanceState(outState);
	}
	/**
	 * 
	 * @author J@yant
	 * 
	 *         Downloading a larger photo using Thread
	 */

	/**
	 * Runnable to get metadata from Flickr API
	 */
	Runnable getMetadata = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String tag = editText.getText().toString().trim();
			if (tag != null && tag.length() >= 3)
				FlickrManager.searchImagesByTag(uihandler, getApplicationContext(), tag);
		}
	};

	/**
	 *
	 * @author J@yant
	 *
	 *         UI Handler to handle messages from threads
	 */
	class UIHandler extends Handler {
		public static final int ID_METADATA_DOWNLOADED = 0;
		public static final int ID_UPDATE_ADAPTER = 2;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ID_METADATA_DOWNLOADED:
				// Set of information required to download thumbnails is
				// available now
				if (msg.obj != null) {
					imageList = (ArrayList<ImageInfo>) msg.obj;
					flickrAdapter = new FlickrAdapter(MedtrailFlickrActivity.this, imageList);
					recyclerFlickr.setAdapter(flickrAdapter);
				}
				break;
			}
			super.handleMessage(msg);
		}
	}
	/**
	 * to get metadata from Flickr API
	 */
	OnClickListener onSearchButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			try {
				InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
			} catch (Exception e) {
				// TODO: handle exception
			}
			// TODO Auto-generated method stub
				imageList = new ArrayList<ImageInfo>();

				flickrAdapter = new FlickrAdapter(MedtrailFlickrActivity.this, imageList);
			recyclerFlickr.setAdapter(flickrAdapter);
			new Thread(getMetadata).start();
		}
	};

}
