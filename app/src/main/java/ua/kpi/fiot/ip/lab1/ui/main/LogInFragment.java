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

public class LogInFragment extends Fragment {

    public static LogInFragment newInstance() {
        return new LogInFragment();
    }

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button logInButton;
    private Button btnSignUp;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.log_in_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        emailEditText = view.findViewById(R.id.email_edit_text);
        passwordEditText = view.findViewById(R.id.password_edit_text);

        logInButton =view.findViewById(R.id.login_button);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields()){
                    logIn();
                }
            }
        });

        btnSignUp = view.findViewById(R.id.signup_button);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, SignUpFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void logIn() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        final FragmentActivity activity = getActivity();
        final ProgressDialog dialog = ProgressDialog.show(activity, "",
                "Loading. Please wait...", true);
        Task<AuthResult> task =  mAuth.signInWithEmailAndPassword (email, password);
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
