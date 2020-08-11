package com.example.chat;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    Context context; // 어디에서 넘겨주는지
    ArrayList<Chat> arrayChat;
    String strEmail; // 이메일 값 비교를 위해

    // 생성자
    public ChatAdapter(Context context, ArrayList<Chat> arrayChat, String strEmail) {
        this.context = context;
        this.arrayChat = arrayChat;
        this.strEmail = strEmail;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) { // position에 해당하는 데이터를 뷰홀더의 아이템 뷰에 표시 (각 위치에 맞는 데이터의 값을)
        // LayoutParams 형으로 형변환하여 사용 (레이아웃의 뷰가 배치되는 속성을 정해주기 위해)
        LinearLayout.LayoutParams prmContent = (LinearLayout.LayoutParams)holder.txtContent.getLayoutParams();
        LinearLayout.LayoutParams prmDate = (LinearLayout.LayoutParams)holder.txtDate.getLayoutParams();
        LinearLayout.LayoutParams prmEmail = (LinearLayout.LayoutParams)holder.txtEmail.getLayoutParams();
        String email = arrayChat.get(position).getEmail();
        // arrayChat으로부터 받아온 이메일 데이터 (모든 채팅내용이 들어간 chat의 데이터가 들어있는 arrayChat)

        // 아래의 조건문에 따라 TextView의 TextColor와 Gravity를 설정
        // (채팅을 한 유저에 따라 채팅창의 위치와 글자색을 설정)
        if (strEmail.equals(email)) {
            // 현재 유저의 이메일이 들은 strEmail과
            // 전체 채팅의 모든 유저의 이메일이 들은 email의
            // 값을 비교했을때 같은 값이라면
            holder.txtContent.setTextColor(Color.RED); // txtContent 글자의 색을 RED로 설정
            prmContent.gravity = Gravity.RIGHT; // 레이아웃파라미터 형으로 형변환한 채팅내용인 prmContent의 레이아웃 위치를 오른쪽으로 위치하게 함
            prmDate.gravity = Gravity.RIGHT;
            prmEmail.gravity = Gravity.RIGHT;
            holder.txtEmail.setVisibility(View.GONE); // txtEmail 출력을 사라지게 함 (본인의 이메일을 없어지게 함)
        }
        else { // 그 이외의 경우
            holder.txtContent.setTextColor(Color.BLUE); // txtContent 글자의 색을 BLUE로 설정
            prmContent.gravity = Gravity.LEFT;
            prmDate.gravity = Gravity.LEFT;
            prmEmail.gravity = Gravity.LEFT;
        }

        // 위의 조건문에 따라 설정된 속성 값으로 (TextColor, Gravity)
        // 리사이클러 뷰에 출력될
        // 내용, 날짜, 이메일의 텍스트 값을 설정
        holder.txtContent.setText(arrayChat.get(position).getContent()); // 내용 설정
        holder.txtDate.setText(arrayChat.get(position).getWdate()); // 날짜 설정
        holder.txtEmail.setText(arrayChat.get(position).getEmail()); // 이메일 설정
    }

    @Override
    public int getItemCount() { // 전체 아이템 갯수 리턴
        return arrayChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder { // 아이디 값 읽어들이기
        TextView txtContent, txtDate, txtEmail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtContent = itemView.findViewById(R.id.txtContent);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtEmail = itemView.findViewById(R.id.txtEmail);
        }
    }
}
