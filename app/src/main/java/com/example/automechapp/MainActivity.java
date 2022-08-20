package com.example.automechapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    public static final String TAG = "Mechanic";
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.navigation);
        setupDrawerContent(navigationView);

        loadFragment(new BreakdownsFragment());
        navigationView.setCheckedItem(R.id.nav_breakdowns);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.standart_app_bar, menu);
        super.onCreateOptionsMenu(menu);

        return true;
    }

    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_layout, fragment);
        ft.addToBackStack("Main");
        ft.commit();
    }

    @SuppressLint("NonConstantResourceId")
    public void selectDrawerItem(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_breakdowns:
                loadFragment(new BreakdownsFragment());
                break;
            case R.id.nav_cars:
                loadFragment(new CarsFragment());
                break;
            case R.id.nav_owners:
                loadFragment(new OwnersFragment());
                break;
            }

        // Highlight the selected item has been done by NavigationView
        item.setChecked(true);
        // Set action bar title
        // setTitle(item.getTitle());
        // Close the navigation drawer
        drawerLayout.closeDrawers();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static Context getContext() {
        return context;
    }
}
