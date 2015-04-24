package com.example.hjcyz1991.project407.Model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.callumtaylor.asynchttp.AsyncHttpClient;
import net.callumtaylor.asynchttp.response.JsonResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;


/**
 * Created by hjcyz1991 on 4/12/15.
 */
public class Backend {
    private static final String TAG = "ConnectionManager";
    private static final String SERVER_URL = "https://nadadana.herokuapp.com/";

    public interface BackendCallback {
        public void onRequestCompleted(Object result);
        public void onRequestFailed(String message);
    }

    public static void logIn(String email, String password, final BackendCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient(SERVER_URL);
        StringEntity jsonParams = null;

        try {
            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("password", password);
            jsonParams = new StringEntity(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));
        headers.add(new BasicHeader("Content-Type", "application/json"));

        client.post("users/authenticate", jsonParams, headers, new JsonResponseHandler() {
            @Override public void onSuccess() {
                JsonObject result = getContent().getAsJsonObject();


                result.addProperty("backendId", result.get("id").toString());
                result.remove("id");

                //String temp = result.get("created_at").toString();
                //String temp2 = "\""
                //Log.d(null, result.get("created_at").toString());
                //String temp = "\"2013-02-10T13:45:30+0100\"";
                //result.remove("created_at");
                //result.addProperty("created_at", temp);

                Log.d(TAG, "Login returned: " + result);
<<<<<<< HEAD
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
=======
                //Gson gson = new Gson();
                //Date test = gson.fromJson(temp, Date.class);
                //Log.d(null, "date:" + test);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
                //Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
                //Gson gson = new Gson();
>>>>>>> 6e9aa1dcdc579ea9a0d121a3684312e7b3feac97

                User user = gson.fromJson(result, User.class);

                callback.onRequestCompleted(user);
<<<<<<< HEAD

=======
>>>>>>> 6e9aa1dcdc579ea9a0d121a3684312e7b3feac97
            }

            @Override
            public void onFailure() {
                Log.d(null, "failed");
                callback.onRequestFailed(handleFailure(getContent()));
            }
        });
    }

    /* Convenience methods */
    /**
     * Convenience method for parsing server error responses, since most of the handling is similar.
     * @param response the raw response from a server failure.
     * @return a string with an appropriate error message.
     */
    private static String handleFailure(JsonElement response) {
        String errorMessage = "unknown server error";

        if (response == null)
            return errorMessage;

        JsonObject result = response.getAsJsonObject();

        //Server will return all error messages (except in the case of a crash) as a single level JSON
        //with one key called "message". This is a convention for this server.
        try {
            errorMessage = result.get("message").toString();
        }
        catch (Exception e) {
            Log.d(TAG, "Unable to parse server error message");
        }

        return errorMessage;
    }





}
