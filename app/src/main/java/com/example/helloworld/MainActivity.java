package com.example.helloworld;

import static android.Manifest.permission.*;
import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity<FusedLocationProviderClient> extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback
{


    
boolean startIsPressed;
boolean smsSent;
    String destinationText;
    String starting;
    String startingLongtitude = null;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    LocationManager lm;
    String startingLatitude = null;
    AsyncHTTP async;
    double minutesToText;
    EditText phoneNumber;
    public void setLocation(Location location) {
        this.location = location;
    }

    Location location;
    double longitude;
    double latitude;

    private static final long LOCATION_REFRESH_TIME = 10;
    private static final float LOCATION_REFRESH_DISTANCE = 1;
    private com.google.android.gms.location.FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        Button start = findViewById(R.id.button);
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        EditText destination = findViewById(R.id.destination);

         phoneNumber = findViewById(R.id.phonenumberTextBox);
        EditText distanceFromLocationToSendText = findViewById(R.id.distance);

        //Starting location rounadbout
              boolean fine=       ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION);


       boolean coarse= ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_COARSE_LOCATION);
System.out.println(fine + ""+ coarse);

        // first ask the system as stated ok...
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

         System.out.println("Checking permission");

         ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION},PackageManager.PERMISSION_GRANTED);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, locationListener);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, SEND_SMS) != PackageManager.PERMISSION_GRANTED ) {

            System.out.println("Checking permission for SMS");

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS},123);



        }
        else {
            System.out.println("Failed SMS permission");
            Log.d("OnCreate", "No Permission FOR SMS");
        }

        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);





        start.setOnClickListener(view -> {
                    System.out.println("Made it");
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
//fix this, i changeed the if varibabler was starringloc now its startinglongtitude



                    //   String locationFromButtonPress = loc.toString();

                    // System.out.println(locationFromButtonPress);
                    System.out.println("LocationButton");
                    System.out.println(latitude);
                    System.out.println(longitude);

           String longit =   Double.toString(longitude);
                    String lat =   Double.toString(latitude);
                     starting = lat+","+longit;
                    destinationText = destination.getText().toString();
                    String mins = distanceFromLocationToSendText.getText().toString();

               minutesToText  =   Double.parseDouble(mins);
                       async = new AsyncHTTP();
//String timeNeeded ="1 day 12 hours";
                    String timeNeeded = async.jsonParse(async.doInBackground(destinationText, minutesToText,starting));
                    if (timeNeeded != null) {
                        async.timeConverter(timeNeeded);
                        if(async.getMinutes()<=minutesToText) {
                            sendMessage();
                        }
                    }
                    startIsPressed =true;
                    System.out.println(startIsPressed);
                }

        );





    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                System.out.println("On requestPermissionsResult");
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, locationListener);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }
            case 123: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PLAYGROUND", "Permission has been granted");


                } else {
                    Log.d("PLAYGROUND", "Permission has been denied or request cancelled");


                }


            }


        }

    }

    private final LocationListener locationListener = new LocationListener() {

        public void onLocationChanged(Location location) {

            setLocation(location);
            System.out.println("Location change");
           if (startIsPressed) {
               longitude = location.getLongitude();
               latitude = location.getLatitude();
               String longit =   Double.toString(longitude);
               String lat =   Double.toString(latitude);
               starting = lat+","+longit;

               String timeNeeded = async.jsonParse(async.doInBackground(destinationText, minutesToText,starting));
               if (timeNeeded != null) {
                   async.timeConverter(timeNeeded);
               }
               System.out.println(async.getMinutes()+ "I AM CHECKING ASYNC");
               if ( async.getMinutes()<=minutesToText) {

                   System.out.println("getMIntues Checkl");
                   if (ActivityCompat.checkSelfPermission(MainActivity.this, SEND_SMS) == PackageManager.PERMISSION_GRANTED ) {




                       System.out.println("Sent message?");
                       sendMessage();
                       startIsPressed=false;
                   }
                   else {
                       System.out.println("Else statement");
                       ActivityCompat.requestPermissions(MainActivity.this, new String[]{SEND_SMS},1);
                   }

               }


           }
           // each time klocation is changed we need to calculate the minutes again
            // an
        }
    };

    private void sendMessage() {


String message = "I am " + minutesToText + " minutes away.";
try {

    if (!smsSent) {
        SmsManager smsManager = SmsManager.getDefault();

        smsManager.sendTextMessage(phoneNumber.getText().toString().trim(), null, message, null, null);
        Toast.makeText(this, "SMS SENT", Toast.LENGTH_LONG).show();
        smsSent = true;
    }
}
catch(Exception e) {

    System.out.println("Message not sent. Error occurred");
    Toast.makeText(this, "Message not sent. Error occurred", Toast.LENGTH_LONG).show();
}
    }


}
