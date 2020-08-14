package com.example.chatprogram;



import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    Context context; // 어디에서 넘겨주는지
    ArrayList<Message> arrayMessage;
    String strEmail; // 이메일 값 비교를 위해

    // 생성자
    public ChatAdapter(Context context, ArrayList<Message> arrayMessage, String strEmail) {
        this.context = context;
        this.arrayMessage = arrayMessage;
        this.strEmail = strEmail;
    }


    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        // LayoutParams 형으로 형변환하여 사용 (레이아웃의 뷰가 배치되는 속성을 정해주기 위해)
        LinearLayout.LayoutParams prmContent = (LinearLayout.LayoutParams)holder.txtContent.getLayoutParams();
        LinearLayout.LayoutParams prmDate = (LinearLayout.LayoutParams)holder.txtDate.getLayoutParams();
        LinearLayout.LayoutParams prmEmail = (LinearLayout.LayoutParams)holder.txtName.getLayoutParams();
        String email = arrayMessage.get(position).getName();
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
            holder.txtName.setVisibility(View.GONE); // txtEmail 출력을 사라지게 함 (본인의 이메일을 없어지게 함)
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
        holder.txtContent.setText(arrayMessage.get(position).getContent()); // 내용 설정
        holder.txtDate.setText(arrayMessage.get(position).getWdate()); // 날짜 설정
        holder.txtName.setText(arrayMessage.get(position).getName()); // 이름 설정
    }

    @Override
    public int getItemCount() {
        return arrayMessage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtContent, txtDate, txtName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtContent = itemView.findViewById(R.id.txtContent);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtName = itemView.findViewById(R.id.txtName);
        }
    }
}
