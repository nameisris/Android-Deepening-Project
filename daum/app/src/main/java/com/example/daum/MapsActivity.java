package com.example.daum;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double x;
    double y;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Intent 값 가져오기 (x와 y)
        Intent intent = getIntent();
        x = Double.parseDouble(intent.getStringExtra("x")); // String형인 x를 Double로 형변환하여 받아옴
        y = Double.parseDouble(intent.getStringExtra("y"));
        title = intent.getStringExtra("title"); // String형인 title을 getStringExtra() 메소드로 받아옴
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(y, x); // 위도, 경도

        mMap.addMarker(new MarkerOptions().position(sydney).title(title)); // 타이틀로 title을 줌
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        // 카메라 이동 (15만큼 zoom)
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }
}