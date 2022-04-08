package com.kugonza.apps.jobapp;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class JobAdapter extends RecyclerView.Adapter<JobAdapter.TipViewHolder> implements Filterable {

    private Context mCtx;
    String status;
    private int doll;
    List<Joblist> mData;
    List<Joblist> mDataFiltered;

    public JobAdapter(Context mCtx, List<Joblist> mData) {
        this.mCtx = mCtx;
        this.mData = mData;
        this.mDataFiltered = mData;
    }

    @Override
    public TipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.row_jobs, parent,false);
//        RecyclerView.LayoutParams layoutParams=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        view.setLayoutParams(layoutParams);

        return new TipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TipViewHolder holder, final int position) {
        final Joblist job = mDataFiltered.get(position);
       holder.title.setText(""+job.getTitle());
       holder.details.setText(""+job.getDetails());
       holder.uploadDate.setText(""+job.getDate());
        Picasso.with(mCtx)
                .load(job.getImage())
                .placeholder(R.drawable.img_kin)
                .error(R.drawable.img_kin)
                .into(holder.imageView ,new Callback(){
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError() {

                    }
                });
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle= new Bundle();
                bundle.putString("key",job.getId());
                mCtx.startActivity(new Intent(mCtx,JobDetailsActivity.class).putExtras(bundle));
            }
        });
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return mDataFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return myFilterData;
    }


    private Filter myFilterData = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String key=constraint.toString();
            if (key.isEmpty()){
                mDataFiltered=mData;
            }
            else{
                List<Joblist> FilteredList=new ArrayList<>();
                for (Joblist row: mData){
                    if (row.getDetails().toString().contains(key) || String.valueOf(row.getRequirements()).contains(key) || row.getTitle().toUpperCase().contains(key)|| row.getRequirements().toLowerCase().contains(key) || String.valueOf(row.getUser_id()).contains(key)){
                        FilteredList.add(row);
                    }
                }

                mDataFiltered=FilteredList;
            }
            FilterResults  filterResults=new FilterResults();
            filterResults.values=mDataFiltered;
            return filterResults;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            mDataFiltered=(List<Joblist>)results.values;
//            mData.clear();
//            mData.addAll((Collection<? extends Home_Objects>) results.values);
            notifyDataSetChanged();
        }
    };
    class TipViewHolder extends RecyclerView.ViewHolder {
        private TextView title,details,uploadDate;
        private ImageView imageView;
        private LinearLayout container;

        public TipViewHolder(View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.jobTitle);
            details=itemView.findViewById(R.id.jobDetails);
            uploadDate=itemView.findViewById(R.id.uploadDate);
            imageView=itemView.findViewById(R.id.jobImage);
            container=itemView.findViewById(R.id.smartContainer);



        }
    }



}