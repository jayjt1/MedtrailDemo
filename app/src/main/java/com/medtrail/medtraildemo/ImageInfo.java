package com.medtrail.medtraildemo;

import android.graphics.Bitmap;

import com.medtrail.medtraildemo.FlickrManager.GetThumbnailsThread;
import com.medtrail.medtraildemo.MedtrailFlickrActivity.UIHandler;

public class ImageInfo implements ThumbInterface {
	String id;
	int position;
	String thumbURL;
	Bitmap thumb;
	Bitmap photo;
	String largeURL;
	String owner;
	String secret;
	String server;
	String farm;

	public ImageInfo(String id, String owner, String secret, String server, String farm) {
		super();
		this.id = id;
		this.owner = owner;
		this.secret = secret;
		this.server = server;
		this.farm = farm;
		setThumbURL(createPhotoURL(FlickrManager.PHOTO_THUMB, this));
		setLargeURL(createPhotoURL(FlickrManager.PHOTO_LARGE, this));
	}

	public String getThumbURL() {
		return thumbURL;
	}

	public void setThumbURL(String thumbURL) {
		this.thumbURL = thumbURL;
		onSaveThumbURL(FlickrManager.uihandler, this);
	}


	public void setLargeURL(String largeURL) {
		this.largeURL = largeURL;
	}

	@Override
	public String toString() {
		return "ImageInfo [id=" + id + ", thumbURL=" + thumbURL + ", largeURL=" + largeURL + ", owner=" + owner + ", secret=" + secret + ", server=" + server + ", farm="
				+ farm + "]";
	}

	private String createPhotoURL(int photoType, ImageInfo imgCon) {
		String tmp = null;
		tmp = "https://farm" + imgCon.farm + ".staticflickr.com/" + imgCon.server + "/" + imgCon.id + "_" + imgCon.secret;// +".jpg";
		switch (photoType) {
		case FlickrManager.PHOTO_THUMB:
			tmp += "_t";
			break;
		case FlickrManager.PHOTO_LARGE:
			tmp += "_z";
			break;

		}
		tmp += ".jpg";
		return tmp;
	}

	@Override
	public void onSaveThumbURL(UIHandler uih, ImageInfo ic) {
		// TODO Auto-generated method stub
		new GetThumbnailsThread(uih, ic).start();
	}
}
