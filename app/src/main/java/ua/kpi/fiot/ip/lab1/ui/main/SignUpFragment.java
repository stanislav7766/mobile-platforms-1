package ua.kpi.fiot.ip.lab1.ui.main;

import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ua.kpi.fiot.ip.lab1.R;

public class SignUpFragment extends Fragment {

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button signUpButton;
    private FirebaseAuth mAuth;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sign_up_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emailEditText= view.findViewById(R.id.signup_email_edit_text);
        passwordEditText= view.findViewById(R.id.signup_password_edit_text);
        confirmPasswordEditText= view.findViewById(R.id.signup_confirm_password_edit_text);

        signUpButton= view.findViewById(R.id.signup_done_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields()){
                    createUser();
                }
            }
        });
        mAuth = FirebaseAuth.getInstance();

    }

    private void createUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        final FragmentActivity activity = getActivity();
        final ProgressDialog dialog = ProgressDialog.show(activity, "",
                "Loading. Please wait...", true);
        Task<AuthResult> task =  mAuth.createUserWithEmailAndPassword(email, password);
        task.addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                dialog.dismiss();
                if (task.isSuccessful()) {
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, MainFragment.newInstance())
                            .commitNow();
                } else {
                    Toast.makeText(activity, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateFields() {

        if (emailEditText.getText().length() == 0) {
            return showError("EMail cannot be empty");
        } else if  (passwordEditText.getText().length() == 0) {
            return showError("Password cannot be empty");
        } else if (confirmPasswordEditText.getText().length() == 0) {
            return showError("Confirm password cannot be empty");
        } else if (confirmPasswordEditText.getText().toString() == passwordEditText.getText().toString()) {
            return showError("Passwords should match");
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
