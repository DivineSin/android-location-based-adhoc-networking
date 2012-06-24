package com.jwetherell.augmented_reality.activity;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.jwetherell.augmented_reality.R;
import com.jwetherell.augmented_reality.common.MyNewDataReceiver;
import com.jwetherell.augmented_reality.data.ARData;
import com.jwetherell.augmented_reality.data.ContextDataSource;
import com.jwetherell.augmented_reality.data.NetworkDataSource;
import com.jwetherell.augmented_reality.ui.Marker;
import com.jwetherell.augmented_reality.widget.VerticalTextView;


import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;


/**
 * This class extends the AugmentedReality and is designed to be an example on how to extends the AugmentedReality
 * class to show multiple data sources.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class Demo extends AugmentedReality {
	private static final String TAG = "Demo";
	public static final String INITIALIZE_NETWORK = "edu.gmu.hodum.INITIALIZE_NETWORK";
	public static final String NEW_DATA = "edu.gmu.hodum.NEW_DATA_IN_DATABASE";
	private static final String locale = Locale.getDefault().getLanguage();
	private static final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(1);
	private static final ThreadPoolExecutor exeService = new ThreadPoolExecutor(1, 1, 20, TimeUnit.SECONDS, queue);
	private static final Map<String,NetworkDataSource> sources = new ConcurrentHashMap<String,NetworkDataSource>();    

	private static Toast myToast = null;
	private static VerticalTextView text = null;
	private MyNewDataReceiver receiver;
	private ContextDataSource contextData;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent broadcastIntent = new Intent(INITIALIZE_NETWORK);
		broadcastIntent.putExtra("channel", "8");
		this.sendBroadcast(broadcastIntent);
		//Create toast
		myToast = new Toast(getApplicationContext());
		myToast.setGravity(Gravity.CENTER, 0, 0);
		// Creating our custom text view, and setting text/rotation
		text = new VerticalTextView(getApplicationContext());
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		text.setLayoutParams(params);
		text.setBackgroundResource(android.R.drawable.toast_frame);
		text.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Small);
		text.setShadowLayer(2.75f, 0f, 0f, Color.parseColor("#BB000000"));
		myToast.setView(text);
		
		// Setting duration and displaying the toast
		myToast.setDuration(Toast.LENGTH_SHORT);

		contextData = new ContextDataSource(this);
		ARData.addMarkers(contextData.getMarkers());

		receiver = new MyNewDataReceiver();
		receiver.registerHandler(newDataHandler);
		IntentFilter filter1 = new IntentFilter(NEW_DATA);
		registerReceiver(receiver,filter1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onStart() {
		super.onStart();

		Location last = ARData.getCurrentLocation();
		updateData(last.getLatitude(),last.getLongitude(),last.getAltitude());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.v(TAG, "onOptionsItemSelected() item="+item);
		switch (item.getItemId()) {
		case R.id.showRadar:
			showRadar = !showRadar;
			item.setTitle(((showRadar)? "Hide" : "Show")+" Radar");
			break;
		case R.id.showZoomBar:
			showZoomBar = !showZoomBar;
			item.setTitle(((showZoomBar)? "Hide" : "Show")+" Zoom Bar");
			zoomLayout.setVisibility((showZoomBar)?LinearLayout.VISIBLE:LinearLayout.GONE);
			break;
		case R.id.exit:
			finish();
			break;
		case R.id.switchPlanType:
			patrolPlan = !patrolPlan;
			item.setTitle("Switch to: " +((patrolPlan)? "Patrol":"Humanitarian"));
			contextData.setTypeOfPlan((patrolPlan)? "Patrol":"Humanitarian");
			ARData.clearAndAddMarkers(contextData.getMarkers());
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);

		updateData(location.getLatitude(),location.getLongitude(),location.getAltitude());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void markerTouched(Marker marker) {
		text.setText(marker.getName());
		myToast.show();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void updateDataOnZoom() {
		super.updateDataOnZoom();
		Location last = ARData.getCurrentLocation();
		updateData(last.getLatitude(),last.getLongitude(),last.getAltitude());
	}

	private void updateData(final double lat, final double lon, final double alt) {
		try {
			exeService.execute(
					new Runnable() {
						@Override
						public void run() {
							List<Marker> markers;
							if(contextData == null)
							{
								System.out.println("Ahh, DB null");
								return;
							}
							markers = contextData.getMarkers();
							if(markers == null)
							{
								System.out.println("Ahh, Markers null");
								return;
							}
							ARData.clearAndAddMarkers(contextData.getMarkers());
						}
					}
			);
		} catch (RejectedExecutionException rej) {
			Log.w(TAG, "Not running new download Runnable, queue is full.");
		} catch (Exception e) {
			Log.e(TAG, "Exception running download Runnable.",e);
		}
	}

	private static boolean download(NetworkDataSource source, double lat, double lon, double alt) {
		if (source==null) return false;

		String url = null;
		try {
			url = source.createRequestURL(lat, lon, alt, ARData.getRadius(), locale);    	
		} catch (NullPointerException e) {
			return false;
		}

		List<Marker> markers = null;
		try {
			markers = source.parse(url);
		} catch (NullPointerException e) {
			return false;
		}

		ARData.addMarkers(markers);
		return true;
	}
	
	

	private Handler newDataHandler = new Handler() { 
		/* (non-Javadoc) 
		 * @see android.os.Handler#handleMessage(android.os.Message) 
		 */ 
		@Override 
		public void handleMessage(Message msg) { 			
			runOnUiThread(new Runnable()
			{
				public void run() {
					ARData.clearAndAddMarkers(contextData.getMarkers());
				}
			});
		}
	};
}
