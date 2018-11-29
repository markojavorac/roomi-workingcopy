package com.example.roomi.roomi;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PersonnelSelector extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private FirebaseDatabase database;
    private DatabaseReference dbRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personnel_selector);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        setTitle(R.string.personnel);
        getDatabase();
        retrieveData();

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
                            //Do nothing since we are in the same screen
                        } else if (id == R.id.nav_security) {
                            // Goes to Security Activity
                        } else if (id == R.id.nav_settings) {
                            // Goes to Settings Page
                        } else if (id == R.id.nav_aboutus) {
                            // Displays the About Us page

                            Intent mAboutUs = new Intent(PersonnelSelector.this, AboutUs.class);
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
        dbRef = database.getReference("personnel");
    }

    private void retrieveData() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fetchPersonnel(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("roomi", "Data retrieval error...", databaseError.toException());
            }
        });
    }

    private void fetchPersonnel(DataSnapshot dataSnapshot) {
        List<PersonnelDatastructure> personnelList = new ArrayList<>();
        personnelList.clear();
        for (DataSnapshot personnelSnapShot: dataSnapshot.getChildren()) {
            PersonnelDatastructure personnel = personnelSnapShot.getValue(PersonnelDatastructure.class);
            personnelList.add(personnel);
        }
        generatePersonnelButtons(personnelList);
    }

    private void generatePersonnelButtons(List<PersonnelDatastructure> personnelList) {
        LinearLayout buttonContainer = findViewById(R.id.button_container);
        buttonContainer.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(900, 200);
        params.setMargins(0, 0, 0, 65);

        for (PersonnelDatastructure personnel: personnelList) {
            final String name = personnel.getName();
            final String avatarColour = personnel.getAvatarColour();
            final int accessLevel = personnel.getaccessLevel();
            Button button = new Button(this);
            button.setTag(personnel.getName());
            button.setBackgroundResource(R.drawable.element_background_dark);
            button.setLayoutParams(params);
            button.setText(personnel.getName());
            button.setTextSize(20);
            button.setTextColor(Color.WHITE);
            button.setTransformationMethod(null);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PersonnelSelector.this, PersonnelSettings.class);
                    intent.putExtra("name", name);
                    intent.putExtra("avatarColour", avatarColour);
                    intent.putExtra("accessLevel", accessLevel);
                    startActivity(intent);
                }
            });

            buttonContainer.addView(button);
        }

        Button addPersonnelButton = new Button(this);
        addPersonnelButton.setTag("add_personnel");
        addPersonnelButton.setBackgroundResource(R.drawable.element_background_dark);
        addPersonnelButton.setLayoutParams(params);
        addPersonnelButton.setText("+");
        addPersonnelButton.setTextSize(20);
        addPersonnelButton.setTextColor(Color.WHITE);
        addPersonnelButton.setTransformationMethod(null);

        addPersonnelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonnelSelector.this, PersonnelAdd.class);
                startActivity(intent);
            }
        });

        buttonContainer.addView(addPersonnelButton);
    }
}
