package fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.caleb.sift11.Email;
import com.example.caleb.sift11.R;
import com.example.caleb.sift11.SendMail;

import java.util.ArrayList;
import java.util.List;


import static fragments.DraftsFragment.cloneDrafts;
import static fragments.DraftsFragment.setDrafts;
import static fragments.SentFragment.addSent;

/**
 * Created by Caleb on 11/19/2016.
 */

public class SendFragment extends Fragment implements View.OnClickListener{
    //Declaring EditText
    private EditText editTextEmail;
    private  EditText editTextSubject;
    private  EditText editTextMessage;
    public   View sendView;

    private static String sRecipient, sSubject, sMessage;

    public static void setsRecipient(String r){
        sRecipient = r;
    }

    public static void setsSubject(String s){
        sSubject = s;
    }

    public static void setsMessage(String m){
        sMessage = m;
    }

    //Send button

    private ImageView imageAttach;
    private ImageView imageSend;
    private ImageView saveDraft;
    private Email nDraft;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sendView = inflater.inflate(R.layout.send_layout, container, false);


        //Initializing the views
        editTextEmail = (EditText) sendView.findViewById(R.id.editTextEmail);
        editTextSubject = (EditText) sendView.findViewById(R.id.editTextSubject);
        editTextMessage = (EditText) sendView.findViewById(R.id.editTextMessage);

        editTextEmail.setText(sRecipient);
        editTextSubject.setText(sSubject);
        editTextMessage.setText(sMessage);

        sRecipient = "";
        sSubject = "";
        sMessage = "";


        imageSend = (ImageView) sendView.findViewById(R.id.imvSend);
        imageAttach = (ImageView) sendView.findViewById(R.id.imvAttach);
        saveDraft = (ImageView) sendView.findViewById(R.id.imvDraft);
        //Adding click listener

        imageSend.setOnClickListener(this);
        saveDraft.setOnClickListener(this);
        imageAttach.setOnClickListener(this);
        return sendView;
    }



    private void saveDraft()
    {
        String recipient = editTextEmail.getText().toString().trim();
        String subject = editTextSubject.getText().toString().trim();
        String message = editTextMessage.getText().toString().trim();
        if(message.equals("") && subject.equals("") && recipient.equals(""))
        {
            Toast.makeText(getActivity(), "Cannot save empty draft", Toast.LENGTH_LONG).show();
        }
        else
        {

        if(recipient.equals(""))
        {
            recipient = "No Recipients";
        }
        if(subject.equals(""))
        {
            subject = "Empty Subject";
        }
        if(message.equals(""))
        {
            message = "No message";
        }
            nDraft = new Email(recipient, subject, message);

            List<Email> draftsTemp = new ArrayList<Email>();
            cloneDrafts(draftsTemp);
            draftsTemp.add(nDraft);
            setDrafts(draftsTemp);
            Toast.makeText(getActivity(), "Message saved to Drafts", Toast.LENGTH_LONG).show();
        }


    }

    private void sendEmail() {
        //Getting content for email
        String recipient = editTextEmail.getText().toString().trim();
        String subject = editTextSubject.getText().toString().trim();
        String message = editTextMessage.getText().toString().trim();

        if(isValid(recipient)){
            SendMail sm = new SendMail(getContext(), recipient, subject, message);

            //Executing sendmail to send email
            sm.execute();
            Email nSent = new Email(recipient, subject, message);
            addSent(nSent);
        }
        else
            Toast.makeText(getActivity(), "Invalid email address", Toast.LENGTH_LONG).show();
        //Creating SendMail object

    }

    //TODO: COPY VALIDATION LOGIC HERE OR REPLACE WITH CALL TO OTHER VALIDATION METHOD
    private static boolean isValid(String m){
        boolean validity = false;
        if(m.contains("@"))
            validity = true;


        return validity;
    }

    @Override
    public void onClick(View v) {
        if(v==imageSend)
            sendEmail();
        else if(v==saveDraft)
            saveDraft();
        else if(v==imageAttach)
            ;
    }
}
