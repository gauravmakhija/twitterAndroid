package com.yahoo.makhija.apps.basictwitter.models;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.DateUtils;

public class Tweet implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1875496677762975052L;
	private static final String H = "h";
	private static final String M = "m";
	private static final String S = "s";
	private static final String HOURS_AGO = " hours ago";
	private static final String HOUR_AGO = " hour ago";
	private static final String MINUTES_AGO = " minutes ago";
	private static final String MINUTE_AGO = " minute ago";
	private static final String SECONDS_AGO = " seconds ago";
	private static final String SECOND_AGO = " second ago";
	
	private String body;
	private long uid;
	private String createdAt;
	private User user;
	
	private static final String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
	private static final SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);

	static{
		sf.setLenient(true);
	}
	
	public static ArrayList<Tweet> fromJSONArray(JSONArray json) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>(json.length());
		for(int i=0;i<json.length();i++){
			try {
				tweets.add(Tweet.fromJson(json.getJSONObject(i)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return tweets;
	}
	
	public static Tweet fromJson(JSONObject jsonObject){
		Tweet tweet = new Tweet();
		//extract values from json to populate the member variables
		try{
			tweet.body = jsonObject.getString("text");
			tweet.uid = jsonObject.getLong("id");
			tweet.createdAt = jsonObject.getString("created_at");
			tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
		}catch(JSONException e){
			e.printStackTrace();
			return null;
		}
		return tweet;
	}
	
	public String getBody() {
		return body;
	}

	public long getUid() {
		return uid;
	}

	public String getCreatedAt() {
		long dateMillis = 0;
		try {
			dateMillis = sf.parse(createdAt).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		String str = (String)DateUtils.getRelativeTimeSpanString(dateMillis,
				System.currentTimeMillis(),
				1000L);
		str = str.replace(SECOND_AGO, S);
		str = str.replace(SECONDS_AGO, S);
		str = str.replace(MINUTE_AGO, M);
		str = str.replace(MINUTES_AGO, M);
		str = str.replace(HOUR_AGO, H);
		str = str.replace(HOURS_AGO, H);
		return str;
	}

	public User getUser() {
		return user;
	}
	
	@Override
	public String toString() {
		return this.body + "-" + this.user.getScreenName();
	}
	
	public void setBody(String body) {
		this.body = body;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
