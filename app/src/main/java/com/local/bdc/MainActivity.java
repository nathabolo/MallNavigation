package com.local.bdc;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.customlbs.coordinates.GeoCoordinate;
import com.customlbs.library.IndoorsException;
import com.customlbs.library.IndoorsFactory;
import com.customlbs.library.IndoorsLocationListener;
import com.customlbs.library.callbacks.LoadingBuildingStatus;
import com.customlbs.library.callbacks.ZoneCallback;
import com.customlbs.library.model.Building;
import com.customlbs.library.model.Zone;
import com.customlbs.shared.Coordinate;
import com.customlbs.surface.library.IndoorsSurfaceFactory;
import com.customlbs.surface.library.IndoorsSurfaceFragment;
import com.customlbs.surface.library.ViewMode;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;



import java.util.List;

import static android.text.Layout.Alignment.ALIGN_CENTER;

/**
 * Sample Android project, powered by indoo.rs :)
 *
 * For an API reference go to:
 *    https://my.indoo.rs/javadoc/
 *
 * For an implementers guide go to:
 *    https://indoors.readme.io/docs/getting-started-with-indoors-android-guide
 *
 * @author indoo.rs
 *
 */
public class MainActivity extends FragmentActivity implements IndoorsLocationListener {

	public static final int REQUEST_CODE_PERMISSIONS = 34168; //Random request code, use your own
	public static final int REQUEST_CODE_LOCATION = 58774; //Random request code, use your own
	public static final int ALIGN = 50;
	private IndoorsSurfaceFragment indoorsSurfaceFragment;
	private Toast progressToast;
	private static int lastProgress = 0;

	private Spinner spinner1;
	ActionBar actionbar;
	TextView textview;
	ActionBar.LayoutParams layoutparams;
	private Building building;
	private IndoorsSurfaceFragment indoorsFragment;
	private ArrayList<Coordinate> poits;

	/** RUN YOU MODIFICATION HERE
     *
     * @param savedInstanceState
     */
        @Override

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// We start by requesting permissions from the user
           setContentView(R.layout.activity_main);

           getActionBar().setDisplayHomeAsUpEnabled(true);

			ActionBarTitleGravity();
		// requestPermissionsFromUser();
//            Spinner staticSpinner = (Spinner) findViewById(R.id.static_spinner);
//            // Create an ArrayAdapter using the string array and a default spinner
//            ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
//                    .createFromResource(this, R.array.mall_names,
//                            android.R.layout.simple_spinner_item);
//
//            // Specify the layout to use when the list of choices appears
//            staticAdapter
//                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//            // Apply the adapter to the spinner
//            staticSpinner.setAdapter(staticAdapter);
            requestPermissionsFromUser();


        }

	private void ActionBarTitleGravity() {
		actionbar = getActionBar();

		textview = new TextView(getApplicationContext());

		layoutparams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

		textview.setLayoutParams(layoutparams);

		textview.setText("Natstem Tech");

		textview.setTextColor(Color.WHITE);

		textview.setGravity(Gravity.CENTER_HORIZONTAL);

		textview.setTextSize(20);

		actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		actionbar.setCustomView(textview);
	}

	private void requestPermissionsFromUser() {
		/**
		 * Since API level 23 we need to request permissions for so called dangerous permissions from the user.
		 *
		 * You can see a full list of needed permissions in the Manifest File.
		 */
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			int permissionCheckForLocation = ContextCompat.checkSelfPermission(
			                                     MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);

			if (permissionCheckForLocation != PackageManager.PERMISSION_GRANTED) {
				requestPermissions(
				    new String[] {
				        Manifest.permission.ACCESS_COARSE_LOCATION
				    },
				    REQUEST_CODE_PERMISSIONS);
			} else {
				//If permissions were already granted,
				// we can go on to check if Location Services are enabled.
				checkLocationIsEnabled();
			}
		} else {
			//Continue loading Indoors if we don't need user-settable-permissions.
			// In this case we are pre-Marshmallow.
			continueLoading();
		}
	}

	/**
	 * The Android system calls us back
	 * after the user has granted permissions (or denied them)
	 * @param requestCode
	 * @param permissions
	 * @param grantResults
	 */
	@TargetApi(Build.VERSION_CODES.M)
	@Override
	public void onRequestPermissionsResult(int requestCode,
	                                       @NonNull String[] permissions,
	                                       @NonNull int[] grantResults) {
		if (requestCode == REQUEST_CODE_PERMISSIONS) {
			// Since we have requested multiple permissions,
			// we need to check if any were denied
			for (int grant : grantResults) {
				if (grant == PackageManager.PERMISSION_DENIED) {
					if (shouldShowRequestPermissionRationale(
					            Manifest.permission.ACCESS_COARSE_LOCATION)) {
						// User has *NOT* allowed us to use ACCESS_COARSE_LOCATION
						// permission on first try. This is the last chance we get
						// to ask the user, so we explain why we want this permission
						Toast.makeText(this,
						               "Location is used for Bluetooth location",
						               Toast.LENGTH_SHORT).show();
						// Re-ask for permission
						requestPermissionsFromUser();
						return;
					}

					// The user has finally denied us permissions.
					Toast.makeText(this,
					               "Cannot continue without permissions.",
					               Toast.LENGTH_SHORT).show();
					this.finishAffinity();
					return;
				}
			}

			checkLocationIsEnabled();
		}
	}

	private void checkLocationIsEnabled() {
		// On android Marshmallow we also need to have active Location Services (GPS or Network based)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			boolean isNetworkLocationProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			boolean isGPSLocationProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			if (!isGPSLocationProviderEnabled && !isNetworkLocationProviderEnabled) {
				// Only if both providers are disabled we need to ask the user to do something
				Toast.makeText(this, "Location is off, enable it in system settings.", Toast.LENGTH_LONG).show();
				Intent locationInSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				this.startActivityForResult(locationInSettingsIntent, REQUEST_CODE_LOCATION);
			} else {
				continueLoading();
			}
		} else {
			continueLoading();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_LOCATION) {
			// Check if the user has really enabled Location services.
			checkLocationIsEnabled();
		}
	}

	// At this point we can continue to load the Indoo.rs SDK as we did with previous android versions
	private void continueLoading() {
		IndoorsFactory.Builder indoorsBuilder = initializeIndoorsLibrary();
		indoorsSurfaceFragment = initializeIndoorsSurface(indoorsBuilder);
		setSurfaceFragment(indoorsSurfaceFragment);
	}

	private IndoorsFactory.Builder initializeIndoorsLibrary() {
		/**
		 * This will initialize the builder for the Indoo.rs object
		 */
		IndoorsFactory.Builder indoorsBuilder = new IndoorsFactory.Builder();
		indoorsBuilder.setContext(this);

        /**     Add The drop down here
         *
         */







    /**
     * TODO: replace this with your API-key
     * This is your API key as set on https://api.indoo.rs
     */
		indoorsBuilder.setApiKey("45588956-2896-40bd-b68e-ff812ebd0f53");
		/**
		 * TODO: replace 12345 with the id of the building you uploaded to our cloud using the MMT
		 * This is the ID of the Building as shown in the desktop Measurement Tool (MMT)
		 */
		indoorsBuilder.setBuildingId((long) 1302555959);
		// callback for indoo.rs-events
		indoorsBuilder.setUserInteractionListener(this);
		return indoorsBuilder;
	}

	private IndoorsSurfaceFragment initializeIndoorsSurface(IndoorsFactory.Builder indoorsBuilder) {
		/**
		 * This will initialize the UI from Indoo.rs which is called IndoorsSurface.
		 * The implementation is the IndoorsSurfaceFragment
		 *
		 * If you use your own map view implementation you don't need the Surface.
		 * https://indoors.readme.io/docs/localisation-without-ui
		 *
		 */
		IndoorsSurfaceFactory.Builder surfaceBuilder = new IndoorsSurfaceFactory.Builder();
		surfaceBuilder.setIndoorsBuilder(indoorsBuilder);
		return surfaceBuilder.build();
	}

	private void setSurfaceFragment(final IndoorsSurfaceFragment indoorsFragment) {
		/**
		 * This will add the IndoorsSurfaceFragment to the current layout
		 */
		// http://stackoverflow.com/questions/33264031/calling-dialogfragments-show-from-within-onrequestpermissionsresult-causes/34204394#34204394
		// http://stackoverflow.com/questions/17184653/commitallowingstateloss-in-fragment-activities
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
				transaction.add(android.R.id.content, indoorsFragment, "indoors");
				transaction.commit();
			}
		});
	}

	public void setZones(java.util.ArrayList<Zone> zones){

		indoorsFragment.setViewMode(ViewMode.HIGHLIGHT_ALL_ZONES);

		IndoorsFactory.getInstance().getZones(building, new ZoneCallback() {
			@Override
			public void setZones(ArrayList<Zone> zones) {
				return;
			}
		});
    }

    public java.util.ArrayList<Coordinate> getZonePoints(){
       // indoorsFragment.setViewMode(ViewMode.HIGHLIGHT_ALL_ZONES);
	    return poits;

    }



	@Override
	public void positionUpdated(Coordinate userPosition, int accuracy) {
		/**
		 * Is called each time the Indoors Library calculated a new position for the user
		 * If Lat/Lon/Rotation of your building are set correctly you can calculate a
		 * GeoCoordinate for your users current location in the building.
		 */
		GeoCoordinate geoCoordinate = indoorsSurfaceFragment.getCurrentUserGpsPosition();

		if (geoCoordinate != null) {
			Toast.makeText(
			    this,
			    "User is located at " + geoCoordinate.getLatitude() + ","
			    + geoCoordinate.getLongitude(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void loadingBuilding(LoadingBuildingStatus loadingBuildingStatus) {
		// indoo.rs is still downloading or parsing the requested building
		// Inform the User of Progress
		showDownloadProgressToUser(loadingBuildingStatus.getProgress());
	}

	@Override
	public void buildingLoaded(Building building) {
		// Fake a 100% progress to your UI when you receive info that the download is finished.
		showDownloadProgressToUser(100);
		// indoo.rs SDK successfully loaded the building you requested and
		// calculates a position now
		Toast.makeText(
		    this,
		    "Building is located at " + building.getLatOrigin() / 1E6 + ","
		    + building.getLonOrigin() / 1E6, Toast.LENGTH_SHORT).show();
	}

	private void showDownloadProgressToUser(int progress) {
		if (progress % 10 == 0) { // Avoid showing too many values.
			if (progress > lastProgress) {
				lastProgress = progress; // Avoid showing same value multiple times.

				if (progressToast != null) {
					progressToast.cancel();
				}

				progressToast = Toast.makeText(this, "Building downloading : "+progress+"%", Toast.LENGTH_SHORT);
				progressToast.show();
			}
		}
	}

	@Override
	public void onError(IndoorsException indoorsException) {
		Toast.makeText(this, indoorsException.getMessage(), Toast.LENGTH_LONG).show();
	}

	@Override
	public void changedFloor(int floorLevel, String name) {
		// user changed the floor
	}

	@Override
	public void leftBuilding(Building building) {
		// Deprecated
	}

	@Override
	public void buildingReleased(Building building) {
		// Another building was loaded, you can release any resources related to linked building
	}

	@Override
	public void orientationUpdated(float orientation) {
		// user changed the direction he's heading to
	}

	@Override
	public void enteredZones(List<Zone> zones) {
		// user entered one or more zones
		IndoorsFactory.getInstance().getZones(building, new ZoneCallback() {
			@Override
			public void setZones(ArrayList<Zone> zones) {
			}
		});

//		building.getFloorById((long) 1302555959);
//		indoorsFragment.setViewMode(ViewMode.HIGHLIGHT_CURRENT_ZONE);
//		indoorsFragment.setViewMode(ViewMode.HIDE_USER_POSITION);


	}

	@Override
	public void buildingLoadingCanceled() {
		// Loading of building was cancelled
	}

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }



    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mybutton) {
            // do something here
        } else if (id == R.id.mybutton2){

		} else if (id == R.id.mybutton3){
			Intent searchIntent = new Intent(getApplicationContext(), AccountSettings.class);
			startActivity(searchIntent);
			return true;

		} else if (id == R.id.nav_setting){
			Intent setting = new Intent(getApplicationContext(), AccountSettings.class);
			startActivity(setting);
			return true;
		} else if (id == R.id.nav_position){
        	//Display the position in google maps
		} else if (id == R.id.nav_cancel){
        	//Cancel Route
		} else if (id == R.id.home){
        	finish();

        	return true;
		}
        return super.onOptionsItemSelected(item);
    }

}
