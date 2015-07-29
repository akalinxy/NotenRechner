package com.linxy.gradeorganizer;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.linxy.gradeorganizer.database_helpers.DatabaseHelperSubjects;
import com.linxy.gradeorganizer.tabs.Popup;

import java.util.ArrayList;
import java.util.List;


public class EditSubjectsActivity extends ActionBarActivity {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    DatabaseHelperSubjects myDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_subjects);

//        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        recyclerView.setHasFixedSize(true);
//
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//
//        recyclerView.setAdapter(adapter);



        myDB = new DatabaseHelperSubjects(this);
        viewAll();
       // populateListView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_subjects, menu);
        System.out.print("ok");
        System.out.print("make");
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        viewAll();
    }

    public void viewAll(){

                Cursor res = myDB.getAllData();
//                if (res.getCount() == 0) {
//                    // Show message
//                    res.close();
//                    return;
//                }

                ArrayList<String> arrayList = new ArrayList<String>();
                final ListView myList = (ListView) findViewById(R.id.edit_subjects_list_view);

//        if(res.getCount() == 0) return;


        final int selSubIds[] = new int[res.getCount()];
                final String selSubNames[] = new String[res.getCount()];
                final int selSubFactors[] = new int[res.getCount()];


            int i = 0;
            while (res.moveToNext()) {
                arrayList.add(res.getString(1));
                selSubIds[i] = Integer.parseInt(res.getString(0));
                selSubNames[i] = res.getString(1);

                if(res.getString(2).equals("") || res.getString(2) == null) selSubFactors[i] = 0;
                else
                selSubFactors[i] = Integer.parseInt(res.getString(2));
                i++;
            }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.list_view_items, arrayList);
                myList.setAdapter(arrayAdapter);
                myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                        Intent intent = new Intent(getBaseContext(), Popup.class);
                        intent.putExtra(Popup.IDX, selSubIds[position]);
                        intent.putExtra(Popup.NAMEX, selSubNames[position]);
                        intent.putExtra(Popup.FACTORX, selSubFactors[position]);
                        startActivity(intent);
                    }
                });
                res.close();

    }

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop(){
        super.onStop();
        myDB.close();

      }


}
