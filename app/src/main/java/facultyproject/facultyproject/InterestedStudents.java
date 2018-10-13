package facultyproject.facultyproject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Locale;

import jxl.CellView;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import static jxl.Workbook.createWorkbook;

public class InterestedStudents extends AppCompatActivity {
    ArrayList<String> name =new ArrayList<>();
    ArrayList<String> email =new ArrayList<>();
    ArrayList<String> contact =new ArrayList<>();
    ArrayList<String> rollno =new ArrayList<>();
    ArrayList<String> father =new ArrayList<>();
    ArrayList<String> mother =new ArrayList<>();
    ArrayList<String> dob =new ArrayList<>();
    ArrayList<String> address =new ArrayList<>();
    ArrayList<String> city =new ArrayList<>();
    ArrayList<String> state =new ArrayList<>();
    ArrayList<String> altercontact =new ArrayList<>();
    ArrayList<String> tenschoolname =new ArrayList<>();
    ArrayList<String> tenpercentage =new ArrayList<>();
    ArrayList<String> twelveschoolname =new ArrayList<>();
    ArrayList<String> twelvepercentage =new ArrayList<>();
    ArrayList<String> gpa1 =new ArrayList<>();
    ArrayList<String> gpa2 =new ArrayList<>();
    ArrayList<String> gpa3 =new ArrayList<>();
    ArrayList<String> gpa4 =new ArrayList<>();
    ArrayList<String> gpa5 =new ArrayList<>();
    ArrayList<String> gpa6 =new ArrayList<>();
    ArrayList<String> gpa7 =new ArrayList<>();
    ArrayList<String> gpa8 =new ArrayList<>();
    ArrayList<String> cgpa =new ArrayList<>();
    ArrayList<String> diploma1 =new ArrayList<>();
    ArrayList<String> diploma2 =new ArrayList<>();
    ArrayList<String> diploma3 =new ArrayList<>();
    ArrayList<String> project1 =new ArrayList<>();
    ArrayList<String> project2 =new ArrayList<>();
    ArrayList<String> moa =new ArrayList<>();
    ArrayList<String> fathercontact =new ArrayList<>();
    ArrayList<String> gender =new ArrayList<>();
    ArrayList<String> branch =new ArrayList<>();
    ArrayList<String> tenpassoutyear =new ArrayList<>();
    ArrayList<String> twelvepassoutyear =new ArrayList<>();
    ArrayList<String> totalback =new ArrayList<>();
    ArrayList<String> yearback =new ArrayList<>();
    String firstrow[] = {"S.No","Mode of Admission","Name","Registration No.","Email","Contact","Date of Birth",
            "Gender", "AlterContact", "Father's Name","Father's Contact","Mother's Name","Address","City","State",
            "10th School Name","10th Percentage","10th Pass out year","12th School Name","12th Percentage",
            "12th Pass out year","Diploma 1","Diploma 2","Diploma 3","Branch","GPA 1", "GPA 2","GPA 3","GPA 4","GPA 5",
            "GPA 6","GPA 7","GPA 8","CGPA","Overall Backs","Year Gap","Name of Project 1","Name of Project 2"};
    String title;
    String column;
    ListView list;
    ArrayAdapter<String> adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    final private int REQUEST_WRITE_EXTERNAL_STORAGE=123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interested_students);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_interested);
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(InterestedStudents.this, "Swipe Down to refresh list", Toast.LENGTH_SHORT).show();
            }
        },1000);
        list = (ListView) findViewById(R.id.listview_interestedStudent);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swiperefresh_interested);

        adapter= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, name);
        list.setAdapter(adapter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title_singleviewactivity);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);
        Intent intent = getIntent();
        title = intent.getExtras().getString("title");
        column = intent.getExtras().getString("column");
        getSupportActionBar().setTitle(title);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.login_background),
                getResources().getColor(R.color.profile_start1),
                getResources().getColor(R.color.profile_center1));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getStudent();
            }
        });
    }
    private void getStudent() {
        if (!isConnected()) {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        } else {
            new GetStudent().execute();
        }
    }
    class GetStudent extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... voids) {
            HashMap<String, String> params = new HashMap<>();
            params.put(Config.KEY_COLUMN_NAME, column);
            RequestHandler rh = new RequestHandler();
            return rh.sendPostRequest(Config.URL_GETSTUDENTS, params);
        }
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!s.isEmpty()) {
                try {
                    JSONObject obj = new JSONObject(s);
                    JSONArray jsonArray = obj.getJSONArray(Config.TAG_JSON_ARRAY);
                    Log.i("JSON", "Number of Surveys in Feed: " + jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (!(rollno.contains(jsonObject.getString(Config.KEY_USER_ROLLNO)))) {
                            name.add(jsonObject.getString(Config.KEY_USER_NAME));
                            email.add(jsonObject.getString(Config.KEY_USER_EMAIL));
                            contact.add(jsonObject.getString(Config.KEY_USER_CONTACT));
                            rollno.add(jsonObject.getString(Config.KEY_USER_ROLLNO));
                            father.add(jsonObject.getString(Config.KEY_USER_FATHER));
                            mother.add(jsonObject.getString(Config.KEY_USER_MOTHER));
                            dob.add(jsonObject.getString(Config.KEY_USER_DOB));
                            address.add(jsonObject.getString(Config.KEY_USER_ADDRESS));
                            city.add(jsonObject.getString(Config.KEY_USER_CITY));
                            state.add(jsonObject.getString(Config.KEY_USER_STATE));
                            altercontact.add(jsonObject.getString(Config.KEY_USER_ALTERCONTACT));
                            tenschoolname.add(jsonObject.getString(Config.KEY_USER_TENSN));
                            tenpercentage.add(jsonObject.getString(Config.KEY_USER_TENPER));
                            twelveschoolname.add(jsonObject.getString(Config.KEY_USER_TWELVESN));
                            twelvepercentage.add(jsonObject.getString(Config.KEY_USER_TWELVEPER));
                            gpa1.add(jsonObject.getString(Config.KEY_USER_GPA_1));
                            gpa2.add(jsonObject.getString(Config.KEY_USER_GPA_2));
                            gpa3.add(jsonObject.getString(Config.KEY_USER_GPA_3));
                            gpa4.add(jsonObject.getString(Config.KEY_USER_GPA_4));
                            gpa5.add(jsonObject.getString(Config.KEY_USER_GPA_5));
                            gpa6.add(jsonObject.getString(Config.KEY_USER_GPA_6));
                            gpa7.add(jsonObject.getString(Config.KEY_USER_GPA_7));
                            gpa8.add(jsonObject.getString(Config.KEY_USER_GPA_8));
                            cgpa.add(jsonObject.getString(Config.KEY_USER_CGPA));
                            diploma1.add(jsonObject.getString(Config.KEY_USER_DIPLOMA_1));
                            diploma2.add(jsonObject.getString(Config.KEY_USER_DIPLOMA_2));
                            diploma3.add(jsonObject.getString(Config.KEY_USER_DIPLOMA_3));
                            project1.add(jsonObject.getString(Config.KEY_USER_PROJECT_1));
                            project2.add(jsonObject.getString(Config.KEY_USER_PROJECT_2));
                            fathercontact.add(jsonObject.getString(Config.KEY_USER_FATHER_CONTACT));
                            gender.add(jsonObject.getString(Config.KEY_USER_GENDER));
                            branch.add(jsonObject.getString(Config.KEY_USER_BRANCH));
                            tenpassoutyear.add(jsonObject.getString(Config.KEY_USER_TENPASS));
                            twelvepassoutyear.add(jsonObject.getString(Config.KEY_USER_TWELVEPASS));
                            totalback.add(jsonObject.getString(Config.KEY_USER_TOTAL_BACK));
                            yearback.add(jsonObject.getString(Config.KEY_USER_YEAR_BACK));
                            moa.add(jsonObject.getString(Config.KEY_USER_MOA));
                        }
                    }
                    Toast.makeText(getBaseContext(), "List Updated", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                new GetStudent().execute();

            }
            swipeRefreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_interested_student, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.downloadxml) {
            downloadXML();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case REQUEST_WRITE_EXTERNAL_STORAGE:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {Toast.makeText(InterestedStudents.this,"Permission Granted",Toast.LENGTH_SHORT).show();}
                else{
                    Toast.makeText(InterestedStudents.this,"Permission denied",Toast.LENGTH_SHORT).show();}break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void downloadXML() {
        String fileName = title + ".xlsx";
        String location = "/JuPlacement";
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() + location);
        File file = new File(directory, fileName);
        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));
        try {
            WritableWorkbook workbook = createWorkbook(file, wbSettings);
            WritableSheet sheet = workbook.createSheet("First Sheet", 0);

            WritableFont wfont = new WritableFont(WritableFont.createFont("font"), 12, WritableFont.BOLD, false,
                    UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);
            WritableCellFormat cellformat = new WritableCellFormat(wfont);
            cellformat.setAlignment(Alignment.CENTRE);
            cellformat.setVerticalAlignment(VerticalAlignment.CENTRE);
            CellView cell;

            for (int i = 0; i < firstrow.length; i++) {
                cell = sheet.getColumnView(i);
                cell.setAutosize(true);
                sheet.setColumnView(i, cell);
                sheet.addCell(new Label(i, 0, firstrow[i], cellformat));
            }
            for(int i=1;i<=rollno.size();i++) {
                sheet.addCell(new Label(0, i, String.valueOf(i)));
                sheet.addCell(new Label(1, i, moa.get(i-1)));
                sheet.addCell(new Label(2, i, name.get(i-1)));
                sheet.addCell(new Label(3, i, rollno.get(i-1)));
                sheet.addCell(new Label(4, i, email.get(i-1)));
                sheet.addCell(new Label(5, i, contact.get(i-1)));
                sheet.addCell(new Label(6, i, dob.get(i-1)));
                sheet.addCell(new Label(7, i, gender.get(i-1)));
                sheet.addCell(new Label(8, i, altercontact.get(i-1)));
                sheet.addCell(new Label(9, i, father.get(i-1)));
                sheet.addCell(new Label(10, i, fathercontact.get(i-1)));
                sheet.addCell(new Label(11, i, mother.get(i-1)));
                sheet.addCell(new Label(12, i, address.get(i-1)));
                sheet.addCell(new Label(13, i, city.get(i-1)));
                sheet.addCell(new Label(14, i, state.get(i-1)));
                sheet.addCell(new Label(15, i, tenschoolname.get(i-1)));
                sheet.addCell(new Label(16, i, tenpercentage.get(i-1)));
                sheet.addCell(new Label(17, i, tenpassoutyear.get(i-1)));
                sheet.addCell(new Label(18, i, twelveschoolname.get(i-1)));
                sheet.addCell(new Label(19, i, twelvepercentage.get(i-1)));
                sheet.addCell(new Label(20, i, twelvepassoutyear.get(i-1)));
                sheet.addCell(new Label(21, i, diploma1.get(i-1)));
                sheet.addCell(new Label(22, i, diploma2.get(i-1)));
                sheet.addCell(new Label(23, i, diploma3.get(i-1)));
                sheet.addCell(new Label(24, i, branch.get(i-1)));
                sheet.addCell(new Label(25, i, gpa1.get(i-1)));
                sheet.addCell(new Label(26, i, gpa2.get(i-1)));
                sheet.addCell(new Label(27, i, gpa3.get(i-1)));
                sheet.addCell(new Label(28, i, gpa4.get(i-1)));
                sheet.addCell(new Label(29, i, gpa5.get(i-1)));
                sheet.addCell(new Label(30, i, gpa6.get(i-1)));
                sheet.addCell(new Label(31, i, gpa7.get(i-1)));
                sheet.addCell(new Label(32, i, gpa8.get(i-1)));
                sheet.addCell(new Label(33, i, cgpa.get(i-1)));
                sheet.addCell(new Label(34, i, totalback.get(i-1)));
                sheet.addCell(new Label(35, i, yearback.get(i-1)));
                sheet.addCell(new Label(36, i, project1.get(i-1)));
                sheet.addCell(new Label(37, i, project2.get(i-1)));
            }
            workbook.write();
            workbook.close();
            Toast.makeText(this, "Download Successfully", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Location : SDCard"+location+"/"+fileName, Toast.LENGTH_LONG).show();
        } catch (IOException | WriteException e) {
            e.printStackTrace();
        }
    }
    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected() && activeNetworkInfo.isAvailable();

    }
}
