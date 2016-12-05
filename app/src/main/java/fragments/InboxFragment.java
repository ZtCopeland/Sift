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


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.jar.Manifest;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.gmail.GmailScopes;


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

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Caleb on 11/20/2016.
 */

public class InboxFragment extends Fragment {

    private ListView inboxListView;
    private View inboxView;

    private ArrayList<String> inboxList;
    private ArrayAdapter<String> inboxAdapter;
    private GoogleAccountCredential creds;
    private static final String[] SCOPES = {GmailScopes.GMAIL_LABELS};
    private String host = "pop.gmail.com";// change accordingly
    private String mailStoreType = "pop3";
    private String username = "zach.t.copeland@gmail.com";// change accordingly
    private String password = "blackbareantiks12345";// change accordingly
    public class fetcher extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                fetch(host, mailStoreType, username, password);
            } catch (Exception e) {
                e.printStackTrace(System.out);


            }
            return null;
        }

        public void fetch(String pop3Host, String storeType, String user,
                          String password) {
            try {
                // create properties field
                Properties properties = new Properties();
                properties.put("mail.store.protocol", "pop3");
                properties.put("mail.pop3.host", pop3Host);
                properties.put("mail.pop3.port", "995");
                properties.put("mail.pop3.starttls.enable", "true");
                Session emailSession = Session.getDefaultInstance(properties);
                // emailSession.setDebug(true);

                // create the POP3 store object and connect with the pop server
                Store store = emailSession.getStore("pop3s");

                store.connect(pop3Host, user, password);

                // create the folder object and open it
                Folder emailFolder = store.getFolder("INBOX");
                emailFolder.open(Folder.READ_ONLY);

                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

                // retrieve the messages from the folder in an array and print it
                Message[] messages = emailFolder.getMessages();
                System.out.println("messages.length---" + messages.length);

                for (int i = 0; i < messages.length; i++) {
                    Message message = messages[i];
                    System.out.println("---------------------------------");
                    writePart(message);
                    String line = reader.readLine();
                    System.out.println(line);
                    if ("YES".equals(line)) {
                        System.out.println("Inside if");
                        message.writeTo(System.out);
                    } else if ("QUIT".equals(line)) {
                        System.out.println("inside else if");
                        break;
                    }
                }

                // close the store and folder objects
                emailFolder.close(false);
                store.close();

            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void writePart(Part p) throws Exception {
            if (p instanceof Message)
                //Call methos writeEnvelope
                writeEnvelope((Message) p);

            System.out.println("----------------------------");
            System.out.println("CONTENT-TYPE: " + p.getContentType());

            //check if the content is plain text
            if (p.isMimeType("text/plain")) {
                System.out.println("This is plain text");
                System.out.println("---------------------------");
                System.out.println((String) p.getContent());
            }
            //check if the content has attachment
            else if (p.isMimeType("multipart/*")) {
                System.out.println("This is a Multipart");
                System.out.println("---------------------------");
                Multipart mp = (Multipart) p.getContent();
                int count = mp.getCount();
                for (int i = 0; i < count; i++)
                    writePart(mp.getBodyPart(i));
            }
            //check if the content is a nested message
            else if (p.isMimeType("message/rfc822")) {
                System.out.println("This is a Nested Message");
                System.out.println("---------------------------");
                writePart((Part) p.getContent());
            }
            //check if the content is an inline image
            else if (p.isMimeType("image/jpeg")) {
                System.out.println("--------> image/jpeg");
                Object o = p.getContent();

                InputStream x = (InputStream) o;
                // Construct the required byte array
                System.out.println("x.length = " + x.available());
                byte[] bArray = new byte[x.available()];

                while (x.available() > 0) {
                    int result = x.read(bArray);
                }
                FileOutputStream f2 = new FileOutputStream("/tmp/image.jpg");
                f2.write(bArray);
            }
            else if (p.getContentType().contains("image/")) {
                System.out.println("content type" + p.getContentType());
                File f = new File("image" + new Date().getTime() + ".jpg");
                DataOutputStream output = new DataOutputStream(
                        new BufferedOutputStream(new FileOutputStream(f)));
                com.sun.mail.util.BASE64DecoderStream test =
                        (com.sun.mail.util.BASE64DecoderStream) p
                                .getContent();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = test.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }
            else {
                Object o = p.getContent();
                if (o instanceof String) {
                    System.out.println("This is a string");
                    System.out.println("---------------------------");
                    System.out.println((String) o);
                }
                else if (o instanceof InputStream) {
                    System.out.println("This is just an input stream");
                    System.out.println("---------------------------");
                    InputStream is = (InputStream) o;
                    is = (InputStream) o;
                    int c;
                    while ((c = is.read()) != -1)
                        System.out.write(c);
                }
                else {
                    System.out.println("This is an unknown type");
                    System.out.println("---------------------------");
                    System.out.println(o.toString());
                }
            }

        }
        /*
        * This method would print FROM,TO and SUBJECT of the message
        */
        public void writeEnvelope(Message m) throws Exception {
            System.out.println("This is the message envelope");
            System.out.println("---------------------------");
            Address[] a;

            // FROM
            if ((a = m.getFrom()) != null) {
                for (int j = 0; j < a.length; j++)
                    System.out.println("FROM: " + a[j].toString());
            }

            // TO
            if ((a = m.getRecipients(Message.RecipientType.TO)) != null) {
                for (int j = 0; j < a.length; j++)
                    System.out.println("TO: " + a[j].toString());
            }

            // SUBJECT
            if (m.getSubject() != null) {
                System.out.println("SUBJECT: " + m.getSubject());
                inboxList.add(m.getSubject());

            }

        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("Beginning of oncreate method");

        new fetcher().execute();
        //Call method fetch
        inboxView = inflater.inflate(R.layout.inbox_layout, container, false);
        inboxListView = (ListView) inboxView.findViewById(R.id.inboxListView);
        inboxList = new ArrayList<>();
        inboxList.add("hello");
        inboxAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, inboxList);
        inboxListView.setAdapter(inboxAdapter);
        inboxAdapter.notifyDataSetChanged();
        for (String i: inboxList){
            System.out.println(i);
        }

        return inboxView;


    }

}










