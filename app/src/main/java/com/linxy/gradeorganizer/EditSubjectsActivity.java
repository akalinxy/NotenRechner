package com.linxy.gradeorganizer;

import android.app.AlertDialog;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.linxy.gradeorganizer.database_helpers.DatabaseHelperSubjects;

import java.util.ArrayList;


public class EditSubjectsActivity extends ActionBarActivity {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    Button testButton;
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
        testButton = (Button) findViewById(R.id.get_data_Temp);

        viewAll();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_subjects, menu);
        System.out.print("ok");
        return true;
    }

    public void viewAll(){
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor res = myDB.getAllData();
                if (res.getCount() == 0) {
                    // Show message
                    showMessage("Error:" , "No Data!");
                    res.close();
                    return;
                }

                StringBuffer buffer = new StringBuffer();
                while (res.moveToNext()) {
                    buffer.append("ID :" + res.getString(0) + "\n");
                    buffer.append("SubjectName :" + res.getString(1) + "\n");
                    buffer.append("SubjectFactor :" + res.getInt(2) + "\n\n");

                }

                showMessage("Data", buffer.toString());
                res.close();
            }
        });
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
        myDB.close();

      }
}
