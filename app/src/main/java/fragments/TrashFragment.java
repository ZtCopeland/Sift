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

/**
 * Created by Caleb on 11/20/2016.
 */

public class TrashFragment extends Fragment {
    private static List<Email> trash = new ArrayList<Email>();
    private static List<Email> tempTrash;
    private EditText etSearch;

    View trashView;

    private static ArrayAdapter<Email> adapter;
    public void addTrash(Email d){
        trash.add(d);
    }


    public static void cloneTrash(List<Email> n) {

        for (int i = 0 ; i<trash.size();i++){
            n.add(trash.get(i)) ;
        }
    }

    public static void setTrash(List<Email> d) {
        trash.clear();

        for (int i = 0 ; i<d.size();i++){
            trash.add(d.get(i)) ;
        }
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        trashView = inflater.inflate(R.layout.trash_layout, container, false);

        tempTrash = new ArrayList<Email>();
        cloneTrash(tempTrash);

        //Search function HERE
        /*The following edit text handles searching through the list. */
        etSearch = (EditText) trashView.findViewById(R.id.etSearchTrash);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")) {
                    //reset list
                    setTrash(tempTrash);
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
        return trashView;
    }

    private void searchItem(String s){
        setTrash(tempTrash); //<--Ensures backspacing will bring back everything that matches
        Iterator<Email> iter = trash.iterator();
        Email d;
        while (iter.hasNext()) {
            d = iter.next();
            if (!d.getdMessage().contains(s) && !d.getdSubject().contains(s) && !d.getdRecipient().contains(s) && !d.getdDate().contains(s))
                iter.remove();
        }
        //update adapter
        adapter.notifyDataSetChanged();

    }
    //Listener for Clicking a ListView Item in Trash fragment
    private void registerClickCallback() {

        ListView list = (ListView) trashView.findViewById(R.id.lvTrash);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id){
               //On clicking list item

            }
        });



    }
    //Populate the ListView with trash
    private void populateLV(){

        adapter = new MyListAdapter();
        ListView list = (ListView)  trashView.findViewById(R.id.lvTrash);
        list.setAdapter(adapter);
    }
    //Custom adapter
    private class MyListAdapter extends ArrayAdapter<Email>{


        public MyListAdapter(){

            super(getActivity(), R.layout.trash_view, trash);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Make sure not given a null view
            View itemView = convertView;
            if (itemView == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                itemView = inflater.inflate(R.layout.trash_view, parent, false);
            }
            //Find trash
            Email currentTrash = trash.get(position);
            ImageView delete = (ImageView) itemView.findViewById(R.id.delete);
            delete.setTag(new Integer(position));
            delete.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    //Permanently delete mail here
                    Toast.makeText(getActivity(), "Trash permanently deleted", Toast.LENGTH_SHORT).show();
                    int d = (Integer)(view.getTag());
                    trash.remove(d);
                    FragmentManager fm = getFragmentManager();
                    fm.beginTransaction().replace(R.id.content_frame, new TrashFragment()).commit();
                }
            });
            //Fill the View

            //Recipient(s)
            TextView list_recipient = (TextView) itemView.findViewById(R.id.item_i_recipients);
            list_recipient.setText("To: " + currentTrash.getdRecipient());

            //Subject
            String tempS = currentTrash.getdSubject();
            if(currentTrash.getdSubject().length()< 25){
                for (int k = currentTrash.getdSubject().length()-1; k<27; k++){
                    tempS +=" ";
                }
            }
            //Display only first few characters of a subject in the trash
            String sub = tempS.substring(0, 25);
            if(currentTrash.getdSubject().length()>25)
                sub +=" . . .";
            TextView list_subject = (TextView) itemView.findViewById(R.id.item_i_subject);
            list_subject.setText(sub);

            //Start of message
            String temp = currentTrash.getdMessage();
            //Display only first 100 characters of message in the trash layout
            if (currentTrash.getdMessage().length() < 100){

                for (int j = currentTrash.getdMessage().length()-1; j < 101; j++) {// -1?
                    temp += " ";
                }
            }


            String start = temp.substring(0, 100);

            if(currentTrash.getdMessage().length() > 100)
                start +=" . . .";

            TextView list_start = (TextView) itemView.findViewById(R.id.item_start);
            list_start.setText("\t\t\t\t"+start);



            TextView list_date = (TextView) itemView.findViewById(R.id.item_date);
            list_date.setText(currentTrash.getdDate());
            return itemView;
        }


    }

}