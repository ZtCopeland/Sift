package fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caleb.sift11.Email;
import com.example.caleb.sift11.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static fragments.SendFragment.setsMessage;
import static fragments.SendFragment.setsRecipient;
import static fragments.SendFragment.setsSubject;


/**
 * Created by Caleb on 11/23/2016.
 */

public class SentFragment extends Fragment {
    View sentView;
    private static boolean expand = false;
    private static List<Email> sent = new ArrayList<Email>();
    private static List<Email> tempSent;
    private EditText etSearch;
    private static ArrayAdapter<Email> adapter;
    public static void cloneSent(List<Email> n) {

        for (int i = 0 ; i<sent.size();i++){
            n.add(sent.get(i)) ;
        }
    }

    public static void setSent(List<Email> s) {
        sent.clear();

        for (int i = 0 ; i<s.size();i++){
            sent.add(s.get(i)) ;
        }
    }

    public static void addSent(Email e){
        sent.add(e);
    }

    private void searchItem(String s){
        setSent(tempSent); //<--Ensures backspacing will bring back everything that matches
        Iterator<Email> iter = sent.iterator();
        Email d;
        while (iter.hasNext()) {
            d = iter.next();
            if (!d.getdMessage().contains(s) && !d.getdSubject().contains(s) && !d.getdRecipient().contains(s) && !d.getdDate().contains(s))
                iter.remove();
        }
        //update adapter
        adapter.notifyDataSetChanged();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sentView = inflater.inflate(R.layout.sent_layout, container, false);


        ListView list = (ListView) sentView.findViewById(R.id.lvSent);
        tempSent = new ArrayList<Email>();
        cloneSent(tempSent);

        etSearch = (EditText) sentView.findViewById(R.id.etSearchSent);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")) {
                    //reset list
                    setSent(tempSent);
                    adapter.notifyDataSetChanged();
                }
                else {
                    //perform search
                    searchItem(charSequence.toString());
                }

            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id){

                Email currentEmail = sent.get(position);

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
        return sentView;
    }

        private void populateLV(){
        adapter = new MyListAdapter();
        ListView list = (ListView)  sentView.findViewById(R.id.lvSent);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Email> {
        public MyListAdapter(){
            super(getActivity(), R.layout.sent_view, sent);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Make sure not given a null view
            View itemView = convertView;
            if (itemView == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                itemView = inflater.inflate(R.layout.sent_view, parent, false);

            }
            //Find draft
            Email currentEmail = sent.get(position);
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
            TextView list_recipient = (TextView) itemView.findViewById(R.id.item_s_recipient);
            list_recipient.setText("To: " + currentEmail.getdRecipient());



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

                TextView list_subject = (TextView) itemView.findViewById(R.id.item_s_subject);
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

                TextView list_start = (TextView) itemView.findViewById(R.id.item_s_message);
                list_start.setText("\t\t\t\t" + start);
                //list_start.setText(currentDraft.getdMessage());


                TextView list_date = (TextView) itemView.findViewById(R.id.item_s_date);
                list_date.setText(currentEmail.getdDate());

            }
            else
            {
                TextView list_start = (TextView) itemView.findViewById(R.id.item_s_message);
                list_start.setText("\t\t\t\t"+currentEmail.getdMessage());

                TextView list_date = (TextView) itemView.findViewById(R.id.item_s_date);
                list_date.setText(currentEmail.getdDate());

                TextView list_subject = (TextView) itemView.findViewById(R.id.item_s_subject);
                list_subject.setText(currentEmail.getdSubject());

            }
            return itemView;
        }

    }
}
