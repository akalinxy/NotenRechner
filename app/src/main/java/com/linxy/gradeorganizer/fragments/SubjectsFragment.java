package com.linxy.gradeorganizer.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linxy.gradeorganizer.R;


/**
 * A simple {@link Fragment} subclass.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.linxy.gradeorganizer.com.linxy.adapters.RVAdapter;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelper;
import com.linxy.gradeorganizer.database_helpers.DatabaseHelperSubjects;

import java.util.ArrayList;
import java.util.List;


public class SubjectsFragment extends Fragment {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    DatabaseHelperSubjects myDB = new DatabaseHelperSubjects(getActivity());
    DatabaseHelper db = new DatabaseHelper(getActivity());
    Toolbar toolbar;

    String clickedname;
    String clickedID;

    private String m_text;

    private List<Subject> subjects;


    public class Subject {
        public String subjectid;
        public String subjectname;
        public String subjectfactor;

        Subject(String subjectid, String subjectname, String subjectfactor) {
            this.subjectid = subjectid;
            this.subjectname = subjectname;
            this.subjectfactor = subjectfactor;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_preference, container, false);

//        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        recyclerView.setHasFixedSize(true);
//
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//
//        recyclerView.setAdapter(adapter);

        myDB = new DatabaseHelperSubjects(getActivity());

        recyclerView = (RecyclerView)v.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);

        viewAll();
        return v;
    }


    public void viewAll() {

        Cursor res = myDB.getAllData();

        subjects = new ArrayList<>();



        final int selSubIds[] = new int[res.getCount()];
        final String selSubNames[] = new String[res.getCount()];
        final int selSubFactors[] = new int[res.getCount()];


        int i = 0;
        while (res.moveToNext()) {
            subjects.add(new Subject(res.getString(0), res.getString(1), res.getString(2)));
            selSubIds[i] = Integer.parseInt(res.getString(0));
            selSubNames[i] = res.getString(1);

            if (res.getString(2).equals("") || res.getString(2) == null) selSubFactors[i] = 0;
            else
                selSubFactors[i] = Integer.parseInt(res.getString(2));
            i++;
        }

        RVAdapter adapter;
        adapter = new RVAdapter(subjects);

//                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.list_view_items, arrayList);
        recyclerView.setAdapter(adapter);

//                myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//
//                        Intent intent = new Intent(getBaseContext(), Popup.class);
//                        intent.putExtra(Popup.IDX, selSubIds[position]);
//                        intent.putExtra(Popup.NAMEX, selSubNames[position]);
//                        intent.putExtra(Popup.FACTORX, selSubFactors[position]);
//                        startActivity(intent);
//                    }
//                });

        ((RVAdapter) adapter).setOnItemClickListener(new RVAdapter.MyClickListener() {

            @Override
            public void onItemClick(final int position, View view) {

                switch (view.getId()) {

                    case R.id.cv_subject_factor:


                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.factor);
                        // Set up the intput
                        final EditText input = new EditText(getActivity());
                        input.setInputType(InputType.TYPE_CLASS_NUMBER);
                        input.setMaxLines(1);
                        input.setText(String.valueOf(selSubFactors[position]));
                        input.append("");

                        input.setSelection(input.getText().length());
                        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});

                        builder.setView(input);

                        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                m_text = input.getText().toString();

                                if (!input.getText().toString().equals("") || input.getText().toString() == null) {
                                    myDB.updateData(String.valueOf(selSubIds[position]), selSubNames[position], input.getText().toString());
                                    dialog.cancel();
                                    viewAll();
                                }
                                else {

                                    showMessage(getResources().getString(R.string.error), getResources().getString(R.string.errorMsgWrongInputNumerical));
                                }


                            }
                        });
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();


                        break;
                    case R.id.cv_subject_delete:
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.delete)
                                .setMessage("Sind sie sicher dass sie loschen willn?")// REMEMBER TO ADD A STRING XML FOR THIS
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() // AD IN STRIGNS XML
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        ArrayList<Integer> deleteList = new ArrayList<Integer>();
                                        Cursor cursor = db.getAllData();
                                        int i = 0;
                                        while (cursor.moveToNext()) {
                                            if (cursor.getString(1).equals(selSubNames[position])) {
                                                deleteList.add(i, Integer.parseInt(cursor.getString(0)));
                                            }
                                        }

                                        for (int s = 0; s < deleteList.size(); s++) {
                                            db.deleteData(String.valueOf(deleteList.get(s)));
                                        }

                                        myDB.deleteData(String.valueOf(selSubIds[position]));
                                        cursor.close();
                                        viewAll();


                                    }
                                })
                                .setNegativeButton("Nain", null) // ADD IN STRINGS XML
                                .show();

                        break;


                }

            }
        });

        res.close();

    }

    @Override
    public void onResume() {
        super.onResume();

        viewAll();


    }

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof RelativeLayout.MarginLayoutParams) {
            RelativeLayout.MarginLayoutParams p = (RelativeLayout.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
    public void onStop() {
        super.onStop();
        myDB.close();
        myDB.close();

    }


}
