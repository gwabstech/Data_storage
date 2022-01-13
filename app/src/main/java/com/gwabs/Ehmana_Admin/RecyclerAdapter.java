package com.gwabs.Ehmana_admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder>{
    Context mContext;
    ArrayList<String> FileName,FileUrl;
    titleClickListener listener;

    public RecyclerAdapter(Context mContext, ArrayList<String> fileName, ArrayList<String> fileUrl, titleClickListener listener) {
        this.mContext = mContext;
        FileName = fileName;
        FileUrl = fileUrl;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.fileviewholder,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.FileName.setText(FileName.get(position).toString());
        listener.onItemClick(holder.itemView,position);

    }

    @Override
    public int getItemCount() {
        return FileUrl.size();
    }
}

class ViewHolder  extends RecyclerView.ViewHolder{

    TextView FileName;
    ImageView Download;
    ImageView Delete;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        FileName = itemView.findViewById(R.id.FileName);
        Delete = itemView.findViewById(R.id.imgDelete);

    }
}
