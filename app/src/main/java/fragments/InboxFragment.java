package fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.caleb.sift11.MainActivity;
import com.example.caleb.sift11.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Manifest;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.gmail.GmailScopes;

import com.google.api.services.gmail.model.*;


import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Caleb on 11/20/2016.
 */

public class InboxFragment extends Fragment {

    private ListView inboxListView;
    private View inboxView;

    private ArrayList<Message> inboxList;
    private ArrayAdapter<Message> inboxAdapter;
    private GoogleAccountCredential creds;
    private static final String[] SCOPES = {GmailScopes.GMAIL_LABELS};

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(getActivity());
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        System.out.println("AGPS method");
//        GoogleApiAvailability apiAvailability =
//                GoogleApiAvailability.getInstance();
//        final int connectionStatusCode =
//                apiAvailability.isGooglePlayServicesAvailable(getActivity());
//        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
//            //showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
//            System.out.println("ERROR IN GPLAY");
//        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("Beginning of oncreate method");
        acquireGooglePlayServices();
        inboxView = inflater.inflate(R.layout.inbox_layout, container, false);
        inboxListView = (ListView) inboxView.findViewById(R.id.inboxListView);
        inboxList = new ArrayList<>();
        inboxAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, inboxList);
        inboxListView.setAdapter(inboxAdapter);
        creds = GoogleAccountCredential.usingOAuth2(getActivity(), Arrays.asList(SCOPES)).setBackOff(new ExponentialBackOff());

        String accountName = "";
        if(EasyPermissions.hasPermissions(getActivity(), android.Manifest.permission.GET_ACCOUNTS)){
            System.out.println("PERMISSIONS SUCCESS");
            accountName = "zach.t.copeland@gmail.com";
            creds.setSelectedAccountName(accountName);
            new MakeInboxRequest(creds).execute();
            inboxAdapter.notifyDataSetChanged();
        }

        System.out.println("accountName: " + accountName);
        System.out.println("EXECUTED");
        return inboxView;
    }
    private class MakeInboxRequest extends AsyncTask<Void, Void, List<Message>> {
        private com.google.api.services.gmail.Gmail mService = null;
        private Exception mLastError = null;


        MakeInboxRequest(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Android Client 1")
                    .build();

            System.out.println("REQUEST CREATED");
        }

        /**
         * Background task to call Gmail API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<Message> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }
        private List<Message> getDataFromApi() throws IOException {
            System.out.println("GETTING DATA");

            String userId = "me";

            ListMessagesResponse response;
            List<Message> messages = new ArrayList<>();

            try{
                response = mService.users().messages().list(userId).execute();
                while (response.getMessages() != null) {
                    messages.addAll(response.getMessages());
                    if (response.getNextPageToken() != null) {
                        String pageToken = response.getNextPageToken();
                        response = mService.users().messages().list(userId)
                                .setPageToken(pageToken).execute();
                    } else {
                        break;
                    }
                }

                System.out.println("GETDATAFROMAPI");
                for (Message message : messages) {
                    System.out.println(message.toPrettyString());
                }

            }catch(Exception e){
                System.out.println("FAILED:");
                e.printStackTrace(System.out);
            }
            return messages;
        }

        /**
         * Fetch a list of Gmail labels attached to the specified account.
         * @return List of Strings labels.
         * @throws IOException
         */
        /*
        private List<Message> getDataFromApi() throws IOException {
            // Get the labels in the user's account.
            System.out.println("beginning of getdatafromapi, bitches");
            String user = "me";
            ListMessagesResponse listResponse =
                    mService.users().messages().list(user).execute();
            System.out.println(listResponse.getMessages().get(0));
            for (Message label : listResponse.getMessages()) {
                System.out.println("Hello");
                //inboxList.add(label.());
            }
            return inboxList;
        }
        */


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(List<Message> output) {

        }

        @Override
        protected void onCancelled() {
        }
    }
}








