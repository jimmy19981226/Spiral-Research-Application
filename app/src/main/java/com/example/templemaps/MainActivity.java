package com.example.templemaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import android.graphics.Color;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private List<Temple> temples;
    private List<Marker> markerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temples = Temple.loadTemplesFromJson(this, "temples.json");
        Log.d("MainActivity", "Context: " + this);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_map);
        mapFragment.getMapAsync(this);

        // Set the status bar color to white
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.WHITE);
        }


        /*temples.add(new Temple(new LatLng(unknownLat, unknownLng), "Busan Korea Temple",
                "Announcement: 2 October 2022\n", R.drawable.no_image));*/


        //Initialization of Bottom Nav
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setItemIconTintList(null);
        // Set the selected item listener
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_spiral) {
                    Log.d("Navigation", "Spiral selected");
                    // Start the SpiralActivity
                    startActivity(new Intent(MainActivity.this, SpiralActivity.class));
                    return true;
                } else if (id == R.id.navigation_map) {
                    Log.d("Navigation", "Map selected");
                    // Already in the map activity, do nothing or handle special cases
                    return true;
                } else if (id == R.id.navigation_list) {
                    Log.d("Navigation", "List selected");
                    // Start the ListActivity
                    startActivity(new Intent(MainActivity.this, ListAct.class));
                    return true;
                }
                return false;
            }
        });
        bottomNav.setSelectedItemId(R.id.navigation_map);

    }


    //Maps and Markers
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        //Markers for each Temple in the List
        for (Temple temple : temples) {
            // Check if the temple has valid coordinates
            if (temple.getLatitude() != 0.0 && temple.getLongitude() != 0.0) {
                MarkerOptions markerOptions = temple.toMarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                        .flat(true);
                markerOptions.anchor(0.5f, 0.5f);
                Marker marker = googleMap.addMarker(markerOptions);
                markerList.add(marker);
            } else {
                Log.e("MapReady", "Skipping marker creation for temple " + temple.getName() + " due to null coordinates");
            }
        }

        //Camera Position
        LatLng location = temples.get(0).getLocation();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12));

        //Listener for zoom changes
        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                //marker size will be based on zoom level
                float zoom = googleMap.getCameraPosition().zoom;
                for (Marker marker : markerList) {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
                    marker.setAnchor(0.5f / zoom, 0.5f / zoom);
                }
            }
        });

        // click Listener for Markers
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Close previous popup if open
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                // Find the corresponding temple for the clicked marker
                Temple clickedTemple = null;
                for (Temple temple : temples) {
                    if (temple.getLocation().equals(marker.getPosition())) {
                        clickedTemple = temple;
                        break;
                    }
                }

                // Display popup with temple's name and description
                if (clickedTemple != null) {
                    //Box with Temple information
                    showPopupWindow(clickedTemple);
                }

                // Return true to consume the event
                return true;
            }
        });
        // Click listener for the map to close the popup
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        });
    }

    private PopupWindow popupWindow;

    private void showPopupWindow(Temple temple) {
        if (popupWindow != null && popupWindow.isShowing()) {
            // Popup window is already showing, update its content and position
            updatePopupContent(temple);
        } else {
            // Popup window is not showing, create and show it
            createAndShowPopup(temple);
        }
    }

    private void createAndShowPopup(Temple temple) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        // Set temple information in the popup layout
        TextView templeName = popupView.findViewById(R.id.templeNameTextView);
        TextView templeInfo = popupView.findViewById(R.id.templeDescriptionTextView);
        ImageView templeImage = popupView.findViewById(R.id.templeImageView);
        templeName.setText(temple.getName());
        templeInfo.setText(temple.getDescription());
        templeImage.setImageResource(temple.getIconResourceId());
        String imageName = temple.getImageResourceId();
        int resId = getResources().getIdentifier(imageName, "drawable", getPackageName());
        templeImage.setImageResource(resId);

        // Create the popup window
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        popupWindow = new PopupWindow(popupView, width, height, true);

        // Set the following properties to prevent dismissal when clicking outside the popup window
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(false);

        // Measure the view to calculate its height
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupHeight = popupView.getMeasuredHeight();

        // Calculate the Y position for the popup window to appear above the bottom navigation bar
        int navigationBarHeight = getResources().getDimensionPixelSize(R.dimen.bottom_navigation_height);
        int popupYPosition = navigationBarHeight;

        // Show the popup window centered horizontally and above the bottom navigation bar
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, popupYPosition);
        // Set a dismiss listener to nullify the popup window reference
        popupWindow.setOnDismissListener(null);
    }

    private void updatePopupContent(Temple temple) {
        // Update temple information in the existing popup layout
        TextView templeName = popupWindow.getContentView().findViewById(R.id.templeNameTextView);
        TextView templeInfo = popupWindow.getContentView().findViewById(R.id.templeDescriptionTextView);
        ImageView templeImage = popupWindow.getContentView().findViewById(R.id.templeImageView);
        ImageView templeImageView = popupWindow.getContentView().findViewById(R.id.templeImageView);

        templeName.setText(temple.getName());
        templeInfo.setText(temple.getDescription());
        templeImage.setImageResource(temple.getIconResourceId());
        String imageName = temple.getImageResourceId();
        int resId = getResources().getIdentifier(imageName, "drawable", getPackageName());
        templeImageView.setImageResource(resId);
    }

}
