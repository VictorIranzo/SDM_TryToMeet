package com.sdm.trytomeet.services;

import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sdm.trytomeet.POJO.Site;
import com.sdm.trytomeet.fragments.Sites.FindPlaceFragment;
import com.sdm.trytomeet.services.serializers.NearbySearchResult;
import com.sdm.trytomeet.services.serializers.NearbySearchResults;
import com.sdm.trytomeet.R;

public class FilterPlacesService {
        public FindPlaceFragment context;

        // TODO: Move to proper location.
        private String GOOGLE_BROWSER_API_KEY = "AIzaSyCbYRpQk8PQUwx4bgCdCEXmvKsfhN-9O-U";


        public FilterPlacesService(FindPlaceFragment context) {
            this.context = context;
        }

        public void executeService(double latitude, double longitude, double radius, String type){
            StringBuilder builder =
                    new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            builder.append("location=").append(latitude).append(",").append(longitude);
            builder.append("&radius=").append(radius);
            builder.append("&types=").append(type);
            builder.append("&sensor=true");
            builder.append("&key=" + GOOGLE_BROWSER_API_KEY);

            String url = builder.toString();

            RequestQueue queue = Volley.newRequestQueue(context.getActivity());

            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Gson gson = new Gson();
                    NearbySearchResults results = gson.fromJson(response, NearbySearchResults.class);
                    for (NearbySearchResult place: results.getResults()) {
                        Site s = new Site(place.getName(),place.getVicinity(),
                                place.getGeometry().getLocation().getLatitude(),place.getGeometry().getLocation().getLongitude());
                        context.addMarkerPlace(s);
                    }

                    // The space that uses the progress bar is removed.
                    //((ProgressBar)context.findViewById(R.id.progressBar)).setVisibility(View.GONE);
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    String message = context.getString(R.string.error_occured);
                    if (volleyError instanceof NetworkError) {
                        message = context.getString(R.string.error_network);
                    } else if (volleyError instanceof ServerError) {
                        message = context.getString(R.string.error_server);
                    } else if (volleyError instanceof AuthFailureError) {
                        message = context.getString(R.string.error_authorization);
                    } else if (volleyError instanceof ParseError) {
                        message = context.getString(R.string.error_parsing);
                    } else if (volleyError instanceof NoConnectionError) {
                        message = context.getString(R.string.error_noconnection);
                    } else if (volleyError instanceof TimeoutError) {
                        message = context.getString(R.string.error_timeot);
                    }

                    Toast.makeText(context.getActivity(), message, Toast.LENGTH_SHORT).show();
                }
            };

            StringRequest postRequest = new StringRequest(Request.Method.GET,url,responseListener, errorListener);

            queue.add(postRequest);
        }
}
