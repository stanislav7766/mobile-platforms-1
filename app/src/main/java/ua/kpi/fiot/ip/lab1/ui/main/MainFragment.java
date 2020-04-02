package ua.kpi.fiot.ip.lab1.ui.main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import ua.kpi.fiot.ip.lab1.MainActivity;
import ua.kpi.fiot.ip.lab1.R;
import ua.kpi.fiot.ip.lab1.User;

public class MainFragment extends Fragment {

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText groupEditText;
    private Button updateButton;
    private Button logoutButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firstNameEditText= view.findViewById(R.id.first_name_edit_text);
        lastNameEditText= view.findViewById(R.id.last_name_edit_text);
        groupEditText= view.findViewById(R.id.group_edit_text);
        updateButton= view.findViewById(R.id.update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields()){
                    updateUser();

                }

            }
        });
        logoutButton= view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }

        });
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        
        getUser();
    }

    private void getUser() {
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                "Loading. Please wait...", true);
        DocumentReference docRef = db.collection("users").document(currentUser.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                dialog.dismiss();
                User user = documentSnapshot.toObject(User.class);
                updateUI(user);
            }
        });
    }

    private void logoutUser() {
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                "Loading. Please wait...", true);

        mAuth.signOut();
        Intent i = new Intent(getActivity().getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    private void updateUser() {

        User user = new User();
        user.setFirstName(firstNameEditText.getText().toString());
        user.setLastName(lastNameEditText.getText().toString());
        user.setGroup(groupEditText.getText().toString());

        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                "Loading. Please wait...", true);
        DocumentReference docRef = db.collection("users").document(currentUser.getUid());
        docRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialog.dismiss();
                getUser();
            }
        });
    }

    private void updateUI(User user) {
        if (user == null) {
            firstNameEditText.setText("");
            lastNameEditText.setText("");
            groupEditText.setText("");
        } else {
            firstNameEditText.setText(user.getFirstName());
            lastNameEditText.setText(user.getLastName());
            groupEditText.setText(user.getGroup());
        }

    }

    private boolean validateFields() {

        if (firstNameEditText.getText().length() == 0) {
            return showError("First name cannot be empty");
        } else if  (lastNameEditText.getText().length() == 0) {
            return showError("Last name cannot be empty");
        } else if (groupEditText.getText().length() == 0) {
            return showError("Group password cannot be empty");
        }
        return true;
    }

    private boolean showError(String errorMessage) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Error message")
                .setMessage(errorMessage)
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        return false;
    }
}
