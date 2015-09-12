package mx.rdy.android.airpp;

import android.os.StrictMode;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seca on 9/4/15.
 */
public class Bathroom implements Serializable{
    private static final String TAG="android.rdy.mx.airpp";

    private int id;
    public int mUserId;
    public String mLocation;
    public  String mDirection;

    private boolean mMix;
    private boolean mHandicap;
    private boolean mBaby;
    private boolean mPaper;
    private boolean mFree;
    private boolean mWater;

    private int mStars;

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public int getStars() {
        return mStars;
    }

    public void setStars(int stars) {
        mStars = stars;
    }

    private double LATITUDE;
    private double LONGITUDE;

    public double getLATITUDE() {
        return LATITUDE;
    }

    public void setLATITUDE(double LATITUDE) {
        this.LATITUDE = LATITUDE;
    }

    public double getLONGITUDE() {
        return LONGITUDE;
    }

    public void setLONGITUDE(double LONGITUDE) {
        this.LONGITUDE = LONGITUDE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getDirection() {
        return mDirection;
    }

    public void setDirection(String direction) {
        mDirection = direction;
    }

    public boolean isMix() {
        return mMix;
    }

    public void setMix(boolean mix) {
        mMix = mix;
    }



    public boolean isHandicap() {
        return mHandicap;
    }

    public void setHandicap(boolean handicap) {
        mHandicap = handicap;
    }

    public boolean isBaby() {
        return mBaby;
    }

    public void setBaby(boolean baby) {
        mBaby = baby;
    }

    public boolean isPaper() {
        return mPaper;
    }

    public void setPaper(boolean paper) {
        mPaper = paper;
    }

    public boolean isFree() {
        return mFree;
    }

    public void setFree(boolean free) {
        mFree = free;
    }

    public boolean isWater() {
        return mWater;
    }

    public void setWater(boolean water) {
        mWater = water;
    }

    public LatLng getLatLon()
    {
        return new LatLng(getLATITUDE(), getLONGITUDE());
    }

    public void save()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://rdy.mx/airpoopee/cms/api/bathroom");
        InputStream inputStream = null;
        String result = null;

        try {
            // Add your data
            String json = "";

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("userId", getUserId());
            jsonObject.accumulate("locationB", getLocation());
            jsonObject.accumulate("addressB", getDirection());
            jsonObject.accumulate("latitudeB", ""+getLATITUDE());
            jsonObject.accumulate("longitudeB", ""+getLONGITUDE());
            jsonObject.accumulate("statusB", "1");

            json = jsonObject.toString();

            StringEntity se = new StringEntity(json);


            // Execute HTTP Post Request
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpclient.execute(httpPost);

            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                Log.i(TAG,inputStream.toString());
            else
                result = "Did not work!";

            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            Log.i(TAG,reader.toString());
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }

            result = sb.toString();

            JSONObject jObject = new JSONObject(result);
            String id = jObject.getString("id");

            setId(Integer.parseInt(id));
            saveDetails();

        } catch (ClientProtocolException e) {
            Log.e(TAG,e.toString(),e);
        } catch (IOException e) {
            Log.e(TAG,e.toString(),e);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void saveDetails()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://rdy.mx/airpoopee/cms/api/review");
        InputStream inputStream = null;
        String result = null;

        try {
            // Add your data
            String json = "";


            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("userId", getUserId());
            jsonObject.accumulate("bathroomId", getId());

            jsonObject.accumulate("mixB", boolToString(isMix()));
            jsonObject.accumulate("handicapB", boolToString(isHandicap()));
            jsonObject.accumulate("babyB", boolToString(isBaby()));
            jsonObject.accumulate("paperB", boolToString(isPaper()));
            jsonObject.accumulate("freeB", boolToString(isFree()));
            jsonObject.accumulate("waterB", boolToString(isWater()));
            jsonObject.accumulate("starsB", getStars());
            jsonObject.accumulate("commentB", "test");

            json = jsonObject.toString();

            StringEntity se = new StringEntity(json);

            Log.i(TAG,"el json es ------------------");
            Log.i(TAG,json);
            // Execute HTTP Post Request
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpclient.execute(httpPost);

            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                Log.i(TAG,inputStream.toString());
            else
                result = "Did not work!";

            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
        } catch (ClientProtocolException e) {
            Log.e(TAG,e.toString(),e);
        } catch (IOException e) {
            Log.e(TAG,e.toString(),e);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //todo HttpClient is now deprecated, use HttpURLConnection
    public String boolToString(boolean val)
    {
        return val == true ? "1" : "0";
    }


}
