package com.medtrail.medtraildemo;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.Log;

import com.medtrail.medtraildemo.MedtrailFlickrActivity.UIHandler;
import com.medtrail.medtraildemo.adapter.util.Util;

public class FlickrManager {

	// String to create Flickr API urls
	private static final String FLICKR_BASE_URL = "https://api.flickr.com/services/rest/?method=";
	private static final String FLICKR_PHOTOS_SEARCH_STRING = "flickr.photos.search";
	private static final String FLICKR_GET_SIZES_STRING = "flickr.photos.getSizes";
	private static final int FLICKR_PHOTOS_SEARCH_ID = 1;
	private static final int FLICKR_GET_SIZES_ID = 2;
	private static final int NUMBER_OF_PHOTOS = 25;
	
	//You can set here your API_KEY
	private static final String APIKEY_SEARCH_STRING = "&api_key=3189212285dcb4cf5b2f044edcb0544e";
	
	private static final String TAGS_STRING = "&tags=";
	private static final String PHOTO_ID_STRING = "&photo_id=";
	private static final String FORMAT_STRING = "&format=json";
	public static final int PHOTO_THUMB = 111;
	public static final int PHOTO_LARGE = 222;

	public static UIHandler uihandler;

	private static String createURL(int methodId, String parameter) {
		String method_type = "";
		String url = null;
		switch (methodId) {
		case FLICKR_PHOTOS_SEARCH_ID:
			method_type = FLICKR_PHOTOS_SEARCH_STRING;
			url = FLICKR_BASE_URL + method_type + APIKEY_SEARCH_STRING + TAGS_STRING + parameter + FORMAT_STRING + "&per_page="+NUMBER_OF_PHOTOS+"&media=photos";
			break;
		case FLICKR_GET_SIZES_ID:
			method_type = FLICKR_GET_SIZES_STRING;
			url = FLICKR_BASE_URL + method_type + PHOTO_ID_STRING + parameter + APIKEY_SEARCH_STRING + FORMAT_STRING;
			break;
		}
		return url;
	}

	public static Bitmap getThumbnail(ImageInfo imgCon) {
		Bitmap bm = null;
		try {
			URL aURL = new URL(imgCon.thumbURL);
			URLConnection conn = aURL.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			bm = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();
		} catch (Exception e) {
			Log.e("FlickrManager", e.getMessage());
		}
		return bm;
	}

	public static class GetThumbnailsThread extends Thread {
		UIHandler uih;
		ImageInfo imgContener;

		public GetThumbnailsThread(UIHandler uih, ImageInfo imgCon) {
			this.uih = uih;
			this.imgContener = imgCon;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			imgContener.thumb = getThumbnail(imgContener);
			if (imgContener.thumb != null) {
				Message msg = Message.obtain(uih, UIHandler.ID_UPDATE_ADAPTER);
				uih.sendMessage(msg);

			}
		}

	}

	public static ArrayList<ImageInfo> searchImagesByTag(UIHandler uih, Context ctx, String tag) {

		uihandler = uih;
		String url = createURL(FLICKR_PHOTOS_SEARCH_ID, tag);
		ArrayList<ImageInfo> tmp = new ArrayList<ImageInfo>();
		String jsonString = null;
		try {
			if (URLConnector.isOnline(ctx)) {
				ByteArrayOutputStream baos = URLConnector.readBytes(url);
				jsonString = baos.toString();
			}
			try {
				JSONObject root = new JSONObject(jsonString.replace("jsonFlickrApi(", "").replace(")", ""));
				JSONObject photos = root.getJSONObject("photos");
				JSONArray imageJSONArray = photos.getJSONArray("photo");
				for (int i = 0; i < imageJSONArray.length(); i++) {
					JSONObject item = imageJSONArray.getJSONObject(i);
					ImageInfo imgCon = new ImageInfo(item.getString("id"), item.getString("owner"), item.getString("secret"), item.getString("server"),
							item.getString("farm"));
					imgCon.position = i;
					tmp.add(imgCon);
				}
				Message msg = Message.obtain(uih, UIHandler.ID_METADATA_DOWNLOADED);
				msg.obj = tmp;
				uih.sendMessage(msg);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NullPointerException nue) {
			nue.printStackTrace();
		}

		return tmp;
	}

}
