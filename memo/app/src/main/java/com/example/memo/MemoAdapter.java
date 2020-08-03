package com.example.memo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.ViewHolder> {
    ArrayList<Memo> arrayMemo = new ArrayList<Memo>(); // HashMap<> 대신 데이터가 들어있는 Memo를 사용
    Context context;

    // MemoAdapter 생성자
    public MemoAdapter(ArrayList<Memo> arrayMemo, Context context) {
        this.arrayMemo = arrayMemo;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) { // arrayMemo에서 position번째 값 가져오기
        final Memo memo = arrayMemo.get(position);
        holder.txtContent.setText(memo.getContent());
        holder.txtDate.setText(memo.getCreateDate());
        // 리사이클 뷰의 메모 항목들에 대한 클릭 리스너
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ReadActivity.class);
                intent.putExtra("key", memo.getKey()); // memo의 키 값을 가져와 intent에 "key"라는 이름으로 넣어줌
                intent.putExtra("createDate", memo.getCreateDate());
                intent.putExtra("updateDate", memo.getUpdateDate());
                intent.putExtra("content", memo.getContent());
                ((Activity)context).startActivityForResult(intent, 2); // context를 Activity로 형변환
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayMemo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder { // 아이디 값 가져오기
        TextView txtContent, txtDate;
        LinearLayout item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtContent = itemView.findViewById(R.id.txtContent);
            txtDate = itemView.findViewById(R.id.txtDate);
            // item 클릭 리스너를 위한 아이디 읽어오기
            item = itemView.findViewById(R.id.item);
        }
    }
}
