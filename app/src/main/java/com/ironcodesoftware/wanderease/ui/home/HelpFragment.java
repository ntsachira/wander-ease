package com.ironcodesoftware.wanderease.ui.home;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.ironcodesoftware.wanderease.R;
import com.ironcodesoftware.wanderease.model.HelpTicket;
import com.ironcodesoftware.wanderease.model.UserLogIn;
import com.ironcodesoftware.wanderease.model.WanderDialog;

import java.io.IOException;
import java.util.HashMap;


public class HelpFragment extends Fragment {

    UserLogIn login;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_help, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            login = UserLogIn.getLogin(getContext());
        } catch (IOException | ClassNotFoundException e) {
            getActivity().finish();
        }

        Button buttonSubmit = view.findViewById(R.id.help_submit_button);
        buttonSubmit.setOnClickListener(v->{
            EditText editTextSubject = view.findViewById(R.id.help_subject_edittext);
            EditText editTextMessage = view.findViewById(R.id.help_message_edittext);

            String subject = editTextSubject.getText().toString().trim();
            String message = editTextMessage.getText().toString().trim();

            if(subject.isEmpty()){
                editTextSubject.setError("Cannot be empty");
            } else if (message.isEmpty()) {
                editTextMessage.setError("Cannot be empty");
            }else{
                AlertDialog loading = WanderDialog.loading(getContext());
                loading.show();
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                HashMap<String,Object> map = new HashMap<>();
                map.put("subject", subject);
                map.put("message", message);
                map.put("user",login.getEmail());
                map.put("status", HelpTicket.NOT_RESPONDED);
                firestore.collection("help").add(map)
                        .addOnSuccessListener(documentReference -> {
                            view.post(()->{
                                loading.cancel();
                                editTextMessage.setText("");
                                editTextSubject.setText("");
                                Toast.makeText(getContext(), "Submitted success", Toast.LENGTH_LONG).show();
                            });

                        })
                        .addOnFailureListener(e->{
                            view.post(loading::cancel);
                        });
            }
        });
    }
}