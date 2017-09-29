package kuchingitsolution.betterpepperboard.map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import android.Manifest;
import android.widget.Toast;

import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.helper.BottomSheetDialogFragmentMap;
import kuchingitsolution.betterpepperboard.helper.Session;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    ArrayList<MapsModel> locations = new ArrayList<MapsModel>();
    private MarkerOptions options = new MarkerOptions();
    Session session;
    public HashMap<String, Integer> params = new HashMap<String, Integer>();
    public HashMap<String, String> image = new HashMap<String, String>();
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    BottomSheetBehavior bottomSheetBehavior;
    BottomSheetDialog bottomSheetDialog;
    TextView title, desc;
    RelativeLayout bottomsheet;
    int report_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if(getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        session = new Session(this);

        title = (TextView) findViewById(R.id.post_title);
        desc = (TextView) findViewById(R.id.post_description);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void getComplaint(){

        Session session = new Session(this);
        final String userID = session.getUserID();

        class getNewsFeed extends AsyncTask<String, String, String> {

            ProgressDialog loading;
            Context context;

            private getNewsFeed(Context context){
                this.context = context;
                loading = new ProgressDialog(context);
            }

            @Override
            protected void onPreExecute() {
                loading.setMessage("Loading News Feed");
                loading.setCancelable(false);
                loading.show();
            }

            @Override
            protected void onPostExecute(String result) {
                loading.dismiss();
//                Toast.makeText(MapsActivity.this, result, Toast.LENGTH_SHORT).show();

                MapsModel location = new MapsModel();
                location.setLatitude(1.4693344);
                location.setLongitude(110.4275608);
                location.setReportID(1);
                location.setReportTitle("Flash flood in this branch");
                location.setReportDescription("Drainage system in this branch blocked");
                location.setImgLink("http://www.nssl.noaa.gov/education/svrwx101/floods/img/nws-flash.jpg");
                locations.add(location);

                location = new MapsModel();
                location.setLatitude(1.4657508);
                location.setLongitude(110.420327);
                location.setReportID(2);
                location.setReportTitle("Staff attitude");
                location.setReportDescription("Irresponsible of staff");
                location.setImgLink("https://cdn1.businessmanagementdaily.com/res/images/bmd_homepage/iStock_BoredBusinessWoman_350.jpg");
                locations.add(location);

                addMarkerOnMap(locations);

//                try {
//                    JSONArray decodedResult = new JSONArray(result);
//                    for (int i = 0; i<decodedResult.length(); i++){
//                        JSONObject json_data = decodedResult.getJSONObject(i);
//                        Locations location = new Locations();
//                        location.setLatitude(json_data.getDouble("location_latitute"));
//                        location.setLongitude(json_data.getDouble("location_longitute"));
//                        location.setReportID(json_data.getInt("ids"));
//                        location.setReportTitle(json_data.getString("report_Title"));
//                        location.setReportDescription(json_data.getString("report_Description"));
//                        locations.add(location);
//                    }
//                    addMarkerOnMap(locations);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }

            @Override
            protected String doInBackground(String... params) {
                String result;
                HashMap<String,String> param = new HashMap<String,String>();
                param.put("userID", userID);
                return "true";
//                result = rh.sendPostRequest(AppConfig.URL_GetReport, param);
//                return result;
            }
        }

        getNewsFeed u = new getNewsFeed(MapsActivity.this);
        u.execute();
    }

    public void addMarkerOnMap(ArrayList<MapsModel> locations){

//        for (int i = 0; i < locations.size(); i++){
//            mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(locations.get(i).getLatitude(), locations.get(i).getLongitude()))
//                .title(locations.get(i).getReportTitle()).snippet(locations.get(i).getReportDescription()).anchor(0.5f,0.5f));
//        }
        for (int i = 0; i < locations.size(); i++){
            options.position(new LatLng(locations.get(i).getLatitude(), locations.get(i).getLongitude()))
                    .title(locations.get(i).getReportTitle())
                    .snippet(locations.get(i).getReportDescription());
            Marker mr = mMap.addMarker(options);
            params.put(mr.getId(), locations.get(i).getReportID());
            image.put(mr.getId(), locations.get(i).getImgLink());
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
//                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if(params.get(marker.getId()) == null){
                    Toast.makeText(MapsActivity.this, "Current location", Toast.LENGTH_SHORT).show();
//                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                } else {
                    Log.d("YES", marker.isInfoWindowShown() + "");
                    marker.hideInfoWindow();
                    report_id = params.get(marker.getId());
                    String imageLink = image.get(marker.getId());
//                    title.setText(marker.getTitle());
//                    desc.setText(marker.getSnippet());
                    BottomSheetDialogFragmentMap bottomSheetDialogFragment = new BottomSheetDialogFragmentMap();
                    bottomSheetDialogFragment.setData(marker.getTitle(), marker.getSnippet(), report_id, imageLink);
                    bottomSheetDialogFragment.show(getSupportFragmentManager(), "Dialog");
//                    bottomSheetBehavior.setPeekHeight(60);
//                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }

                return true;
            }
        });
    }

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        getComplaint();

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//        bottomSheetBehavior.setPeekHeight(0);

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            //You can add here other case statements according to your requirement.
        }
    }
}
