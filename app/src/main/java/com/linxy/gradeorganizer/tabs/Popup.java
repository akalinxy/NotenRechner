package com.linxy.gradeorganizer.tabs;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.linxy.gradeorganizer.fragments.SubjectsFragment;
import com.linxy.gradeorganizer.R;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelper;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelperSubjects;

import java.util.ArrayList;

/**
 * Created by minel_000 on 28/7/2015.
 */
public class Popup extends Activity { // implements View.OnClickListener

    public static final String IDX = "id";
    public static final String NAMEX = "subjectname";
    public static final String FACTORX = "subjectfactor";

    private int ID;
    private String name;
    private int factor;

    TextView tvSName;
    TextView tvSFactor;

    Button btnSave;
    Button btnDelete;

    EditText etSFactor;
    DatabaseHelperSubjects myDB = new DatabaseHelperSubjects(this);
    DatabaseHelper db = new DatabaseHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_edit_subject);

        Intent intent = getIntent();
        ID = intent.getIntExtra(IDX, -1);
        name = intent.getStringExtra(NAMEX);
        factor = intent.getIntExtra(FACTORX, -1);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        tvSName =(TextView) findViewById(R.id.popup_title);
//        tvSFactor = (TextView) findViewById(R.id.popup_factor);

        btnSave = (Button) findViewById(R.id.popup_save);
//        btnDelete = (Button) findViewById(R.id.popup_delete);

//        btnSave.setOnClickListener(this);
//        btnDelete.setOnClickListener(this);

        etSFactor = (EditText) findViewById(R.id.popup_edit_factor);

        getWindow().setLayout((int)(width*.8),(int)(height*.5));
        //setInfo();
    }


//
//    private void setInfo(){
//        tvSName.setText(name);
//        tvSFactor.setText(String.valueOf(factor));
//
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId())
//        {
//            case R.id.popup_save:
//                if(etSFactor.getText().toString().equals("") || etSFactor.getText().toString() == null)
//                    break;
//                myDB.updateData(String.valueOf(ID), name, etSFactor.getText().toString());
//
//
//
//                break;
//            case R.id.popup_delete:
//
//                ArrayList<Integer> deleteList = new ArrayList<Integer>();
//                Cursor cursor = db.getAllData();
//                int i = 0;
//                while(cursor.moveToNext()){
//                    if(cursor.getString(1).equals(name)){
//                        deleteList.add(i, Integer.parseInt(cursor.getString(0)));
//                    }
//                }
//
//                for(int s = 0; s < deleteList.size(); s++){
//                    db.deleteData(String.valueOf(deleteList.get(s)));
//                }
//
//                myDB.deleteData(String.valueOf(ID));
//                cursor.close();
//                break;
//            default:
//                break;
//        }
//
////        Intent intent = new Intent(getBaseContext(), EditSubjectsActivity.class);
////        startActivity(intent);
////
//        this.finish();
//
//    }
//
//    @Override
//    public void onStop(){
//        super.onStop();
//        db.close();
//        myDB.close();
//    }

}
