package com.example.litingting.navigationfragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.imagecut.MyImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

//        findViewById(R.id.simple_five_tabs).setOnClickListener(this);

        setContentView(R.layout.image_cut_layout);



        MyImageView myImageView=(MyImageView)findViewById(R.id.imageview);

    }

    @Override
    public void onClick(View v){
        Class clazz = null;

        switch (v.getId()){
            case R.id.simple_five_tabs:
                clazz = FiveTabsActivity.class;
                break;
        }
        startActivity(new Intent(this,clazz));
    }

}
