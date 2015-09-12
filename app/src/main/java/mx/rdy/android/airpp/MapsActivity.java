package mx.rdy.android.airpp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.widget.Button;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class MapsActivity extends FragmentActivity {
    private static final String TAG="android.rdy.mx.airpp";
    static final int SAVE_BATHROOM_CODE = 1;
    static final int DETAIL_BATHROOM_CODE = 2;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public Button mCreate;
    public Button mDragOk;
    public Button mDragCancel;
    public LinearLayout mDragContainer;
    public TextView mDragInstructions;

    private LocationManager mLocationManager;
    static final int LOCATION_REFRESH_TIME = 100;
    static final int LOCATION_REFRESH_DISTANCE = 5000;
    public double LATITUDE;
    public double LONGITUDE;
    Location location;
    public Marker mCurrentMark;
    public Marker mNewMark;
    private BitmapDescriptor iconCurrent;
    private BitmapDescriptor iconBad;
    private BitmapDescriptor iconGood;
    private BitmapDescriptor iconPerfect;
    private User mLocalUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        // todo anonymous and login for users
        mLocalUser = new User();
        mLocalUser.setId(1);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);
        iconBad = BitmapDescriptorFactory.fromResource(R.drawable.img_pin_1);
        iconGood = BitmapDescriptorFactory.fromResource(R.drawable.img_pin_2);
        iconPerfect = BitmapDescriptorFactory.fromResource(R.drawable.img_pin_3);

        setUpMapIfNeeded();
        cacheElements();
        getMarks gm = new getMarks();
        gm.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        Criteria criteria = new Criteria();

        //location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Location location = mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(criteria, false));
        if (location != null) {
            LATITUDE = location.getLatitude();
            LONGITUDE = location.getLongitude();
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(LATITUDE, LONGITUDE), 13));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(LATITUDE, LONGITUDE))      // Sets the center of the map to location user
                .zoom(15)                   // Sets the zoom
                        //.bearing(90)                // Sets the orientation of the camera to east
                        //.tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        createCurrentMark();

    }

    private void cacheElements()
    {
        mCreate = (Button) findViewById(R.id.addButton);
        mDragOk = (Button) findViewById(R.id.dragOK);
        mDragCancel = (Button) findViewById(R.id.dragCancel);
        mDragContainer = (LinearLayout)findViewById(R.id.drag_container);
        mDragInstructions= (TextView)findViewById(R.id.drag_instructions);
        binding();

    }
    private void binding()
    {
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddBathroom();
            }
        });

        mDragOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_new();
            }
        });

        mDragCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close_new();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {

            @Override
            public boolean onMarkerClick(Marker mark) {

                Log.i(TAG,"snipet->"+mark.getSnippet());
                if(mark.getSnippet().substring(0,7).equals("fromurl"))
                {
                    StringTokenizer parts=new StringTokenizer(mark.getSnippet().toString(),"|");

                    String first = parts.nextToken();// this will contain "Fruit"
                    Integer id = Integer.parseInt(parts.nextToken());// this will contain " they taste good"
                    Log.i(TAG,"first"+first);
                    Log.i(TAG,"el id"+id);
                    getBathroomDetail(id);
                }

                //if(arg0.getTitle().equals("MyHome")) // if marker source is clicked

                return true;
            }

        });
    }

    private void openAddBathroom()
    {

        mCurrentMark.setVisible(false);
        if(mNewMark==null)
        {
            mNewMark= mMap.addMarker(new MarkerOptions().position(mMap.getCameraPosition().target)
                            .title("Tap and drag me then click on 'OK' button")
                            .draggable(true)
                            .snippet("new")
            );

        }
        else
        {
            mNewMark.setPosition(new  LatLng(LATITUDE,LONGITUDE));
            mNewMark.setVisible(true);
        }


        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                if(marker.equals(mNewMark))
                {

                    LATITUDE= marker.getPosition().latitude;
                    LONGITUDE= marker.getPosition().longitude;

                }
            }
        });

        mNewMark.showInfoWindow();

        mDragContainer.setVisibility(View.VISIBLE);
        mDragInstructions.setVisibility(View.VISIBLE);
        mCreate.setVisibility(View.INVISIBLE);

    }

    private void open_new()
    {

        Intent i = new Intent(MapsActivity.this,SaveActivity.class);


        Bathroom b = new Bathroom();
        b.setLATITUDE(LATITUDE);
        b.setLONGITUDE(LONGITUDE);
        b.setUserId(1);
        i.putExtra("bathroom",(Bathroom)b);
        startActivityForResult(i,SAVE_BATHROOM_CODE);


    }

    private void close_new()
    {
        mDragContainer.setVisibility(View.INVISIBLE);
        mCreate.setVisibility(View.VISIBLE);
        mDragInstructions.setVisibility(View.INVISIBLE);
        mNewMark.setVisible(false);
        mCurrentMark.setVisible(true);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if(requestCode == SAVE_BATHROOM_CODE)
        {
            Log.i(TAG,"--------------------------------");
            mNewMark.setVisible(false);

            Bathroom b = (Bathroom)data.getSerializableExtra("bathroom");

            Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(b.getLATITUDE(), b.getLONGITUDE())).title(b.getLocation()));
            setMarkerIcon(m,b.getStars());
            m.setSnippet("fromurl|"+b.getId());
            Log.i(TAG,"snippet->"+m.getSnippet());
            m.showInfoWindow();


            close_new();
        }
        //super.onActivityResult(requestCode, resultCode, data);
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            LATITUDE= location.getLatitude();
            LONGITUDE = location.getLongitude();
            updateMarkerLocation();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        public void updateMarkerLocation()
        {
            LatLng mLocation= new  LatLng(LATITUDE,LONGITUDE);
            mCurrentMark.setPosition(mLocation);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(LATITUDE, LONGITUDE), 15));


        }
    };

    public void createCurrentMark()
    {
        iconCurrent = BitmapDescriptorFactory.fromResource(R.drawable.img_pin_ubica);
        mCurrentMark= mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(LATITUDE, LONGITUDE))
                        .title("You are here")
                        .icon(iconCurrent)
                        .snippet("current")
        );

        mCurrentMark.showInfoWindow();
    }


    class getMarks extends AsyncTask<String, String, Void> {

        private ProgressDialog progressDialog = new ProgressDialog(MapsActivity.this);
        InputStream inputStream = null;
        String result = "";

        protected void onPreExecute() {
            progressDialog.setMessage("Buscando Baños cercanos...");
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface arg0) {
                    getMarks.this.cancel(true);
                }
            });
        }

        @Override
        protected Void doInBackground(String... params) {

            String url_select = "http://rdy.mx/airpoopee/cms/api/bathroomgeo?latitude="+LATITUDE+"&longitude="+LONGITUDE+"&distance=111";

            try
            {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url_select);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();

                inputStream = httpEntity.getContent();

            } catch (ClientProtocolException e2) {
                Log.e("ClientProtocolException", e2.toString());
                e2.printStackTrace();
                e2.printStackTrace();
            } catch (IllegalStateException e3) {
                Log.e("IllegalStateException", e3.toString());
                e3.printStackTrace();
            } catch (IOException e4) {
                Log.e("IOException", e4.toString());
                e4.printStackTrace();
            }

            try {
                BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                StringBuilder sBuilder = new StringBuilder();

                String line = null;
                while ((line = bReader.readLine()) != null) {
                    sBuilder.append(line + "\n");
                }

                inputStream.close();
                result = sBuilder.toString();


            } catch (Exception e) {
                Log.e("StringBuilding & BufferedReader", "Error converting result " + e.toString());
            }

            Log.i(TAG,result.toString());
            return null;
        }



        protected void onPostExecute(Void v) {
            //parse JSON data



            try {
                JSONObject jResult = new JSONObject(result);

                JSONArray rows = jResult.getJSONArray("rows");

                for(int i=0; i<rows.length();i++)
                {
                    JSONObject row= rows.getJSONObject(i);
                    createMark(new LatLng(row.getDouble("latitudeB"), row.getDouble("longitudeB")),row.getString("locationB"),row.getInt("id"),row.getDouble("totalStars"));

                }


            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            } // catch (JSONException e)
            this.progressDialog.dismiss();
        } // protected void onPostExecute(Void v)

        public void createMark(LatLng latlong,String location,int id,double stars)
        {
            Log.i(TAG,"stars="+stars);
            Marker m =mMap.addMarker(new MarkerOptions()
                    .position(latlong)
                    .title(location)
                    .snippet("fromurl|"+id));
            setMarkerIcon(m,(int)Math.round(stars));

        }
    }

    public void setMarkerIcon(Marker m,int stars)
    {
        switch(stars)
        {
            case 1:
            case 2: m.setIcon(iconBad);
                break;
            case 3:
            case 4:
                m.setIcon(iconGood);
                break;
            case 5:
                m.setIcon(iconPerfect);
                break;

        }
    }
    public void openBathroomDetail(Bathroom b)
    {
        Intent i = new Intent(MapsActivity.this,DetailActivity.class);

        i.putExtra("bathroom",(Bathroom)b);

        startActivityForResult(i,DETAIL_BATHROOM_CODE);
    }
    public void getBathroomDetail(int id)
    {

        getDetail detail_info = new getDetail(id);
        detail_info.execute();

    }
    //http://stackoverflow.com/questions/13196234/simple-parse-json-from-url-on-android

    class getDetail extends AsyncTask<String, String, Void> {

        private ProgressDialog progressDialog = new ProgressDialog(MapsActivity.this);
        InputStream inputStream = null;
        String result = "";
        Bathroom b = new Bathroom();


        public getDetail(int id)
        {
            super();
            b.setId(id);
        }
        protected void onPreExecute() {
            progressDialog.setMessage("Cargando ...");
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface arg0) {
                    getDetail.this.cancel(true);
                }
            });
        }

        @Override
        protected Void doInBackground(String... params) {

            String url_select = "http://rdy.mx/airpoopee/cms/api/bathroomdetail?id="+b.getId();

            try
            {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url_select);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();

                inputStream = httpEntity.getContent();

            } catch (ClientProtocolException e2) {
                Log.e("ClientProtocolException", e2.toString());
                e2.printStackTrace();
                e2.printStackTrace();
            } catch (IllegalStateException e3) {
                Log.e("IllegalStateException", e3.toString());
                e3.printStackTrace();
            } catch (IOException e4) {
                Log.e("IOException", e4.toString());
                e4.printStackTrace();
            }

            try {
                BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                StringBuilder sBuilder = new StringBuilder();

                String line = null;
                while ((line = bReader.readLine()) != null) {
                    sBuilder.append(line + "\n");
                }

                inputStream.close();
                result = sBuilder.toString();


            } catch (Exception e) {
                Log.e("StringBuilding & BufferedReader", "Error converting result " + e.toString());
            }

            Log.i(TAG,result.toString());
            return null;
        }



        protected void onPostExecute(Void v) {
            //parse JSON data



            try {
                JSONObject jResult = new JSONObject(result);
                JSONArray rows = jResult.getJSONArray("rows");
                JSONObject data= rows.getJSONObject(0);

                b.setUserId(mLocalUser.getId());
                b.setLocation(data.getString("locationB"));
                b.setDirection(data.getString("addressB"));

                b.setMix(isMin(data.getDouble("totalMix")));
                b.setHandicap(isMin(data.getDouble("totalHandicap")));
                b.setBaby(isMin(data.getDouble("totalBaby")));
                b.setPaper(isMin(data.getDouble("totalPaper")));
                b.setFree(isMin(data.getDouble("totalFree")));
                b.setWater(isMin(data.getDouble("totalWater")));

                b.setStars((int)Math.round(data.getDouble("totalStars")));

                openBathroomDetail(b);



            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            } // catch (JSONException e)
            this.progressDialog.dismiss();
        } // protected void onPostExecute(Void v)

        public Boolean isMin(Double val)
        {
            return val > 0.5;
        }


    }
}
