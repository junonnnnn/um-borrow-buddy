package com.example.umborrowbuddy.features.module1.auth;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.umborrowbuddy.R;

/** First UI stage only: no authentication request or session is created. */
public class LoginActivity extends AppCompatActivity {
    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button registerButton;
    private Button resetButton;
    private ProgressBar progress;
    private TextView feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.m1_activity_login);

        View root = findViewById(R.id.m1_login_root);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets keyboard = windowInsets.getInsets(WindowInsetsCompat.Type.ime());
            view.setPadding(bars.left, bars.top, bars.right,
                    Math.max(bars.bottom, keyboard.bottom));
            return windowInsets;
        });
        ViewCompat.requestApplyInsets(root);

        emailInput = findViewById(R.id.m1_email);
        passwordInput = findViewById(R.id.m1_password);
        loginButton = findViewById(R.id.m1_login);
        registerButton = findViewById(R.id.m1_register);
        resetButton = findViewById(R.id.m1_forgot_password);
        progress = findViewById(R.id.m1_progress);
        feedback = findViewById(R.id.m1_feedback);
        // Do not persist the password through Android's view-state saving.
        passwordInput.setSaveEnabled(false);
        setLoading(false);

        loginButton.setOnClickListener(view -> handleLogin());
        registerButton.setOnClickListener(view ->
                feedback.setText(R.string.m1_register_unavailable));
        resetButton.setOnClickListener(view ->
                feedback.setText(R.string.m1_reset_unavailable));
    }

    /** Validate locally. A valid form still cannot sign in until Firebase is configured. */
    private void handleLogin() {
        emailInput.setError(null);
        passwordInput.setError(null);
        String email = emailInput.getText().toString().trim();
        // Keep passwords exactly as entered: never trim or log them.
        String password = passwordInput.getText().toString();

        if (!isUmEmail(email)) {
            emailInput.setError(getString(R.string.m1_invalid_email));
            emailInput.requestFocus();
            feedback.setText(R.string.m1_check_fields);
            return;
        }
        if (password.isEmpty()) {
            passwordInput.setError(getString(R.string.m1_password_required));
            passwordInput.requestFocus();
            feedback.setText(R.string.m1_check_fields);
            return;
        }
        feedback.setText(R.string.m1_auth_unavailable);
    }

    /** Exact domain matching rejects lookalikes and additional subdomains. */
    private boolean isUmEmail(String email) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        }
        String domain = email.substring(email.lastIndexOf('@') + 1);
        return domain.equalsIgnoreCase("siswa.um.edu.my")
                || domain.equalsIgnoreCase("um.edu.my");
    }

    /**
     * UI hook for a future real request. Currently called only with false:
     * there is no network operation, fake delay or simulated login success.
     */
    private void setLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!loading);
        registerButton.setEnabled(!loading);
        resetButton.setEnabled(!loading);
        emailInput.setEnabled(!loading);
        passwordInput.setEnabled(!loading);
    }
}
