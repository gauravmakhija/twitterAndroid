package com.yahoo.makhija.apps.basictwitter;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.image.SmartImageView;
import com.yahoo.makhija.apps.basictwitter.models.Tweet;

public class ComposeTweetActivity extends Activity {
	
	private static final int COLOR_LIGHT_GRAY = -3355444;
	private TwitterClient client;
	private Tweet tweet;
//	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose_tweet);
		client = TwitterApplication.getRestClient();
		tweet = (Tweet)getIntent().getSerializableExtra("tweet");
//		user = tweet.getUser();
		SmartImageView ivProfilePic = (SmartImageView) findViewById(R.id.ivProfilePic);
		ivProfilePic.setImageUrl(tweet.getUser().getProfileImageUrl());
		TextView tvUserId = (TextView)findViewById(R.id.tvUserId);
		tvUserId.setText("@"+tweet.getUser().getScreenName());
		tvUserId.setTextColor(COLOR_LIGHT_GRAY);
		TextView tvUsersName = (TextView)findViewById(R.id.tvUsersName);
		tvUsersName.setText(tweet.getUser().getName());
	}
	
	public void onTweet(View v){
		TextView tvTweet = (TextView)findViewById(R.id.etTweet);
		client.postTweet(new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int arg0, JSONObject jsonObj) {
//				Toast.makeText(getBaseContext(), "tweeted succesfully", Toast.LENGTH_SHORT).show();
				
				try {
					tweet.setBody(jsonObj.get("text").toString());
					tweet.setCreatedAt(jsonObj.getString("created_at"));
					tweet.setUid(jsonObj.getLong("id_str"));
//					JSONObject userObj = jsonObj.getJSONObject("user");
//					user.setName(userObj.getString("name"));
//					user.setProfileImageUrl(userObj.getString("profile_image_url"));
//					user.setScreenName(userObj.getString("screen_name"));
//					user.setUid(userObj.getLong("id_str"));
				} catch (JSONException e) {
//					Toast.makeText(getBaseContext(), "tweet being sent back to timeline might be inappropriate", Toast.LENGTH_SHORT).show();
					Log.d("debug", "tweet being sent back to timeline might be inappropriate" + e);
				}
//				tweet.setUser(user);
				Intent i = new Intent();
				i.putExtra("tweet", tweet);
				setResult(RESULT_OK, i);
				finish();
			}
			
			@Override
			public void onFailure(Throwable arg0, JSONObject arg1) {
				//Toast.makeText(getBaseContext(), "tweeting failed", Toast.LENGTH_SHORT).show();
				Log.d("debug", "tweeting failed");
			}
			
		},tvTweet.getText().toString());
	}
	
	public void onCancel(View v){
		Intent i = new Intent();
		i.putExtra("tweet", "");
		setResult(RESULT_OK, i);
		finish();
	}

}
