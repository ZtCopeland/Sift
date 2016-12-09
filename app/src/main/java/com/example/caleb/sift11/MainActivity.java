package com.example.caleb.sift11;

import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import fragments.DraftsFragment;
import fragments.FilterFragment;
import fragments.InboxFragment;
import fragments.SendFragment;
import fragments.SentFragment;
import fragments.TrashFragment;


import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.gmail.GmailScopes;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;


import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;

import static com.example.caleb.sift11.LoginActivity.getEmailAccount;
import static com.example.caleb.sift11.LoginActivity.getEmailPassword;
import static fragments.DraftsFragment.setDrafts;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private static ArrayList<Message> inboxList = new ArrayList<>();
    private static ArrayList<Email> emailList = new ArrayList<>();
    private ProgressBar spinner;
    private static final String[] SCOPES = {GmailScopes.GMAIL_LABELS};
    private static String host = "pop.gmail.com";// change accordingly
    private static String mailStoreType = "pop3";
    private static String username = getEmailAccount();// change accordingly
    private static String password = getEmailPassword();// change accordingly




    public static void cloneInbox(ArrayList<Email> n) {

        for (Email i: emailList){
            n.add(i);
        }
    }

    public static void setInbox(List<Email> s) {
        emailList.clear();

        for (int i = 0 ; i<s.size();i++){
            emailList.add(s.get(i));
        }
    }

    public static void addInbox(Email e){
        emailList.add(e);
    }
    public static void fetcherer () {
        try {
            new fetcher().execute().get();
        } catch (Exception e) {

        }
    }

    public void refreshing(View view) {
        fetcherer();
    }

    public static class fetcher extends AsyncTask<Void, Void, Void> {
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


                    try {
                        inboxList.add(message);
                    } catch (Exception e) {
                        e.printStackTrace(System.out);
                    }

                    String line = reader.readLine();
                    //System.out.println(line);
                    if ("YES".equals(line)) {
                        System.out.println("Inside if");
                        message.writeTo(System.out);
                    } else if ("QUIT".equals(line)) {
                        System.out.println("inside else if");
                        break;
                    }
                }


                for (Message i : inboxList) {
                    try {
                        Email temp = new Email();
                        temp.setdSubject(i.getSubject());
                        temp.setdRecipient(i.getAllRecipients().toString());
                        temp.setdMessage(i.getContent().toString());
                        temp.setdSender(i.getFrom().toString());
                        System.out.println(i.getFrom());
                        temp.setdDate(i.getSentDate().toString());
                        emailList.add(temp);
                        //System.out.println(i.getSubject());
                    } catch (Exception e) {
                        e.printStackTrace(System.out);
                    }
                }
                /*try {
                    for (Message i : inboxList) {
                        try {
                            System.out.println("subject" + i.getSubject());
                        } catch (Exception e) {
                            e.printStackTrace(System.out);
                        }
                        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }*/

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

            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);

        try {
            new fetcher().execute().get();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        spinner.setVisibility(View.GONE);






        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        try {
            System.out.println(inboxList.get(0).getSubject());
        } catch (Exception e) {

        }


    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fm = getFragmentManager();

        if (id == R.id.nav_inbox) {
            // Handle the camera action
            fm.beginTransaction().replace(R.id.content_frame, new InboxFragment()).commit();
        } else if (id == R.id.nav_sent) {
            fm.beginTransaction().replace(R.id.content_frame, new SentFragment()).commit();
        } else if (id == R.id.nav_drafts) {
            fm.beginTransaction().replace(R.id.content_frame, new DraftsFragment()).commit();
        } else if (id == R.id.nav_trash) {
            fm.beginTransaction().replace(R.id.content_frame, new TrashFragment()).commit();
        } else if (id == R.id.nav_filter) {
            fm.beginTransaction().replace(R.id.content_frame, new FilterFragment()).commit();
        } else if (id == R.id.nav_send) {
            fm.beginTransaction().replace(R.id.content_frame, new SendFragment()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}
