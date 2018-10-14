package net.smimran.cloud_note;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 0;
    private FirebaseAuth auth;

    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.login_coordinatorLayout);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        } else {
            updateUI();
        }
    }

    public void updateUI() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else {
            startActivityForResult(AuthUI.getInstance().
                    createSignInIntentBuilder().
                    setIsSmartLockEnabled(false, true).
                    setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build(), new AuthUI.IdpConfig.EmailBuilder().build())).
                    setLogo(R.mipmap.cloud_logo_with_txt).
                    setTheme(R.style.GreenTheme).build(), RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == Activity.RESULT_OK) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    showSnackbar(R.string.sign_in_cancelled);
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackbar(R.string.no_internet_connection);
                    getSupportFragmentManager().beginTransaction().replace(R.id.loginFramlayout, new OfflineFragment()).commit();
                    return;
                }

                showSnackbar(R.string.unknown_error);
                Log.e("TAG", "Sign-in error: ", response.getError());
            }
        }
    }

    public void showSnackbar(int msgCode) {
        String status = getResources().getString(msgCode);
        Snackbar snackbar = Snackbar.make(coordinatorLayout, status, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void restartapps(View view) {
        this.recreate();
    }
}
