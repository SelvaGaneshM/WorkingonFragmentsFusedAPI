package com.selvaganesh.fusedapi.workingonfragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

/**
 * Created by Selva Ganesh M on 2/19/2018.
 */

public class TutorialFragment extends Fragment implements OnMapReadyCallback,
        LocationListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    Context mContext;
    //Map
    LatLng myLocation;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    int value;
    GoogleMap mMap;
    SupportMapFragment mapFragment;
    Marker currentMarker;
    CameraPosition position;
    LatLng location;
    Double crtLat;
    Double crtLng;
    Double dbl;

    Double latitude, longitude;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tutorial_layout, null);
        mContext = getContext();
        initRecyclerViews(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // Android M Permission check
            //requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Keyname.MY_PERMISSIONS_ACCESS_FINE);
        } else {
            initMap();
            MapsInitializer.initialize(mContext);
        }
        return view;

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @SuppressWarnings("MissingPermission")
    void initMap() {
        if (mMap == null) {
            if (isAdded()) {
                FragmentManager fm = getChildFragmentManager();
                mapFragment = ((SupportMapFragment) fm.findFragmentById(R.id.map));
                mapFragment.getMapAsync(this);
            }
        }
    }

    private void initRecyclerViews(View view) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (value == 0) {
            value = 1;
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(16).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            value++;
        }
        crtLat = location.getLatitude();
        crtLng = location.getLongitude();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(mContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
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
    public void onCameraIdle() {
        position = mMap.getCameraPosition();
        myLocation = position.target;

        latitude = Double.valueOf(String.valueOf(myLocation.latitude));
        longitude = Double.valueOf(String.valueOf(myLocation.longitude));

        getAddress(myLocation.latitude, myLocation.longitude);


        if (currentMarker != null) {
            currentMarker.remove();
        }
        currentMarker = mMap.addMarker(new MarkerOptions()
                .position(position.target)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        //      mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 13f));


    }

    public void getAddress(double latitude, double longitude) {
        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if ((addresses != null) && !addresses.isEmpty()) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                if (returnedAddress.getMaxAddressLineIndex() > 0) {
                    for (int j = 0; j < returnedAddress.getMaxAddressLineIndex(); j++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(j)).append("");
                    }
                } else {
                    strReturnedAddress.append(returnedAddress.getAddressLine(0)).append("");
                }
                Toast.makeText(mContext, strReturnedAddress.toString(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("getAddress", "getAddress: " + e);
        }
    }

    @Override
    public void onCameraMove() {
        position = mMap.getCameraPosition();
        mMap.clear();
        // Toast.makeText(mContext,"Getting Addreess",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap = googleMap;

        setupMap();
        String mlatlat = String.valueOf(latitude);
        String mlatlon = String.valueOf(longitude);
        if (mlatlat != null) {
            if (!mlatlat.isEmpty()) {
                LatLng location = new LatLng(Double.parseDouble(mlatlat), Double.parseDouble(mlatlon));

                if (location != null) {
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(16).build();
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        }
        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
            } else {
                //Request Location Permission
            }
        } else {
            buildGoogleApiClient();
        }
    }


    // set up settings for map
    @SuppressWarnings("MissingPermission")
    private void setupMap() {
        if (mMap != null) {
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.setBuildingsEnabled(true);
            mMap.setOnCameraMoveListener(this);
            mMap.setOnCameraIdleListener(this);
            mMap.getUiSettings().setRotateGesturesEnabled(false);
            mMap.getUiSettings().setTiltGesturesEnabled(false);
        }
    }


}
