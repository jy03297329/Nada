package com.example.hjcyz1991.project407.Model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.callumtaylor.asynchttp.AsyncHttpClient;
import net.callumtaylor.asynchttp.response.JsonResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.SerializableEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Date;


/**
 * Created by hjcyz1991 on 4/12/15.
 */
public class Backend {
    private static final String TAG = "ConnectionManager";
    static final String SERVER_URL = "https://nadadana.herokuapp.com/";
    // Google Project Number
    static final String GOOGLE_PROJ_ID = "16617277799";
    // Message Key for GCM
    static final String MSG_KEY = "Message";

    public interface BackendCallback {
        public void onRequestCompleted(Object result);

        public void onRequestFailed(String message);
    }

    public static void logIn(String email, final String password, final BackendCallback callback) {
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

        //final User curUser = new User();
        Log.d(null, "backend LOGIN called");
        client.post("users/authenticate", jsonParams, headers, new JsonResponseHandler() {
            @Override
            public void onSuccess() {
                JsonObject result = getContent().getAsJsonObject();
                result.addProperty("backendId", result.get("id").toString());
                result.remove("id");
                result.addProperty("authToken", password);
                result.addProperty("authTokenConfirm", password);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
                User user = gson.fromJson(result, User.class);
                //Log.d(null, "---------" + user.toString());
                //curUser.copy(user);
                callback.onRequestCompleted(user);
            }

            @Override
            public void onFailure() {
                Log.d(null, "failed");
                callback.onRequestFailed(handleFailure(getContent()));
            }
        });
    }

    public static void loadUserFriends(final User user, final BackendCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient(SERVER_URL);
        StringEntity jsonParams = null;
        //Log.d(null, "*******" + user.toString());
        try {
            JSONObject json = new JSONObject();
            //Log.d(null, "User backendId: " + user.backendId);
            json.put("cur_user_id", user.backendId);
            json.put("password", user.authToken);
            jsonParams = new StringEntity(json.toString());
            //Log.d(null, "checking friend posts");
            //Log.d(null, json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));
        headers.add(new BasicHeader("Content-Type", "application/json"));

        client.post("friendships/friend_list", jsonParams, headers,
                new JsonResponseHandler() {
                    @Override
                    public void onSuccess() {
                        //JsonElement listWrapper = getContent().getAsJsonObject();
                        JsonArray friendList = (JsonArray) getContent().getAsJsonObject().get("friends");
                        List<User> newFriends = new ArrayList<User>();
                        for (JsonElement element : friendList) {
                            JsonObject friend = element.getAsJsonObject();
                            friend.addProperty("backendId", friend.get("id").toString());
                            friend.remove("id");
                            //friend.addProperty("");
                            Gson gson = new Gson();
                            newFriends.add(gson.fromJson(friend, User.class));
                            //user.friends.add(newFriend);
                        }
                        Log.d(TAG, "backend got newfriends: " + newFriends.toString());
                        callback.onRequestCompleted(newFriends);
                    }

                    @Override
                    public void onFailure() {
                        Log.d(null, "trying to get friend list");
                        Log.d(null, "failed");
                        callback.onRequestFailed(handleFailure(getContent()));
                    }
                });
    }

    public static void loadUserBill(final User user, final BackendCallback callback) {
        //final int userId = user.backendId;
        AsyncHttpClient client = new AsyncHttpClient(SERVER_URL);
        StringEntity jsonParams = null;
        try {
            JSONObject json = new JSONObject();
            json.put("cur_user_id", user.backendId);
            json.put("password", user.authToken);
            jsonParams = new StringEntity(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));
        headers.add(new BasicHeader("Content-Type", "application/json"));
        //headers.add(new BasicHeader("X-USER-ID", Integer.toString(user.backendId)));
        //headers.add(new BasicHeader("X-AUTHENTICATION-TOKEN", user.authToken));

        client.post("bills/unpaid_bills", jsonParams, headers, new JsonResponseHandler() {
            @Override
            public void onSuccess() {
                //JsonObject result = getContent().getAsJsonObject();
                JsonArray billList = (JsonArray) getContent().getAsJsonObject().get("unpaid_bills");
                List<Bill> newBills = new ArrayList<Bill>();
                for (JsonElement element : billList) {
                    JsonObject bill = element.getAsJsonObject();
                    bill.addProperty("backendId", bill.get("id").toString());
                    bill.remove("id");
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
                    newBills.add(gson.fromJson(bill, Bill.class));

                    //Log.d(null, newBill.toString());
                }
                callback.onRequestCompleted(newBills);
            }

            @Override
            public void onFailure() {
                Log.d(null, "trying to get bill list");
                Log.d(null, "failed");
                callback.onRequestFailed(handleFailure(getContent()));
            }
        });

    }

    public static void register(String name, String email, String password, String conPassword,
                                final BackendCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient(SERVER_URL);
        StringEntity jsonParams = null;

        try {
            JSONObject userJson = new JSONObject();
            JSONObject userData = new JSONObject();
            userData.put("name", name);
            userData.put("email", email);
            userData.put("password", password);
            userData.put("password_confirmation", conPassword);
            userJson.accumulate("user", userData);

            //Log.d(null, "----------" + userJson.toString());
            jsonParams = new StringEntity(userJson.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));
        headers.add(new BasicHeader("Content-Type", "application/json"));

        //Log.d(null, "----------" + jsonParams.toString());
        client.post("users/", jsonParams, headers, new JsonResponseHandler() {

            @Override
            public void onSuccess() {
                JsonObject result = getContent().getAsJsonObject();
                result.addProperty("backendId", result.get("id").toString());
                result.remove("id");
                Log.d(TAG, "Register returned: " + result);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
                User user = gson.fromJson(result, User.class);
                callback.onRequestCompleted(user);

            }

            @Override
            public void onFailure() {
                Log.d(null, "trying to post to users");
                Log.d(null, "failed");
                JsonArray errMsg = (JsonArray) getContent().getAsJsonObject().get("email");
                Gson gson = new Gson();
                String err = gson.fromJson(errMsg.get(0), String.class);

                callback.onRequestFailed(err);
            }
        });

    }

    public static void searchUser(String email,
                                    final BackendCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient(SERVER_URL);
        StringEntity jsonParams = null;
        try {
            JSONObject json = new JSONObject();
            json.put("email", email);
            jsonParams = new StringEntity(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));
        headers.add(new BasicHeader("Content-Type", "application/json"));
        client.post("users/searchuser", jsonParams, headers, new JsonResponseHandler() {

            @Override
            public void onSuccess() {
                JsonObject result = getContent().getAsJsonObject();
                result.addProperty("backendId", result.get("id").toString());
                result.remove("id");
                Log.d(null, "Search user returned: " + result);
                Gson gson = new Gson();
                User user = gson.fromJson(result, User.class);
                callback.onRequestCompleted(user);

            }

            @Override
            public void onFailure() {
                Log.d(null, "trying to search users");
                Log.d(null, "failed");
                callback.onRequestFailed(handleFailure(getContent()));
            }
        });

    }

    public static void addFriend(int user_id, String password,
                                 String friendEmail, final BackendCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient(SERVER_URL);
        StringEntity jsonParams = null;
        try {
            JSONObject json = new JSONObject();
            json.put("cur_user_id", user_id);
            json.put("password", password);
            json.put("friend_email", friendEmail);
            Log.d("ADD_FRIEND", json.toString());
            jsonParams = new StringEntity(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));
        headers.add(new BasicHeader("Content-Type", "application/json"));
        client.post("friendships", jsonParams, headers, new JsonResponseHandler() {
            @Override
            public void onSuccess() {
                JsonArray friendship = (JsonArray) getContent().
                        getAsJsonObject().get("friendships");
                //List<Friendship> resultFriendship = new ArrayList<Friendship>();
                if (friendship.size() != 2)
                    Log.d("ERROR: ", "***friendshipList from backend have "
                            + friendship.size() + " friendships!");
                callback.onRequestCompleted(null);
            }

            @Override
            public void onFailure() {
                Log.d(null, "trying to add friend list");
                Log.d(null, "failed");
                callback.onRequestFailed(handleFailure(getContent()));
            }
        });
    }

    //actually add event
    public static void addBillEvent(User user, int totalAmt, String billName, String note,
                               int creditor_id, List<Bill> bills, final BackendCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient(SERVER_URL);
        StringEntity jsonParams = null;
        try {
            JSONObject json = new JSONObject();
            json.put("cur_user_id", user.backendId);
            json.put("password", user.authToken);
            json.put("name", billName);
            json.put("total", totalAmt);
            json.put("note", note);
            json.put("creditor_id", creditor_id);
            for (Bill i : bills) {
                JSONObject billData = new JSONObject();
                billData.put("debtor_id", i.debtor_id);
                billData.put("amount", i.amount);
                json.accumulate("bills", billData);
            }

            jsonParams = new StringEntity(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));
        headers.add(new BasicHeader("Content-Type", "application/json"));
        client.post("events", jsonParams, headers, new JsonResponseHandler() {
            @Override
            public void onSuccess() {
                callback.onRequestCompleted(getContent().getAsJsonObject());
            }
            @Override
            public void onFailure() {
                Log.d(null, "trying to add bill event");
                Log.d(null, "failed");
                callback.onRequestFailed(handleFailure(getContent()));
            }
        });
    }

    public static void changePassword(String backendId, String name, String oldAuth, String newAuth,
                                      final BackendCallback callback){
        AsyncHttpClient client = new AsyncHttpClient(SERVER_URL);
        StringEntity jsonParams = null;
        try {
            JSONObject json = new JSONObject();
            json.put("name", name);
            json.put("old_password", oldAuth);
            json.put("password", newAuth);
            jsonParams = new StringEntity(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));
        headers.add(new BasicHeader("Content-Type", "application/json"));
        String url = "users/" + backendId;
        Log.d("BEND_CHANGE_PASSWORD", url);
        client.put(url, jsonParams, headers, new JsonResponseHandler() {
            @Override
            public void onSuccess() {
                JsonObject updatedUser = (JsonObject) getContent().
                        getAsJsonObject().get("user");
                callback.onRequestCompleted(updatedUser);

            }

            @Override
            public void onFailure() {
                Log.d(null, "trying to change password");
                Log.d(null, "failed");
                callback.onRequestFailed(handleFailure(getContent()));
            }
        });

    }

    /* Convenience methods */

    /**
     * Convenience method for parsing server error responses, since most of the handling is similar.
     *
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
        } catch (Exception e) {
            Log.d(TAG, "Unable to parse server error message");
        }

        return errorMessage;
    }


}
