package facultyproject.facultyproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class Login extends AppCompatActivity {
    EditText email, password;
    String valid_password, valid_email;
    SharedPreferences prefs;
    Animation anim_logo, anim_email_pass, anim_login,anim_version;
    ImageView anim1;
    LinearLayout anim3;
    RelativeLayout anim2;
    TextView anim0;
    public static final String preference = "UserData";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        email = (EditText) findViewById(R.id.login_email);
        password = (EditText) findViewById(R.id.login_password);

        anim0 = (TextView) findViewById(R.id.version);
        anim1 = (ImageView) findViewById(R.id.logo);
        anim3 = (LinearLayout) findViewById(R.id.linear_layout1);
        anim2 = (RelativeLayout) findViewById(R.id.relative_layout_1);

        anim_version = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation0);
        anim_logo = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation1);
        anim_email_pass = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation2);
        anim_login = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation3);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                anim2.setVisibility(View.VISIBLE);
                anim3.setVisibility(View.VISIBLE);
                anim0.startAnimation(anim_version);
                anim1.startAnimation(anim_logo);
                anim2.startAnimation(anim_email_pass);
                anim3.startAnimation(anim_login);
            }
        }, 2000);
    }

    protected void onResume() {
        prefs = getSharedPreferences(preference, Context.MODE_PRIVATE);
        if (prefs.contains("email")) {
            if (prefs.contains("password")) {
                Intent i = new Intent(this, MainActivity.class);
                finish();
                startActivity(i);
            }
        }
        super.onResume();
    }

    public void logIn(View view) {
        valid_email = email.getText().toString().trim();
        valid_password = password.getText().toString().trim();
        if (!isConnected()) {
            Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else if (valid_email.isEmpty() || !isEmailValid(valid_email)) {
            alertDialog("Invalid Email Address");
        } else if (valid_password.isEmpty() || valid_password.length() < 5) {
            password.setError("at least 6 character");
            alertDialog("Invalid Password");
        } else {
            class AddUser extends AsyncTask<Void, Void, String> {
                private ProgressDialog loading;

                @Override
                protected String doInBackground(Void... voids) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put(Config.KEY_USER_EMAIL, valid_email);
                    params.put(Config.KEY_USER_PASSWORD, valid_password);

                    RequestHandler rh = new RequestHandler();
                    return rh.sendPostRequest(Config.URL_LOGIN, params);

                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    loading = ProgressDialog.show(Login.this, "Processing...", "Please Wait...", false, false);
                }

                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    loading.dismiss();
                    if (!s.isEmpty()) {
                        try {
                            JSONObject obj = new JSONObject(s);
                            JSONArray result = obj.getJSONArray(Config.TAG_JSON_ARRAY);
                            JSONObject c = result.getJSONObject(0);
                            final String email = c.getString(Config.TAG_EMAIL);
                            final String password = c.getString(Config.TAG_PASSWORD);
                            // if (password != "null") {
                            if (!password.equalsIgnoreCase(null)) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        prefs = getSharedPreferences(preference, MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putString("email", email);
                                        editor.putString("password", password);
                                        editor.apply();
                                        finish();
                                        Intent i = new Intent(Login.this, MainActivity.class);
                                        startActivity(i);
                                    }
                                }, 1000);
                            } else {
                                alertDialog("Wrong Email Address or Password");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        new AddUser().execute();
                        Toast.makeText(Login.this, "Slow Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }
             new AddUser().execute();
        }
    }

    private void alertDialog(String mess) {
        final AlertDialog.Builder aBuilder = new AlertDialog.Builder(this);
        aBuilder.setMessage(mess);
        aBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).create().show();
    }

    boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected() && activeNetworkInfo.isAvailable();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}