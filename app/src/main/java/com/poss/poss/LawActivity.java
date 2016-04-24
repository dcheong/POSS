package com.poss.poss;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class LawActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Spinner provinceSpinner;
    Spinner barangaySpinner;
    Spinner municipalitySpinner;
    Spinner daySpinner;
    Spinner monthSpinner;
    EditText searchText;
    lawAdapter adapter;
    Button searchButton;

    DBManager db;
    ListView lawList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_law);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ArrayList<Law> blankList = new ArrayList<>();
        lawList = (ListView) findViewById(R.id.lawList);
        db = new DBManager(getApplicationContext());
        db.start();
        adapter = new lawAdapter(this);
        adapter.setList(blankList);
        lawList.setAdapter(adapter);

        searchText = (EditText) findViewById(R.id.practice);
        searchButton = (Button) findViewById(R.id.search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String day = daySpinner.getSelectedItem().toString();
                String month = monthSpinner.getSelectedItem().toString();
                String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                String municipality = municipalitySpinner.getSelectedItem().toString();
                String province = provinceSpinner.getSelectedItem().toString();
                String barangay = barangaySpinner.getSelectedItem().toString();
                String practice = searchText.getText().toString();
                String date = day + "/" + month + "/" + year;
                if (practice.length() == 0) {
                    practice = null;
                }
                if (municipality.equals("(municipality)")) {
                    municipality = null;
                }
                if (province.equals("(province)")) {
                    province = null;
                }
                if (barangay.equals("(barangay)")) {
                    barangay = null;
                }
                if (day.equals("(day)") && month.equals("(month)")) {
                    date = null;
                } else if (day.equals("(day)")) {
                    day = "1";
                    date = day + "/" + month + "/" + year;
                } else if (month.equals("(month)")) {
                    month = "1";
                    date = day + "/" + month + "/" + year;
                }
                ArrayList<Law> lawResults = db.searchLaws(province, municipality, barangay, date, practice);
                adapter.setList(lawResults);
                adapter.notifyDataSetChanged();
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        provinceSpinner = (Spinner) findViewById(R.id.provSpinner);
        ArrayAdapter<CharSequence> provinceAdapter = ArrayAdapter.createFromResource(this, R.array.province_array, android.R.layout.simple_spinner_item);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provinceSpinner.setAdapter(provinceAdapter);

        municipalitySpinner = (Spinner) findViewById(R.id.munSpinner);
        ArrayAdapter<CharSequence> municipalityAdapter = ArrayAdapter.createFromResource(this, R.array.municipality_array, android.R.layout.simple_spinner_item);
        municipalityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        municipalitySpinner.setAdapter(municipalityAdapter);

        barangaySpinner = (Spinner) findViewById(R.id.barSpinner);
        ArrayAdapter<CharSequence> barangayAdapter = ArrayAdapter.createFromResource(this, R.array.barangay_array, android.R.layout.simple_spinner_item);
        barangayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        barangaySpinner.setAdapter(barangayAdapter);

        daySpinner = (Spinner) findViewById(R.id.daySpinner);
        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(this, R.array.day_array, android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(dayAdapter);

        monthSpinner = (Spinner) findViewById(R.id.monthSpinner);
        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(this, R.array.month_array, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.law, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.laws) {
        } else if (id == R.id.mapLink) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
class lawAdapter extends BaseAdapter {
    Context context;
    ArrayList<Law> lawResults;
    private static LayoutInflater inflater = null;

    public lawAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public void setList(ArrayList<Law> lawResults) {
        this.lawResults = lawResults;
    }
    @Override
    public int getCount() {
        return lawResults.size();
    }
    @Override
    public Object getItem(int position) {
        return lawResults.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null) {
            vi = inflater.inflate(R.layout.law_result, null);
        }
        TextView lawText = (TextView) vi.findViewById(R.id.law);
        TextView fineText = (TextView) vi.findViewById(R.id.fine);
        lawText.setText(lawResults.get(position).getPractice());
        fineText.setText(String.valueOf(lawResults.get(position).getFine()));
        return vi;
    }
}
