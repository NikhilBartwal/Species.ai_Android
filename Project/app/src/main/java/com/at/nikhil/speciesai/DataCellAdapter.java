package com.at.nikhil.speciesai;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class DataCellAdapter extends RecyclerView.Adapter<DataCellAdapter.MyViewHolder> {
    private ArrayList<DataCell> dataCells;
    ItemClicked activity;

    public interface ItemClicked{

        void onItemClicked(int index);
    }

    public DataCellAdapter(Context context, ArrayList<DataCell> datalist){

        dataCells = datalist;
        activity = (ItemClicked) context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView heading;
        TextView description;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            heading = itemView.findViewById(R.id.heading);
            description = itemView.findViewById(R.id.description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onItemClicked(dataCells.indexOf((DataCell) v.getTag()));
                }
            });
        }
    }

    @NonNull
    @Override
    public DataCellAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                               .inflate(R.layout.list_items,viewGroup,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DataCellAdapter.MyViewHolder myViewHolder, int i) {

        myViewHolder.itemView.setTag(dataCells.get(i));
        myViewHolder.heading.setText(dataCells.get(i).getHeading());
        myViewHolder.description.setText(dataCells.get(i).getDescription());
    }

    @Override
    public int getItemCount() {
        return dataCells.size();
    }
}
