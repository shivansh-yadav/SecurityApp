package com.example.securityapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.os.Bundle;
import android.content.*;
import android.widget.*;
import  android.view.*;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SMS = 1;

    private static final int REQUEST_LOCATION = 1;

    protected static final int RESULT_SPEECH = 1;

    String name,phone,bname,bmobile;
    public static final String MyPREFERENCES = "SecurityApp" ; //appname

    public static final String Name = "name"; //column name
    public static final String Phone = "phone";// column name
    public static final String BName = "bname"; //column name
    public static final String BPhone = "bphone";// column name

    SharedPreferences sharedpreferences;

    TextView txtText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        final  SharedPreferences.Editor editor = sharedpreferences.edit();

        name = sharedpreferences.getString(Name,"");
        phone = sharedpreferences.getString(Phone,"");
        bname = sharedpreferences.getString(BName,"");
        bmobile = sharedpreferences.getString(BName,"");

        if(name.length()>0)
        {
            setContentView(R.layout.speak);
            Toast.makeText(this,"Welcome"+name,Toast.LENGTH_SHORT).show();
            txtText = (TextView) findViewById(R.id.textView);
        }
        else
        {
            setContentView(R.layout.activity_main);

        }
        ActivityCompat.requestPermissions( this,
                new String[] {Manifest.permission.SEND_SMS}, REQUEST_SMS);
    }

    public void submitclick(View v)
    {
        EditText t1 = (EditText) findViewById(R.id.editTextTextPersonName);
        EditText t2 = (EditText) findViewById(R.id.editTextPhone);
        EditText t3 = (EditText) findViewById(R.id.editTextTextPersonName2);
        EditText t4 = (EditText) findViewById(R.id.editTextPhone2);

        name =t1.getText().toString();
        phone =t2.getText().toString();

        bname =t3.getText().toString();
        bmobile =t4.getText().toString();

        if(name.length()==0 || bname.length()==0 || phone.length()==0 ||bmobile.length()==0)
        {
            Toast.makeText(this,"Value cannot be empty",Toast.LENGTH_SHORT).show();

            return;

        }
        final  SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(Name,name);
        editor.putString(Phone,phone);
        editor.putString(BName,bname);
        editor.putString(BPhone,bmobile);
        editor.commit();


        try {
            String firstmsg = "Hi " + bname
                    + "\n, i have added as a helping person";

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(bmobile,
                    null, firstmsg, null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent!",
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS faild, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        setContentView(R.layout.speak);
        txtText = (TextView) findViewById(R.id.textView);
        Toast.makeText(this,"Welcome"+name,Toast.LENGTH_SHORT).show();


    }


    public void speakclick(View v)
    {
        Intent intent = new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                "en-US");

        try {
            startActivityForResult(intent, RESULT_SPEECH);
            txtText.setText("");
        } catch (ActivityNotFoundException a) {
            Toast t = Toast.makeText(getApplicationContext(),
                    "Ops! Your device doesn't support Speech to Text",
                    Toast.LENGTH_SHORT);
            t.show();
        }

    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    txtText.setText(text.get(0));
                    if (txtText.getText().equals("help")) {
                        helpSMS();
                    }

                }
                break;
            }

        }
    }

    public void helpSMS()
    {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        getCuttentLocation();
    }
    @SuppressLint("MissingPermission")
    public void getCuttentLocation() {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //   if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        //         != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        //                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            double lat = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double lon = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            //showLocation.setText(String.format("Latitude :%s \n Longitude : %s", lat, lon));
//"http://maps.google.com/maps?daddr=22.6912162,75.8882394";

                            String url ="http://maps.google.com/maps?daddr="+lat+","+lon;
                            String firstmsg = "i need help \n"+" and my location is "+url;

                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(bmobile,
                                    null, firstmsg, null, null);
                            Toast.makeText(getApplicationContext(), "SMS Sent!",
                                    Toast.LENGTH_LONG).show();



                        }
                    }
                }, Looper.getMainLooper());
        return;
    }

}