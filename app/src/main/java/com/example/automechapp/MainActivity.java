package com.example.automechapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.automechapp.breakdown.BreakdownsFragment;
import com.example.automechapp.car.CarsFragment;
import com.example.automechapp.owner.OwnersFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    public static final String TAG = "Mechanic";
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    int code;
    public static String APP_PREFERENCES_CODE = "code";
    public static final String APP_PREFERENCES = "automechapp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.navigation);
        setupDrawerContent(navigationView);

        SharedPreferences settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        checkItem(settings.getInt(APP_PREFERENCES_CODE, R.id.nav_breakdowns));
        navigationView.setCheckedItem(code);
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

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt(
                APP_PREFERENCES_CODE,
                R.id.nav_breakdowns
        );
        editor.apply();
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_layout, fragment);
        ft.addToBackStack("Main");
        ft.commit();
    }

    @SuppressLint("NonConstantResourceId")
    public void checkItem(int code) {
        this.code = code;
        switch (code){
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
    }

    @SuppressLint("NonConstantResourceId")
    public void selectDrawerItem(MenuItem item) {
        checkItem(item.getItemId());

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

    public static Context getContext() {
        return context;
    }
}
