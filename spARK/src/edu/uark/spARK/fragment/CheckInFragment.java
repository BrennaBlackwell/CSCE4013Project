package edu.uark.spARK.fragment;

import java.io.*;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.*;

import com.google.android.gms.maps.MapFragment;

import edu.uark.spARK.R;
import edu.uark.spARK.R.id;
import edu.uark.spARK.R.layout;
import edu.uark.spARK.location.MyLocation;
import edu.uark.spARK.location.MyLocation.LocationResult;



public class CheckInFragment extends MapViewFragment{

    private String latitude;
    private String longitude;
    private String provider;
    private final String APIKEY = "AIzaSyDkyotX867kletjCsFXqrQvCWwgzuVK2zs";
    private final int radius = 2000;
    private StringBuilder query = new StringBuilder();
    private ArrayList<Place> places = new ArrayList<Place>();
    private ListView listView;
    MyLocation myLocation = new MyLocation();
    MyLocation.LocationResult locationResult;
    ProgressDialog progressDialog = null;
    Context myContext;

    public CheckInFragment() {
        // Empty constructor required for fragment subclasses
    }    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
    	View rootView = inflater.inflate(R.layout.fragment_check_in, container, false);
        getFragmentManager().beginTransaction().add(R.id.checkin_map_frame, new MapViewFragment()).commit();
        myContext = container.getContext();

        LocationManager locationManager = (LocationManager) myContext.getSystemService(
                Context.LOCATION_SERVICE);
        LocationListener myLocationListener = new MyLocationListener();

        locationResult = new MyLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                progressDialog.dismiss();
                new GetCurrentLocation().execute(latitude, longitude);
            }
        };

        MyRunnable myRun = new MyRunnable();
        myRun.run();

        progressDialog = ProgressDialog.show(CheckInFragment.this.getActivity(), "Finding your location",
                "Please wait...", true);


        return rootView;
    }
    
//    final Button loginButton = (Button) getView().findViewById(R.id.scanButton);
    
/*    public void scanCode(View v) {
        
    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

	@Override
    public void setMapPadding(int padding){
		
    }

    public static Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        InputSource is = new InputSource(new StringReader(xml));

        return builder.parse(is);
    }

    private class GetCurrentLocation extends AsyncTask<Object, String, Boolean> {

        @Override
        protected Boolean doInBackground(Object... myLocationObjs) {
            if(null != latitude && null != longitude) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            assert result;
            query.append("https://maps.googleapis.com/maps/api/place/nearbysearch/xml?");
            query.append("location=" +  latitude + "," + longitude + "&");
            query.append("radius=" + radius + "&");
            query.append("sensor=true&"); //Must be true if queried from a device with GPS
            query.append("key=" + APIKEY);
            new QueryGooglePlaces().execute(query.toString());
        }
    }

    /**
     * Based on: http://stackoverflow.com/questions/3505930
     */
    private class QueryGooglePlaces extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(args[0]));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                } else {
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                Log.e("ERROR", e.getMessage());
            } catch (IOException e) {
                Log.e("ERROR", e.getMessage());
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                Document xmlResult = loadXMLFromString(result);
                NodeList nodeList =  xmlResult.getElementsByTagName("result");
                Log.w("Hello", result);
                for(int i = 0, length = nodeList.getLength(); i < length; i++) {
                    Node node = nodeList.item(i);
                    if(node.getNodeType() == Node.ELEMENT_NODE) {
                        Element nodeElement = (Element) node;
                        Place place = new Place();
                        Node name = nodeElement.getElementsByTagName("name").item(0);
                        Node vicinity = nodeElement.getElementsByTagName("vicinity").item(0);
                        Node rating = nodeElement.getElementsByTagName("rating").item(0);
                        Node reference = nodeElement.getElementsByTagName("reference").item(0);
                        Node id = nodeElement.getElementsByTagName("id").item(0);
                        Node geometryElement = nodeElement.getElementsByTagName("geometry").item(0);
                        NodeList locationElement = geometryElement.getChildNodes();
                        Element latLngElem = (Element) locationElement.item(1);
                        Node lat = latLngElem.getElementsByTagName("lat").item(0);
                        Node lng = latLngElem.getElementsByTagName("lng").item(0);
                        float[] geometry =  {Float.valueOf(lat.getTextContent()),
                                Float.valueOf(lng.getTextContent())};
                        int typeCount = nodeElement.getElementsByTagName("type").getLength();
                        String[] types = new String[typeCount];
                        for(int j = 0; j < typeCount; j++) {
                            types[j] = nodeElement.getElementsByTagName("type").item(j).getTextContent();
                        }
                        place.setVicinity(vicinity.getTextContent());
                        place.setId(id.getTextContent());
                        place.setName(name.getTextContent());
                        if(null == rating) {
                            place.setRating(0.0f);
                        } else {
                            place.setRating(Float.valueOf(rating.getTextContent()));
                        }
                        place.setReference(reference.getTextContent());
                        place.setGeometry(geometry);
                        place.setTypes(types);
                        places.add(place);
                    }
                }
                PlaceAdapter placeAdapter = new PlaceAdapter(CheckInFragment.this.getActivity(), R.layout.placerow, places);
                listView = (ListView)getView().findViewById(R.id.lv_places);
                listView.setAdapter(placeAdapter);

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage());
            }
        }
    }

    //http://www.mkyong.com/java/jaxb-hello-world-example/
    /**
     * Represents a single Result of a places API call
     * https://developers.google.com/places/documentation/search
     */
    private class Place {
        public String vicinity;
        public float[] geometry; //array(0 => lat, 1 => lng)
        public String id;
        public String name;
        public float rating;
        public String reference;
        public String[] types;

        public String getVicinity() {
            return vicinity;
        }

        public void setVicinity(String vicinity) {
            this.vicinity = vicinity;
        }

        public float[] getGeometry() {
            return geometry;
        }

        public void setGeometry(float[] geometry) {
            this.geometry = geometry;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public float getRating() {
            return rating;
        }

        public void setRating(float rating) {
            this.rating = rating;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public String[] getTypes() {
            return types;
        }

        public void setTypes(String[] types) {
            this.types = types;
        }
    }

    private class PlaceAdapter extends ArrayAdapter<Place> {
        public Context mContext;
        
        public int layoutResourceId;
        public ArrayList<Place> places;

		private LayoutInflater mInflater;

        public PlaceAdapter(Context context, int layoutResourceId, ArrayList<Place> places) {
            super(context, layoutResourceId, places);
            this.layoutResourceId = layoutResourceId;
    		mContext = context;
    		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.places = places;
        }

        @Override
        public View getView(int rowIndex, View convertView, ViewGroup parent) {
        	//super.getView(rowIndex, convertView, parent);
            if(convertView == null) {
            	convertView = mInflater.inflate(R.layout.placerow, null);
            }
            Place place = places.get(rowIndex);
            if(null != place) {
                TextView name = (TextView) convertView.findViewById(R.id.placerow_name);
                TextView vicinity = (TextView) convertView.findViewById(
                        R.id.placerow_vicinity);
                if(null != name) {
                    name.setText(place.getName());
                }
                if(null != vicinity) {
                    vicinity.setText(place.getVicinity());
                }
            }
            return convertView;
        }
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    }

    public class MyRunnable implements Runnable {
        public MyRunnable() {
        }

        public void run() {
            myLocation.getLocation(getActivity().getApplicationContext(), locationResult);
        }
    }





}

