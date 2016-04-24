package com.poss.poss;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.overlays.BasicInfoWindow;
import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.bonuspack.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private MapView m_mapView;
    private int MAP_DEFAULT_ZOOM = 11;
    private double MAP_DEFAULT_LATITUDE = 14.157882;
    private double MAP_DEFAULT_LONGITUDE = 121.025391;

    private ArrayList<Polygon> mpaRegions;
    private LocationListener locListener;
    private LocationManager locManager;
    private boolean listeningForLocation = false;

    private double longitude = 1000;
    private double latitude = 1000;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        if (isNetworkAvailable(getApplicationContext())) {
            Log.d("network available", "Network Is Available");
            startLocationUpdates();
            listeningForLocation = true;
        }

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            instantiateMap();



/*
        final PhillyLawsManager lols = new PhillyLawsManager(getApplicationContext());
        lols.start();
        int id = lols.newLaw("Batangas", "San Juan", "Laiya-Aplaya", 7,7, 14,14, 1975, 3000, "Failure to submit required reports. - The owner, master or operator of a fishing boat who fails to submit a required report within thirty (30) days after due date shall be fined in an amount not exceeding five (P5.00) pesos.", 5);
        id = lols.newLaw("Batangas", "San Juan", "", 4,4, 15, 30, 2016, 2016, "Vessel engaging in fishing without license. - The owner, master or operator of a fishing boat engaging in fishing operations without a license shall be fined in an amount not exceeding one thousand pesos (P1,000.00) for each month or fraction thereof of operation.", 1000);
        id = lols.newLaw("Batangas", "", "", 7, 7, 14, 14, 1975, 3000, "SEC. 41.  Compromise. - With the approval of the Secretary, the Director may, at any stage of the proceedings, compromise any case arising under any provision of this decree, subject to the following schedule of administrative fines: a) Vessel entering fishery reserve or closed areas. - Any vessel, licensed or unlicensed, entering a fishery reserve or a declared closed area for the purpose of fishing shall be fined in a sum not exceeding five thousand (P5,000.00) pesos.", 5000);
        id = lols.newLaw("", "", "", 0, 0, 0, 0, 0, 3000, "SEC. 101. Violation of Catch Ceilings. - It shall be unlawful for any person to fish in violation of catch ceilings as determined by the Department. Violation of the provision of this section shall be punished by imprisonment of six (6) months and one (1) day to six (6) years and/or a fine of Fifty Thousand Pesos (P50,000.00) and forfeiture of the catch, and fishing equipment used and revocation of license.", 50000);
        lols.close();
*/


        }

    public void startLocationUpdates() {
        locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                Log.d("Location changed", latitude + " " + longitude);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

// Register the listener with the Location Manager to receive location updates

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);


    }

    public void instantiateMap() {
        m_mapView = (MapView) findViewById(R.id.map);
        m_mapView.setMultiTouchControls(false);
        m_mapView.setClickable(true);
        m_mapView.setUseDataConnection(false);
        m_mapView.getController().setZoom(MAP_DEFAULT_ZOOM);
        m_mapView.getController().setCenter(
                new GeoPoint(MAP_DEFAULT_LATITUDE, MAP_DEFAULT_LONGITUDE));
        m_mapView.setTileSource(new CustomMapTile(getApplicationContext()));
        mpaRegions = new ArrayList<>();
        drawMPARegions();
    }


    public void drawMPARegions() {
        try {

            JSONArray safeZones = new JSONArray(loadJSONFromAsset());
            for (int i = 0; i < safeZones.length(); i++) {
                JSONArray shape = safeZones.getJSONArray(i);
                ArrayList<GeoPoint> shapePoints = new ArrayList<>();
                for (int j = 0; j < shape.length(); j++) {
                    JSONArray point = shape.getJSONArray(j);
                    GeoPoint tempPoint = new GeoPoint(point.getDouble(1), point.getDouble(0));
                    shapePoints.add(tempPoint);
                }

                Polygon tempPoly = new Polygon(this);
                tempPoly.setPoints(shapePoints);
                tempPoly.setStrokeWidth(2);
                tempPoly.setFillColor(0x12121212);
                tempPoly.setStrokeColor(Color.RED);
                tempPoly.setInfoWindow(new BasicInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, m_mapView));
                tempPoly.setTitle("MPA ZONE");
                mpaRegions.add(tempPoly);
                m_mapView.getOverlays().add(tempPoly);
            }
            m_mapView.invalidate();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getAssets().open("safeZones.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_weather) {
            if (isNetworkAvailable(getApplicationContext()) && listeningForLocation) {
                Weather weather = new Weather();
                String report = Weather.fetchWeather(latitude, longitude);
                if (report != null) {
                    try {
                        JSONObject reportObject = new JSONObject(report);
                        JSONArray weatherArray = (JSONArray) reportObject.get("weather");
                        JSONObject mainObject = (JSONObject) reportObject.get("main");
                        JSONObject desc = (JSONObject) weatherArray.get(0);
                        String description = desc.getString("description");
                        double temperature = Double.parseDouble(mainObject.getString("temp")) - 273.15;
                        String tempString = String.format("%3.1f", temperature);
                        Snackbar.make(MainActivity.this.getCurrentFocus(), description + ". Temperature: " + tempString + " Celsius." , Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Snackbar.make(MainActivity.this.getCurrentFocus(), "Could not retrieve weather.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.laws) {
            Intent i = new Intent(MainActivity.this, LawActivity.class);
            startActivity(i);
        } else if (id == R.id.trip) {

        } else if (id == R.id.weather) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public boolean isNetworkAvailable(final Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }
}