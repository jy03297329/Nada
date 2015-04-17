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

                /*Remember, we defined our User class to have a field backendId.  Therefore, we must move the “id”
property in the object returned to a new property “backendId”.  Some of you might be thinking, “Why not just
change the field of user to “id” rather than backendId”?  The short answer is that this will create some
conflicts in what we will be doing later on, and become more clear in the next lab.*/

                result.addProperty("backendId", result.get("id").toString());
                result.remove("id");

                Log.d(TAG, "Login returned: " + result);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
//                User user = gson.fromJson(result, User.class);
//
                callback.onRequestCompleted(null);
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
