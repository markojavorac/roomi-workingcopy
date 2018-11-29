package com.example.roomi.roomi;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RoomSettings extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private TextView nameView;
    private EditText temperatureInput;
    private EditText brightnessInput;
    private Button submitButton;
    private Bundle extras;
    private String nameVal;

    private FirebaseDatabase database;
    private DatabaseReference dbRef;
    RoomDatastructure data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_settings);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        extras = getIntent().getExtras();
        nameVal = extras.getString("name");

        setTitle(nameVal);
        findViews();
        getDatabase();

        nameView.setText("Update Settings");
        temperatureInput.setHint("Current: " + extras.getInt("temperature") + getString(R.string.degrees_cel));
        brightnessInput.setHint("Current: " + extras.getInt("brightness") + getString(R.string.percent));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_hamburger);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        int id = menuItem.getItemId();

                        if (id == R.id.nav_home) {
                            Intent mAboutUs = new Intent(RoomSettings.this, HomeActivity.class);
                            startActivity(mAboutUs);
                        } else if (id == R.id.nav_security) {
                            // Goes to Security Activity
                        } else if (id == R.id.nav_settings) {
                            // Goes to Settings Page
                        } else if (id == R.id.nav_aboutus) {
                            // Displays the About Us page

                            Intent mAboutUs = new Intent(RoomSettings.this, AboutUs.class);
                            startActivity(mAboutUs);

                        } else if (id == R.id.nav_logout) {
                            // Logs out and displays the Log In Screen

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        } else if (id == R.id.nav_exit) {
                            finishAffinity();
                        }

                        mDrawerLayout.closeDrawers();
                        return true;
                    }});

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateData()) {
                    int temperature = Integer.parseInt(temperatureInput.getText().toString());
                    int brightness = Integer.parseInt(brightnessInput.getText().toString());
                    dbRef.child("temperature").setValue(temperature);
                    dbRef.child("brightness").setValue(brightness);
                    Toast toast = Toast.makeText(getApplicationContext(), "Updated " + nameVal, Toast.LENGTH_LONG);
                    toast.show();
                    finish();
                }
            }
        });

//        TODO: Add Cancel button if user does not want to change values
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getDatabase() {
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("rooms/" + nameVal);
    }

    private void findViews() {
        temperatureInput = findViewById(R.id.update_temperature_input);
        brightnessInput = findViewById(R.id.update_brightness_input);
        nameView = findViewById(R.id.room_name);
        submitButton = findViewById(R.id.update_room_button);
    }

    private boolean validateData() {
        int temperature = 0;
        int brightness = 0;

        try {
            temperature = Integer.parseInt(temperatureInput.getText().toString());
            brightness = Integer.parseInt(brightnessInput.getText().toString());
        } catch (Exception e) {
            Log.d("IntParse", e.toString());
        }

        if (temperature < 15 || temperature > 30) return false;
        if (brightness < 0 || brightness > 100) return false;
        return true;
    }
}