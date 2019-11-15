package com.example.hydRate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class FountainsFragment extends Fragment implements OnMapReadyCallback {

    private DatabaseReference database;
    private StorageReference storage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();
        return inflater.inflate(R.layout.fragment_fountains, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //    GoogleMap map;
        MapView mapview = view.findViewById(R.id.mapview);
        if (mapview != null) {
            mapview.onCreate(null);
            mapview.onResume();
            mapview.getMapAsync(this);
        }

        FloatingActionButton ftnBtn = view.findViewById(R.id.addFtnBtn);
        ftnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoCreateFountain = new Intent(getContext(), CreateFountain.class);
                startActivity(gotoCreateFountain);
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        MapsInitializer.initialize(Objects.requireNonNull(getContext()));
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Get location and set camera to that location at default zoom
        setLocation(googleMap);

        // loadFountains in this area
        loadFountains(googleMap, database);
    }

    private void loadFountains(final GoogleMap googleMap, final DatabaseReference database) {
        // Get fountains from the database in this area
        Query query = database.child("fountains").orderByChild("location/latitude");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot fountain: dataSnapshot.getChildren()) {
                    Fountain ftn = createFountain(fountain);
                    googleMap.addMarker(new MarkerOptions().position(ftn.getLocation()).title(ftn.getName()));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("error", String.valueOf(databaseError));
            }
        });
    }

    private Fountain createFountain(DataSnapshot ftnData) {
        Fountain ftn = new Fountain();
        ftn.setFtnID(ftnData.getKey());
        ftn.setName("Water Fountain");
        if (ftnData.child("location").exists() && ftnData.child("location").child("latitude").exists() && ftnData.child("location").child("longitude").exists()) {
            ftn.setLocation(new LatLng(ftnData.child("location").child("latitude").getValue(Double.class), ftnData.child("location").child("longitude").getValue(Double.class)));
        }

        ftn.setPicID(ftnData.child("picture").getValue(String.class));

        return ftn;
    }

    private void setLocation(GoogleMap googleMap) {
        // Get user location
        // Set camera position to the user's location at the default zoom level
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(40.4259, -86.9081), 18, 0, 0))); // Dummy location
    }
}
