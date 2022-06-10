package com.locatocam.app.views.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.locatocam.app.R;
import com.locatocam.app.utils.ItemData;

import java.util.List;


public class ListDataAdapter extends RecyclerView.Adapter<ListDataAdapter.ViewHolder> {
    private List<ItemData> values;

    public List<ItemData> getValues() {
        return values;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tx_name;

        public ViewHolder(View v) {
            super(v);
            tx_name = (TextView) v.findViewById(R.id.label_dataname);

        }
    }

    public void add(int position, ItemData item) {
        values.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        values.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListDataAdapter(List<ItemData> myDataset) {
        values = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.row_layout_listdata, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if(!values.get(position).getId().equalsIgnoreCase("")){
            holder.tx_name.setText(values.get(position).getName());
            holder.tx_name.setTextColor(Color.parseColor("#181717"));

        }
        else {
            holder.tx_name.setText(values.get(position).getName());
            holder.tx_name.setTextColor(Color.RED);
        }

       // holder.tx_name.setText(values.get(position).getName());


        holder.tx_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context context = holder.tx_name.getContext();
                Intent data = new Intent();
                String text = values.get(position).getId();

                data.putExtra("result_id",text);
                data.putExtra("result_value",values.get(position).getName());

                if (((CustomSearchSpinner)context).row_number!=-1)
                {
                    data.putExtra("row_number", String.valueOf(((CustomSearchSpinner)context).row_number));
                }
                ((AppCompatActivity)context).setResult(((CustomSearchSpinner)context).req_code, data);
                ((AppCompatActivity)context).finish();

            }
        });
       }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }

}
