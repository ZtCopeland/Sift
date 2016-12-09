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

import static com.example.caleb.sift11.R.layout.drafts_layout;

import static fragments.SendFragment.setsMessage;
import static fragments.SendFragment.setsRecipient;
import static fragments.SendFragment.setsSubject;


/**
 * Created by Caleb on 11/20/2016.
 * Description:
 *  DraftFragment pertains to the drafts_layout functionality of the app.
 *      This Fragment routinely interacts with the SendFragement by populating it with
 *      the contents of a draft.  Additionally, this Fragment handles the search functionality
 *      of the drafts_layout.
 * Functionality Details:
 *  An arrayList of Email objects is used to store drafts. Then, an ArrayAdapter of email objects
 *      is used to populate a ListView Widget.
 *  Search functions via TextChanged listener.  Each time the text is changed,
 *
 */

public class DraftsFragment extends Fragment {
    private static List<Email> drafts = new ArrayList<Email>();
    private static List<Email> tempDrafts;
    private EditText etSearch;

    private static ArrayAdapter<Email> adapter;
    public void addDraft(Email d){
        drafts.add(d);
    }


    //public void deleteDraft(){}

    public static void cloneDrafts(List<Email> n) {

        for (int i = 0 ; i<drafts.size();i++){
            n.add(drafts.get(i)) ;
        }
    }

    public static void setDrafts(List<Email> d) {
        drafts.clear();

        for (int i = 0 ; i<d.size();i++){
            drafts.add(d.get(i)) ;
        }
    }

    View draftsView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        draftsView = inflater.inflate(drafts_layout, container, false);

        /*tempDrafts is independent clone (NOT REFERENCE) of drafts.  Saves original list
        * so that it can be restored.*/
        tempDrafts = new ArrayList<Email>();
        cloneDrafts(tempDrafts);

        /*The following edit text handles searching through the list. */
        etSearch = (EditText) draftsView.findViewById(R.id.etDSearch);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")) {
                    //reset list
                    setDrafts(tempDrafts);
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

       populateLV();
       registerClickCallback();
        return draftsView;
    }



    //List only items that match by removing them from the drafts arraylist
    private void searchItem(String s){
        setDrafts(tempDrafts); //<--Ensures backspacing will bring back everything that matches
        Iterator<Email> iter = drafts.iterator();
        Email d;
        while (iter.hasNext()) {
             d = iter.next();
            if (!d.getdMessage().contains(s) && !d.getdSubject().contains(s) && !d.getdRecipient().contains(s) && !d.getdDate().contains(s))
                iter.remove();
        }
        //update adapter
        adapter.notifyDataSetChanged();

    }
    //Listener for Clicking a ListView Item in drafts fragment
    private void registerClickCallback() {

        ListView list = (ListView) draftsView.findViewById(R.id.draftsView);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id){
                Email clickedDraft = drafts.get(position);

                //Set string values that are loaded into SendFragment in its onCreate method
                setsRecipient(clickedDraft.getdRecipient());
                setsSubject(clickedDraft.getdSubject());
                setsMessage(clickedDraft.getdMessage());

                Toast.makeText(getActivity(), "Loading Draft . . .", Toast.LENGTH_SHORT).show();
                //Switch to SendFragment which will self populate with this draft
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.content_frame, new SendFragment()).commit();

            }
         });



    }
    //Populate the ListView with drafts
    private void populateLV(){

        adapter = new MyListAdapter();
        ListView list = (ListView)  draftsView.findViewById(R.id.draftsView);
        list.setAdapter(adapter);
    }
    //Custom adapter
    private class MyListAdapter extends ArrayAdapter<Email>{


        public MyListAdapter(){

            super(getActivity(), R.layout.draft_view, drafts);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Make sure not given a null view
            View itemView = convertView;
            if (itemView == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                itemView = inflater.inflate(R.layout.draft_view, parent, false);
            }
            //Find draft
            Email currentDraft = drafts.get(position);
            ImageView delete = (ImageView) itemView.findViewById(R.id.delete);
            delete.setTag(new Integer(position));
            delete.setOnClickListener(new View.OnClickListener(){
               public void onClick(View view){
                   Toast.makeText(getActivity(), "Draft Deleted", Toast.LENGTH_SHORT).show();
                   int d = (Integer)(view.getTag());
                   drafts.remove(d);
                   FragmentManager fm = getFragmentManager();
                   fm.beginTransaction().replace(R.id.content_frame, new DraftsFragment()).commit();
               }
            });
            //Fill the View

            //Recipient(s)
            TextView list_recipient = (TextView) itemView.findViewById(R.id.item_i_recipients);
            list_recipient.setText("To: " + currentDraft.getdRecipient());

            //Subject
            String tempS = currentDraft.getdSubject();
            if(currentDraft.getdSubject().length()< 25){
                for (int k = currentDraft.getdSubject().length()-1; k<27; k++){
                    tempS +=" ";
                }
            }
            //Display only first few characters of a subject in the draft
            String sub = tempS.substring(0, 25);
            if(currentDraft.getdSubject().length()>25)
                sub +=" . . .";
            TextView list_subject = (TextView) itemView.findViewById(R.id.item_i_subject);
            list_subject.setText(sub);

            //Start of message
            String temp = currentDraft.getdMessage();
            //Display only first 100 characters of message in the drafts layout
            if (currentDraft.getdMessage().length() < 100){

                for (int j = currentDraft.getdMessage().length()-1; j < 101; j++) {// -1?
                    temp += " ";
                }
            }


            String start = temp.substring(0, 100);

            if(currentDraft.getdMessage().length() > 100)
                start +=" . . .";

            TextView list_start = (TextView) itemView.findViewById(R.id.item_start);
            list_start.setText("\t\t\t\t"+start);
            //list_start.setText(currentDraft.getdMessage());


            TextView list_date = (TextView) itemView.findViewById(R.id.item_date);
            list_date.setText(currentDraft.getdDate());
            return itemView;
        }


    }

}
