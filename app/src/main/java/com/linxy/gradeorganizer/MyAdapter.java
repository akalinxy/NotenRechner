package com.linxy.gradeorganizer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Linxy on 4/8/2015 at 15:33
 * Working on Grade Organizer in com.linxy.gradeorganizer
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private String mNavTitles[];
    private int mIcons[];

    private String name;
    private int profile;
    private String email;
    Context context;

    // Create a ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        int Holderid;

        TextView textView;
        ImageView imageView;
      //  ImageView profile;
        TextView Name;
        TextView email;
        Context contxt;

        public ViewHolder(View itemView, int ViewType, Context c){
            super(itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            contxt = c;
            if(ViewType == TYPE_ITEM){
                textView = (TextView) itemView.findViewById(R.id.rowText);
                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
                Holderid = 1;
            } else {
                Name = (TextView) itemView.findViewById(R.id.header_name);
                email = (TextView) itemView.findViewById(R.id.header_email);
               // profile = (ImageView) itemView.findViewById(R.id.header_circleView);
                Holderid = 0;
            }
        }

        @Override
        public void onClick(View view){

        }

    }

    /* MyAdapter Constructor */
    MyAdapter(String Titles[], int Icons[], String Name, String Email, Context passedContext){
        mNavTitles = Titles;
        mIcons = Icons;
        name = Name;
        email = Email;
        this.context = passedContext;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if(viewType == TYPE_ITEM){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false); /* Inflating Layout */
            ViewHolder vhItem = new ViewHolder(v, viewType, context);
            return  vhItem; /* Return the created object and inflate */
        } else if (viewType == TYPE_HEADER) {
            View v =  LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false); /* Inflating Layout */
            ViewHolder vhHeader  = new ViewHolder(v, viewType, context);
            return vhHeader; /* Return  the created object */
        }
        return  null;
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position){
        if(holder.Holderid == 1){
            holder.textView.setText(mNavTitles[position - 1]);
            holder.imageView.setImageResource(mIcons[position-1]);
        } else {
         //  holder.profile.setImageResource(profile);
            holder.Name.setText(name);
            holder.email.setText(email);
        }
    }

    /* This Method returns the amount of items present in the list */
    @Override
    public int getItemCount(){
        return mNavTitles.length +1;
    }

    @Override
    public int getItemViewType(int position){
        if(isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position){
        return position == 0; /* returns true if position is0 */
    }


}
