package com.linxy.gradeorganizer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;

import com.linxy.gradeorganizer.com.linxy.adapters.GetGradeCallback;
import com.linxy.gradeorganizer.fragments.Tab2;

import org.apache.http.HttpConnection;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.util.ArrayList;

/**
 * Created by Linxy on 9/8/2015 at 19:20
 * Working on Grade Organizer in com.linxy.gradeorganizer
 */
public class ServerRequest  {
    ProgressDialog progressDialog;
    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    public static final String SERVER_ADRESS = "http://notenrechner.freeiz.com/";

    public ServerRequest(Context context){

    }

    public void storeGradeDataInBackground(Tab2.Grade grade, GetGradeCallback callback){
        new StoreGradeDataAsyncTask(grade, callback).execute();

    }

    public class StoreGradeDataAsyncTask extends AsyncTask<Void, Void, Void> {
        Tab2.Grade grade;
        GetGradeCallback gradeCallback;


        public StoreGradeDataAsyncTask(Tab2.Grade grade, GetGradeCallback callback){
            this.grade = grade;
            this.gradeCallback = gradeCallback;

        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataTosend = new ArrayList<>();
            dataTosend.add(new BasicNameValuePair("subjectname", grade.gradeName));
            dataTosend.add(new BasicNameValuePair("gradename", grade.gradeName));
            dataTosend.add(new BasicNameValuePair("grade", grade.grade));
            dataTosend.add(new BasicNameValuePair("gradefactor", grade.gradeFactor));
            dataTosend.add(new BasicNameValuePair("gradedate", grade.gradeDate));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADRESS + "Register.php");

            try{
                post.setEntity(new UrlEncodedFormEntity(dataTosend));
                client.execute(post);
            } catch (Exception e){
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
            //gradeCallback.done(null);
        }
    }

}
