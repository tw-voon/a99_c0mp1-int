package kuchingitsolution.betterpepperboard.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.helper.BottomSheetDialogFragmentMap;
import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.helper.DB_Offline;
import kuchingitsolution.betterpepperboard.helper.Session;
import kuchingitsolution.betterpepperboard.helper.Utility;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    ArrayList<MapsModel> data = new ArrayList<MapsModel>();
    private MarkerOptions options = new MarkerOptions();
    public HashMap<String, String> params = new HashMap<>();
    public HashMap<String, String> image = new HashMap<String, String>();
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    TextView title, desc;
    Session session;
    String report_id;
    DB_Offline db_offline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if(getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        session = new Session(this);
        db_offline = new DB_Offline(this);

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

        final String userID = session.getUserID();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_GET_LOCATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Status: ", response);
                        process_geolocation(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("Error", error.toString());

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("user_id",session.getUserID());
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void process_geolocation(String result){
        try {
            JSONArray jsonArray = new JSONArray(result);
            int length = jsonArray.length();

            for (int i = 0; i < length ; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONArray reports = jsonObject.getJSONArray("reports");
                int report_length = reports.length();
                for (int j = 0 ; j < report_length ; j++){
                    JSONObject single_report = reports.getJSONObject(j);
                    JSONObject media = single_report.getJSONObject("media");
                    MapsModel mapsModel = new MapsModel();
                    mapsModel.setLocation_id(jsonObject.getString("id"));
                    mapsModel.setLatitude(jsonObject.getDouble("lat"));
                    mapsModel.setLongitude(jsonObject.getDouble("lon"));
                    mapsModel.setLocation_name(jsonObject.getString("name"));
                    mapsModel.setReportID(single_report.getString("id"));
                    mapsModel.setReportTitle(single_report.getString("title"));
                    mapsModel.setReportDescription(single_report.getString("description"));
                    mapsModel.setImgLink(media.getString("link"));
                    mapsModel.setCreated_at(single_report.getString("created_at"));
                    data.add(mapsModel);
                    db_offline.insertMap(mapsModel);
                }
            }
            addMarkerOnMap(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void addMarkerOnMap(ArrayList<MapsModel> locations){

        for (int i = 0; i < locations.size(); i++){
            options.position(new LatLng(locations.get(i).getLatitude(), locations.get(i).getLongitude()))
                    .title(locations.get(i).getLocation_name());
            Marker mr = mMap.addMarker(options);
            params.put(mr.getId(), locations.get(i).getLocation_id());
            image.put(mr.getId(), locations.get(i).getImgLink());
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if(params.get(marker.getId()) == null){
                    Toast.makeText(MapsActivity.this, "Current location", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("YES", marker.isInfoWindowShown() + "");
                    marker.hideInfoWindow();
                    report_id = params.get(marker.getId());
                    String imageLink = image.get(marker.getId());
                    BottomSheetDialogFragmentMap bottomSheetDialogFragment = new BottomSheetDialogFragmentMap();
                    bottomSheetDialogFragment.setData(report_id);
                    bottomSheetDialogFragment.show(getSupportFragmentManager(), "Dialog");
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
            } else
                Utility.checklocationPermission(this);
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
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }

        }
    }
}
