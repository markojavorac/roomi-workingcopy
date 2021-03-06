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

public class SecurityRoomSelector extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private FirebaseDatabase database;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_room_selector);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        setTitle(R.string.rooms);
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

                            Intent mAboutUs = new Intent(SecurityRoomSelector.this, AboutUs.class);
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
        dbRef = database.getReference("rooms");
    }

    private void retrieveData() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fetchRooms(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("roomi", "Data retrieval error...", databaseError.toException());
            }
        });
    }

    private void fetchRooms(DataSnapshot dataSnapshot) {
        List<RoomDatastructure> roomList = new ArrayList<>();
        roomList.clear();
        for (DataSnapshot roomSnapShot: dataSnapshot.getChildren()) {
            RoomDatastructure room = roomSnapShot.getValue(RoomDatastructure.class);
            roomList.add(room);
        }
        generateRoomButtons(roomList);
    }

    private void generateRoomButtons(List<RoomDatastructure> roomList) {
        LinearLayout buttonContainer = findViewById(R.id.button_container);
        buttonContainer.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(900, 200);
        params.setMargins(0, 0, 0, 65);

        for (RoomDatastructure room: roomList) {
            final String name = room.getName();
            final int accessLevel = room.getAccessLevel();
            Button button = new Button(this);
            button.setTag(room.getName());
            button.setBackgroundResource(R.drawable.element_background_dark);
            button.setLayoutParams(params);
            button.setText(room.getName());
            button.setTextSize(20);
            button.setTextColor(Color.WHITE);
            button.setTransformationMethod(null);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SecurityRoomSelector.this, SecurityRoomSettings.class);
                    intent.putExtra("name", name);
                    intent.putExtra("accessLevel", accessLevel);
                    startActivity(intent);
                }
            });

            buttonContainer.addView(button);
        }

        Button addSecurityRoomButton = new Button(this);
        addSecurityRoomButton.setTag("add_room");
        addSecurityRoomButton.setBackgroundResource(R.drawable.element_background_dark);
        addSecurityRoomButton.setLayoutParams(params);
        addSecurityRoomButton.setText("+");
        addSecurityRoomButton.setTextSize(20);
        addSecurityRoomButton.setTextColor(Color.WHITE);
        addSecurityRoomButton.setTransformationMethod(null);

        addSecurityRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecurityRoomSelector.this, AddRoom.class);
                startActivity(intent);
            }
        });

        buttonContainer.addView(addSecurityRoomButton);
    }
}
