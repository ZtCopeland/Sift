package fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.caleb.sift11.Email;

import com.example.caleb.sift11.R;



import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.mail.Message;

import static com.example.caleb.sift11.MainActivity.cloneInbox;

/**
 * Created by Caleb on 11/20/2016.
 */

public class InboxFragment extends Fragment {
    private class startsWithComparator implements Comparator<Email>{
        private String match;

        startsWithComparator(String match){
            this.match = match;
        }

        @Override
        public int compare(Email e1, Email e2){
            if(e1.getdMessage().startsWith(match)){
                return 1;
            }
            if(e2.getdMessage().startsWith(match)){
                return 1;
            }
            return -1;
        }
    }

    View inboxView;
    private static boolean expand = false;
    private Button refresh;
    private static ArrayList<Email> inboxList = new ArrayList<>();
    private static ArrayList<Email> tempInbox = new ArrayList<>();
    private static ArrayAdapter<Email> adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inboxView = inflater.inflate(R.layout.inbox_layout, container, false);

        System.out.println("Beginning of oncreate method");

        cloneInbox(inboxList);
        //Email sortingEmail = new Email("","","hello");
        Collections.sort(inboxList, new startsWithComparator("hello"));
        //inboxList.sort(startsWithComparator(sortingEmail));
        cloneInbox(tempInbox);


        ListView list = (ListView) inboxView.findViewById(R.id.lvInbox);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id){

                Email currentEmail = inboxList.get(position);

                if(currentEmail.getExpanded()){
                    expand = false;
                    currentEmail.setExpanded(false);
                }
                else{
                    expand = true;
                    currentEmail.setExpanded(true);
                }
                if(adapter==null){
                    adapter = new MyListAdapter();
                }
                adapter.getView(position, viewClicked, parent);
            }
        });
        populateLV();
        //Call method fetch
        //inboxView = inflater.inflate(R.layout.inbox_layout, container, false);
        //inboxListView = (ListView) inboxView.findViewById(R.id.inboxListView);

        //inboxAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, inboxList);
        //inboxListView.setAdapter(inboxAdapter);
        //inboxAdapter.notifyDataSetChanged();


        return inboxView;


    }



    private void populateLV(){
        adapter = new MyListAdapter();
        ListView list = (ListView)  inboxView.findViewById(R.id.lvInbox);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Email> {
        public MyListAdapter(){
            super(getActivity(), R.layout.inbox_view, inboxList);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Make sure not given a null view
            View itemView = convertView;
            if (itemView == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                itemView = inflater.inflate(R.layout.inbox_view, parent, false);

            }
            //Find draft
            Email currentEmail = inboxList.get(position);
           /* ImageView delete = (ImageView) itemView.findViewById(R.id.delete);
            delete.setTag(new Integer(position));
            delete.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    Toast.makeText(getActivity(), "Draft Deleted", Toast.LENGTH_SHORT).show();
                    int d = (Integer)(view.getTag());
                    sent.remove(d);
                    FragmentManager fm = getFragmentManager();
                    fm.beginTransaction().replace(R.id.content_frame, new DraftsFragment()).commit();
                }
            });*/
            //Fill the View

            //Recipient(s)
            TextView list_recipient = (TextView) itemView.findViewById(R.id.item_i_recipients);
            list_recipient.setText("To: " + currentEmail.getdRecipient());

            //Sender
            TextView list_sender = (TextView) itemView.findViewById(R.id.item_i_sender);
            list_recipient.setText("From: " + currentEmail.getdSender());


            //Subject
            if (!expand) {

                String tempS = currentEmail.getdSubject();


                if (currentEmail.getdSubject().length() < 25) {
                    for (int k = currentEmail.getdSubject().length() - 1; k < 27; k++) {
                        tempS += " ";
                    }
                }

                //Display only first few characters of a subject in the draft
                String sub = tempS.substring(0, 25);
                if (currentEmail.getdSubject().length() > 25)
                    sub += " . . .";

                TextView list_subject = (TextView) itemView.findViewById(R.id.item_i_subject);
                list_subject.setText(sub);

                //Start of message
                String temp = currentEmail.getdMessage();
                //Display only first 100 characters of message in the drafts layout
                if (currentEmail.getdMessage().length() < 100) {

                    for (int j = currentEmail.getdMessage().length() - 1; j < 101; j++) {// -1?
                        temp += " ";
                    }
                }


                String start = temp.substring(0, 100);

                if (currentEmail.getdMessage().length() > 100)
                    start += " . . .";

                TextView list_start = (TextView) itemView.findViewById(R.id.item_i_message);
                list_start.setText("\t\t\t\t" + start);
                //list_start.setText(currentDraft.getdMessage());


                TextView list_date = (TextView) itemView.findViewById(R.id.item_i_date);
                list_date.setText(currentEmail.getdDate());

            }
            else
            {
                TextView list_start = (TextView) itemView.findViewById(R.id.item_i_message);
                list_start.setText("\t\t\t\t"+currentEmail.getdMessage());

                TextView list_date = (TextView) itemView.findViewById(R.id.item_i_date);
                list_date.setText(currentEmail.getdDate());

                TextView list_subject = (TextView) itemView.findViewById(R.id.item_i_subject);
                list_subject.setText(currentEmail.getdSubject());

            }
            return itemView;
        }

    }
}