package facultyproject.facultyproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class Fragment1 extends Fragment {
    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> body = new ArrayList<>();
    ArrayList<String> date = new ArrayList<>();
    ArrayList<String> time = new ArrayList<>();
    ArrayList<String> column = new ArrayList<>();
    ArrayList<String> click = new ArrayList<>();
    ArrayList<Integer> server_id = new ArrayList<>();
    ArrayList<Integer> key_row_id = new ArrayList<>();
    ArrayList<Integer> deleted_id = new ArrayList<>();
    SwipeRefreshLayout refreshLayout;
    View rootview;
    ListView listView;
    ListViewAdapter adapter;
    Context context;
    Boolean counter = false;
    String tempcolumn;
    String tempserverid;
    public Fragment1(Context context) {
        this.context = context;
    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_fragment1, container, false);
        listView = (ListView) rootview.findViewById(R.id.listview_fragment);
        adapter = new ListViewAdapter(context, title, body, date, time, server_id, key_row_id, column,click);
        listView.setAdapter(adapter);
        saveInDatabase();
        new AddUser().execute();
        refreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.Swipe_refresh);
        try {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    long rowid=key_row_id.get(position);
                    ((MainActivity)getActivity()).db.open();
                    ((MainActivity)getActivity()).db.updateData(rowid);
                    click.set(position,"true");

                    Intent intent = new Intent(getActivity(), SingleItemView.class);
                    intent.putExtra("title", title);
                    intent.putExtra("body", body);
                    intent.putExtra("column", column);
                    intent.putExtra("position", position);
                    startActivity(intent);

                    adapter.notifyDataSetChanged();


                }
            });

            refreshLayout.setColorSchemeColors(getResources().getColor(R.color.login_background),
                    getResources().getColor(R.color.profile_start1),
                    getResources().getColor(R.color.profile_center1));
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!((MainActivity) getActivity()).isConnected()) {
                                Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_SHORT).show();
                                refreshLayout.setRefreshing(false);
                            } else {
                                saveInDatabase();
                                new AddUser().execute();
                            }
                        }
                    }, 100);
                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    long serverid = parent.getAdapter().getItemId(position);
                    counter=false;
                    alertDialog("Are you sure you want to delete", position, (int) serverid);
                    return true;
                }
            });

        } catch (Exception ignored) {
        }
        return rootview;
    }

    public void saveInDatabase() {
        try {
            ((MainActivity) getActivity()).db.open();
            Cursor c = ((MainActivity) getActivity()).db.getAllData();
            Cursor c2 = ((MainActivity) getActivity()).db.getAllDeletedData();
            if (c.moveToFirst()) {
                do {
                    if (!(server_id.contains(c.getInt(1)))) {
                        key_row_id.add(0, c.getInt(0));
                        server_id.add(0, c.getInt(1));
                        title.add(0, c.getString(2));
                        body.add(0, c.getString(3));
                        date.add(0, c.getString(4));
                        time.add(0, c.getString(5));
                        column.add(0, c.getString(6));
                        click.add(0, c.getString(7));
                    }
                } while (c.moveToNext());
            }
            if (c2.moveToFirst()) {
                do {
                    if (!(deleted_id.contains(c2.getInt(1)))) {
                        deleted_id.add(c2.getInt(1));
                    }
                } while (c2.moveToNext());
            }
            ((MainActivity) getActivity()).db.close();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    public void alertDialog(String mess, final int position, final int serverid) {
        final AlertDialog.Builder aBuilder = new AlertDialog.Builder(context);
        final CharSequence[] items = {"Delete from database"};
        aBuilder.setTitle(mess);

        aBuilder.setSingleChoiceItems(items, 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        counter = true;
                }
            }
        });

        aBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tempcolumn=column.get(position);
                tempserverid=String.valueOf(server_id.get(position));

                key_row_id.remove(position);
                server_id.remove(position);
                title.remove(position);
                body.remove(position);
                date.remove(position);
                time.remove(position);
                column.remove(position);
                click.remove(position);
                ((MainActivity) getActivity()).db.open();
                ((MainActivity) getActivity()).db.deleteData(serverid);
                ((MainActivity) getActivity()).db.insertDataDeleted(serverid);
                ((MainActivity) getActivity()).db.close();
                adapter.notifyDataSetChanged();
                if (counter) {
                    new DeleteData().execute();
                }
            }
        });
        aBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    class AddUser extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... voids) {
            return new RequestHandler().sendGetRequest(Config.URL_DOWNLOAD_INFO);
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
                        Integer server_id = jsonObject.getInt("id");
                        String title = jsonObject.getString("title");
                        String body = jsonObject.getString("body");
                        String date = jsonObject.getString("date");
                        String time = jsonObject.getString("time");
                        String column = jsonObject.getString("column");
                        String click = "false";
                        if (!(deleted_id.contains(server_id))) {
                            ((MainActivity) getActivity()).db.open();
                            ((MainActivity) getActivity()).db.insertData(server_id, title, body, date, time, column,click);
                            ((MainActivity) getActivity()).db.close();
                        }
                    }
                    saveInDatabase();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                new AddUser().execute();
            }
            refreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
        }
    }
    class DeleteData extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... voids) {
            HashMap<String, String> params = new HashMap<>();
            params.put(Config.KEY_COLUMN_NAME, tempcolumn);
            params.put(Config.KEY_SERVER_ID, tempserverid);
            RequestHandler rh = new RequestHandler();
            return rh.sendPostRequest(Config.URL_DELETE_SINGLE, params);

        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (!s.isEmpty()) {
                    Toast.makeText(context, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                } else {
                    new DeleteData().execute();
                }
            } catch (Exception ignored) {
            }
        }
    }
}