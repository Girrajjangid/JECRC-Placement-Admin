package facultyproject.facultyproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class ShareActivity extends AppCompatActivity {
    TextView title;
    FloatingActionButton fab_update;
    EditText titleET, messageET;
    String valid_title, valid_message;
    String time, date;
    ProgressDialog loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title_shareactivity);
        title = (TextView) findViewById(R.id.titleshareactivity);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/font1.ttf");
        title.setTypeface(custom_font);
        titleET = (EditText) findViewById(R.id.title);
        messageET = (EditText) findViewById(R.id.message);
        //fab_attach = (FloatingActionButton) findViewById(R.id.floatingActionButton_attachment);
        fab_update = (FloatingActionButton) findViewById(R.id.floatingActionButton_update);
        /*fab_attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog BottomSheetDialog = new Dialog(ShareActivity.this, R.style.MaterialDialogSheet);
                BottomSheetDialog.setContentView(R.layout.floating_action_dialog); // your custom view.
                BottomSheetDialog.setCancelable(true);
                BottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                BottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
                BottomSheetDialog.show();
            }
        });*/
        fab_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateConfirmation(v);
            }
        });
    }

    private void updateConfirmation(View view) {
        valid_title = titleET.getText().toString().trim();
        valid_message = messageET.getText().toString().trim();
        if (!isConnected()) {
            Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else if (valid_title.isEmpty()) {
            alertDialog("Please enter a Title");
        } else if (valid_message.isEmpty()) {
            alertDialog("Please enter a Message");
        } else {
            AlertDialog.Builder aBuilder = new AlertDialog.Builder(ShareActivity.this);
            aBuilder.setMessage("Are you sure you want to update this message");
            aBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    timeDate();
                    new AddUser().execute();
                    loading = ProgressDialog.show(ShareActivity.this, "Processing....", "Please Wait..", false, false);
                }
            });
            aBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).create().show();
        }
    }

    public void timeDate() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int minutes = c.get(Calendar.MINUTE);
        int a = c.get(Calendar.AM_PM);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        String AM_PM;
        if (a == Calendar.AM) {
            AM_PM = "AM";
        } else {
            AM_PM = "PM";
        }
        date = String.format("%02d", day) + "/" + String.format("%02d", month) + "/" + year;
        time = hour + ":" + String.format("%02d", minutes) + " " + AM_PM;
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected() && activeNetworkInfo.isAvailable();

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

    class AddUser extends AsyncTask<Void, Void, String> {

        String valid_column = valid_title.substring(0,2) + Integer.toString(new Random().nextInt((900 - 200) + 1) + 900)  ;

        protected String doInBackground(Void... voids) {
            HashMap<String, String> params = new HashMap<>();
            params.put(Config.KEY_TITLE, valid_title);
            params.put(Config.KEY_BODY, valid_message);
            params.put(Config.KEY_DATE, date);
            params.put(Config.KEY_TIME, time);
            params.put(Config.KEY_COLUMN_NAME, valid_column);
            RequestHandler rh = new RequestHandler();
            return rh.sendPostRequest(Config.URL_UPLOAD_INFO, params);

        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (!s.isEmpty()) {
                    loading.dismiss();
                    Toast.makeText(ShareActivity.this, "Successfully Update", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(ShareActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            finish();
                            startActivity(intent);
                        }
                    }, 1000);
                } else {
                    if (!isConnected()) {
                        Toast.makeText(ShareActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    } else {
                        new AddUser().execute();
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }
}