package com.example.naver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class BookDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        getSupportActionBar().setTitle("도서내용"); // 액션바 title 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 백 버튼 생성

        Intent intent = getIntent();
        String stitle = intent.getStringExtra("title");
        String sauthor = intent.getStringExtra("author");
        String sprice = intent.getStringExtra("price");
        String sdescription = intent.getStringExtra("description");
        String simage = intent.getStringExtra("image");
        String spubdate = intent.getStringExtra("pubdate");
        String spublisher = intent.getStringExtra("publisher");

        TextView title = findViewById(R.id.title);
        TextView author = findViewById(R.id.author);
        TextView price = findViewById(R.id.price);
        TextView description = findViewById(R.id.description);
        ImageView image = findViewById(R.id.image);
        TextView pubdate = findViewById(R.id.pubdate);
        TextView publisher = findViewById(R.id.publisher);

        title.setText(Html.fromHtml(stitle));
        author.setText("저자 : " + Html.fromHtml(sauthor));
        price.setText("가격 : " + Html.fromHtml(sprice));
        description.setText(Html.fromHtml(sdescription));
        pubdate.setText("출판일 : " + Html.fromHtml(spubdate));
        publisher.setText("출판사 : " + Html.fromHtml(spublisher));

        // 책 표지 설정/?
        Picasso.with(BookDetailActivity.this).load(simage).into(image);
    }

    // 옵션 선택 이벤트
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) // 백 버튼일 경우
            finish(); // 현재 액티비티 끝내기 (이전 액티비티로 돌아감)
        return super.onOptionsItemSelected(item);
    }
}