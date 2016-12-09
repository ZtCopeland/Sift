package fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caleb.sift11.Email;
import com.example.caleb.sift11.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.example.caleb.sift11.MainActivity.cloneInbox;

/**
 * Created by Caleb on 11/23/2016.
 */

public class FilterFragment extends Fragment {
    View filterView;
    private static boolean expand = false;
    private static ArrayList<Email> filterList = new ArrayList<>();
    private static ArrayList<Email> tempFilter = new ArrayList<>();
    private static ArrayAdapter<Email> adapter;

    private boolean noFilter = true;
    private boolean emailFilter = false;
    private boolean subjectFilter = false;

    private EditText etSearch;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        filterView = inflater.inflate(R.layout.filter_layout, container, false);


        cloneInbox(filterList);
        cloneInbox(tempFilter);

        ListView list = (ListView) filterView.findViewById(R.id.lvFilter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id){

                Email currentEmail = filterList.get(position);

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

        final RadioButton radEmail = (RadioButton) filterView.findViewById(R.id.rdEmail);
        final RadioButton radImages = (RadioButton) filterView.findViewById(R.id.rdImages);
        final RadioButton radAttachments = (RadioButton) filterView.findViewById(R.id.rdAttachments);
        final RadioButton radNone = (RadioButton) filterView.findViewById(R.id.rdNone);

        radNone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //onRadioButtonClicked(radNone);
                boolean checked = ((RadioButton) radNone).isChecked();
                if(checked){
                    setFilter("none");
                }
            }
        });

        radImages.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onRadioButtonClicked(radImages);
            }
        });

        radAttachments.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //onRadioButtonClicked(radAttachments);
                boolean checked = ((RadioButton) radAttachments).isChecked();
                if(checked){
                    setFilter("subject");
                }

            }
        });

        radEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //onRadioButtonClicked(radEmail);
                boolean checked = ((RadioButton) radEmail).isChecked();
                if(checked){
                    setFilter("email");
                }
            }
        });

        etSearch = (EditText) filterView.findViewById(R.id.etSearchFilter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")) {
                    //reset list
                    applyFilter(tempFilter);
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




        return filterView;
    }


    public static void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rdEmail:
                if (checked)
                    Log.d("Test", "EMAIL CLICKED");
                    break;
            case R.id.rdImages:
                if (checked)
                    Log.d("Test", "Images CLICKED");
                    break;
            case R.id.rdAttachments:
                if (checked)
                    Log.d("Test", "Attachments CLICKED");
                    break;
            case R.id.rdNone:
                if (checked)
                    Log.d("Test", "None CLICKED");
                    break;
        }
    }

    private void populateLV(){
        adapter = new MyListAdapter();
        ListView list = (ListView)  filterView.findViewById(R.id.lvFilter);
        list.setAdapter(adapter);
    }

    public void setFilter(String filter){
        if(filter.equals("email")){
            noFilter = false;
            emailFilter = true;
            subjectFilter = false;        }
        else if(filter.equals("subject")){
            noFilter = false;
            emailFilter = false;
            subjectFilter = true;        }
        else if(filter.equals("none")){
            noFilter = true;
            emailFilter = false;
            subjectFilter = false;
        }
    }

    public boolean filterBody(Email e, String query) {
        return !e.getdMessage().contains(query);
    }

    public boolean filterSubject(Email e, String query){
        return !e.getdSubject().contains(query);
    }

    public boolean filterNone(Email e, String query){
        return !e.getdMessage().contains(query) && !e.getdRecipient().contains(query) && !e.getdDate().contains(query);
    }

    public static void applyFilter(List<Email> s) {
        filterList.clear();

        for (int i = 0 ; i<s.size();i++){
            filterList.add(s.get(i));
        }
    }
    private void searchItem(String s){
        applyFilter(tempFilter); //<--Ensures backspacing will bring back everything that matches
        Iterator<Email> iter = filterList.iterator();
        Email d;
        boolean filter;

        while (iter.hasNext()) {
            d = iter.next();
            if(subjectFilter){
                //System.out.println("SUBJECT FILTERING");
                filter = filterSubject(d, s);
            }
            else{
                //System.out.println("NONE FILTERING");
                filter = filterNone(d, s);
            }
            if (filter)
                iter.remove();
        }
        //update adapter
        //System.out.println("SUBJECT: " + subjectFilter);
        //System.out.println("EMAIL: " + emailFilter);
        //System.out.println("NONE: " + noFilter);

        adapter.notifyDataSetChanged();

    }


    private class MyListAdapter extends ArrayAdapter<Email> {
        public MyListAdapter(){
            super(getActivity(), R.layout.filter_view, filterList);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Make sure not given a null view
            View itemView = convertView;
            if (itemView == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                itemView = inflater.inflate(R.layout.filter_view, parent, false);

            }
            //Find draft
            Email currentEmail = filterList.get(position);
           ImageView delete = (ImageView) itemView.findViewById(R.id.delete);
            delete.setTag(new Integer(position));
            delete.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    Toast.makeText(getActivity(), "Email Deleted", Toast.LENGTH_SHORT).show();
                    int d = (Integer)(view.getTag());
                    filterList.remove(d);
                    FragmentManager fm = getFragmentManager();
                    fm.beginTransaction().replace(R.id.content_frame, new FilterFragment()).commit();
                }
            });
            //Fill the View

            //Recipient(s)
            TextView list_recipient = (TextView) itemView.findViewById(R.id.item_f_recipients);
            list_recipient.setText("To: " + currentEmail.getdRecipient());

            //Sender
            TextView list_sender = (TextView) itemView.findViewById(R.id.item_f_sender);
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

                TextView list_subject = (TextView) itemView.findViewById(R.id.item_f_subject);
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

                TextView list_start = (TextView) itemView.findViewById(R.id.item_f_message);
                list_start.setText("\t\t\t\t" + start);
                //list_start.setText(currentDraft.getdMessage());


                TextView list_date = (TextView) itemView.findViewById(R.id.item_f_date);
                list_date.setText(currentEmail.getdDate());

            }
            else
            {
                TextView list_start = (TextView) itemView.findViewById(R.id.item_f_message);
                list_start.setText("\t\t\t\t"+currentEmail.getdMessage());

                TextView list_date = (TextView) itemView.findViewById(R.id.item_f_date);
                list_date.setText(currentEmail.getdDate());

                TextView list_subject = (TextView) itemView.findViewById(R.id.item_f_subject);
                list_subject.setText(currentEmail.getdSubject());

            }
            return itemView;
        }

    }

}
