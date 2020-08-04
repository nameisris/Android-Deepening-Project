package com.example.crawling;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class CGVAdapter extends RecyclerView.Adapter<CGVAdapter.ViewHolder> {
    Context context;
    ArrayList<HashMap<String, String>> arrayCGV;

    // 생성자
    // Context는 어디에서 만들어졌는지에 대해 구분하기 위함
    // MainActivity에서 CGVAdapter를 생성할 때 MainActivity.this와 arrayCGV를 매개변수로 주기 위한 생성자
    // MainActivity 내에 어댑터를 만들었다면 생성자가 필요없지만 외부에 만들었으므로
    // MainActivity 내에서 어댑터를 생성할 때 CGVAdapter라는 이름만으로 구분이 안되기에
    // Context를 주어서 MainActivity에서 생성되었다는것을 알려주는 것임
    public CGVAdapter(Context context, ArrayList<HashMap<String, String>> arrayCGV) {
        this.context = context;
        this.arrayCGV = arrayCGV;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cgv, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // holder는 뷰 항목의 순서를 구분하기 위함
        // position은 뷰 항목 안의 데이터의 순서를 구분하기 위함
        holder.txtRank.setText(arrayCGV.get(position).get("rank"));
        // arrayCGV의 해당 포지션의 "rank" 값을 현재 홀더의 txtRank의 텍스트로 set
        // (리사이어클 뷰가 여러 개의 항목을 가지므로 홀더로 특정 항목에 값을 넣을 수 있는 거임)
        holder.txtTitle.setText(arrayCGV.get(position).get("title"));
        Picasso.with(context).load(arrayCGV.get(position).get("image")).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return arrayCGV.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtRank, txtTitle;
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRank = itemView.findViewById(R.id.rank);
            txtTitle = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
        }
    }
}
