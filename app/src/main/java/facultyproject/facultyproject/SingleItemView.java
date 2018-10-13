package facultyproject.facultyproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class SingleItemView extends AppCompatActivity {

    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> body = new ArrayList<>();
    ArrayList<String> column = new ArrayList<>();
    int position;
    TextView bodyTV, titleTV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singleitemview);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title_singleviewactivity);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        bodyTV = (TextView) findViewById(R.id.title);
        titleTV = (TextView) findViewById(R.id.tvtitle);
        Intent intent = getIntent();
        title = intent.getStringArrayListExtra("title");
        body = intent.getStringArrayListExtra("body");
        column = intent.getStringArrayListExtra("column");

        position = intent.getExtras().getInt("position");
        bodyTV.setText(body.get(position));

        getSupportActionBar().setTitle(title.get(position));
    }

    public void interested(View view) {
    Intent intent=new Intent(SingleItemView.this,InterestedStudents.class);
        intent.putExtra("title", title.get(position));
        intent.putExtra("column", column.get(position));
        startActivity(intent);
    }
}


