package com.yahoo.makhija.apps.basictwitter;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.activeandroid.util.Log;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yahoo.makhija.apps.basictwitter.models.Tweet;
import com.yahoo.makhija.apps.basictwitter.models.TweetArrayAdapter;
import com.yahoo.makhija.apps.basictwitter.models.User;

public class TimelineActivity extends Activity {
	private static final String TWEET = "tweet";
	private static final int COMPOSE_TWEET = 1;
	private TwitterClient client;
	private ArrayList<Tweet> tweets;
	private ArrayAdapter<Tweet> adapterTweets;
	private ListView lvTweets;
	private static long max_id = 999999999999999999L;
	private Tweet tweet;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		client = TwitterApplication.getRestClient();
		populateTimeline(true);
		lvTweets = (ListView)findViewById(R.id.lvTweets);
		tweets = new ArrayList<Tweet>();
		adapterTweets = new TweetArrayAdapter(this, tweets);
		lvTweets.setAdapter(adapterTweets);
		setupOnScrollListener();
		tweet = new Tweet();
		fetchUserInfo();
	}
	
	private void fetchUserInfo() {
		client.fetchUserInfo(new JsonHttpResponseHandler(){
			
			@Override
			public void onSuccess(JSONObject json) {
				Log.d("debug",json.toString());
				try {
					User user = new User();
					user.setName(json.getString("name"));
					user.setScreenName(json.getString("screen_name"));
					user.setProfileImageUrl(json.getString("profile_image_url_https"));
					user.setUid(json.getLong("id_str"));
					tweet.setUser(user);
				} catch (JSONException e) {
					Log.d("failed to set user info after receiving");
				}
			}
			
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("failed to get user info");
			}
			
		});
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.compose, menu);
        return true;
    }
	
	public void onComposeTweet(MenuItem mi){
    	Intent i = new Intent(this,ComposeTweetActivity.class);
    	//i.putExtra(SOMETHING_IF_NEEDED_TO_BE_PASSED, somethingIfNeededToPassed);
    	i.putExtra(TWEET, tweet);
       	startActivityForResult(i, COMPOSE_TWEET);
    }
	
	private void setupOnScrollListener() {
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
            	populateTimeline(false);
            }
		});
	}

	public void populateTimeline(final boolean clearResults){
		client.getHomeTimeline(new JsonHttpResponseHandler(){
			
			@Override
			public void onSuccess(JSONArray json) {
				Log.d("debug",json.toString());
//				Toast.makeText(getBaseContext(), "populate timeline succesful", Toast.LENGTH_SHORT).show();
				if(clearResults)
					tweets.clear();
				adapterTweets.addAll(Tweet.fromJSONArray(json));
				for(Tweet tweet : tweets){
					if(tweet.getUid() < max_id){
						max_id = tweet.getUid();
					}
				}
			}
			
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug",e.toString());
				Log.d("debug",s.toString());
//				Toast.makeText(getBaseContext(), "populate timeline failed", Toast.LENGTH_SHORT).show();
			}
			
		},max_id);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == COMPOSE_TWEET){
			if(resultCode==RESULT_OK){
				Serializable serializableExtra = data.getSerializableExtra(TWEET);
				if(serializableExtra!=null && serializableExtra.toString()!=null && !serializableExtra.toString().isEmpty()){
					tweets.add(0, (Tweet)serializableExtra);
					adapterTweets.notifyDataSetChanged();
				}
			}
		}
	}
	
}
