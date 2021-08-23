package com.example.helloworld;

import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class AsyncHTTP  extends AsyncTask {
// google distance matrix
private static final String yourAPIkey = "APIkey";
    public double getMinutes() {
        return minutes;
    }

    private  double minutes = 0;




    @Override
    protected Object doInBackground(Object[] o) {


        return null;
    }
    protected Object doInBackground(String endDestination, double minutesToSendText, String startingLatLong) {
        StringBuilder response = new StringBuilder();


        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins="+startingLatLong.replaceAll(" ", "")+"&destinations="+endDestination+"&key="+yourAPIkey);

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            connection.setRequestMethod("GET");


            String line;

            InputStreamReader is = new InputStreamReader(connection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(is);



            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
                //  response.append('\r');
            }
            bufferedReader.close();
            System.out.println("Response: "  + response.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {


        }


        return response;


    }




    public String jsonParse(Object o) {
        String timeNeeded = null;
        JSONObject json = null;

        JSONObject elements = null;

        try {
            json = new JSONObject(o.toString());
                    JSONArray data = json.getJSONArray("rows");
int length = data.length();
System.out.println(length);

            for (int i = 0; i < length; i++) {

                 elements = data.getJSONObject(i);
                System.out.println(data.getJSONObject(i));


            }



JSONObject holder = null;
            JSONArray timee = elements.getJSONArray("elements");
            length = elements.length();
            for (int i = 0; i < length; ++i) {

                holder = timee.getJSONObject(i);
                System.out.println(timee.getJSONObject(i));


            }

            System.out.println(holder.getString("duration").indexOf("text"));
        int index=   holder.getString("duration").indexOf("text");
           JSONObject ok= holder.getJSONObject("duration");
            System.out.println(    ok.toString());
 timeNeeded =     ok.getString("text");
return timeNeeded;


        } catch (JSONException e) {
            e.printStackTrace();
        }


return timeNeeded;




    }


    public void timeConverter(String time) {
        boolean notLastPosition = false;
        boolean goBack = false;
       time = time.replaceAll(" ","");
        System.out.println("Time converter");
    System.out.println(time);
int index  = 0;
         minutes =0;
String timeConvertedToMinutes="";
String typeOfTime="";
    while(index<time.length()&& Character.isDigit(time.charAt(index))  ) {
        goBack = false;
System.out.println("oops");

        timeConvertedToMinutes+=time.charAt(index);
        System.out.println(timeConvertedToMinutes);
        index++;
        if (index<time.length() &&!Character.isDigit(time.charAt(index)) ) {

            while(index<time.length()&& !Character.isDigit(time.charAt(index))){
                    typeOfTime += time.charAt(index);
                    System.out.println(typeOfTime);
                    index++;
                    System.out.println(index);
                                     if(index==time.length()-1)
                                        notLastPosition = true;
                    if( (index<time.length() ||index==time.length()-1) &&(Character.isDigit(time.charAt(index))||notLastPosition)) {
                               System.out.println("After index vcheck");
                        switch(typeOfTime) {

                            case "days":
                            case"day":    minutes +=24*60 * Double.parseDouble(timeConvertedToMinutes);
                            break;
                            case "hours":
                            case"hour":
                              minutes +=60 * Double.parseDouble(timeConvertedToMinutes);
                              break;
                            case "mi":
                            case "min":
                            case "mins":
                            case "minutes":
                            case"minute":    minutes +=Double.parseDouble(timeConvertedToMinutes);
                                break;
                            default: System.out.println("not days hours or minutes. Maybe seconds?");
                            break;
                        }
                        System.out.println("Got to bottom" + index);
                        typeOfTime="";
                        timeConvertedToMinutes="";
                        goBack=true;
                }
                    if(goBack){

                        break;}
            }
        }
    }

    System.out.println(minutes);

    }
}
