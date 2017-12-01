package com.example.animallove;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.animallove.Adapter.RecyclerAdapter;
import com.example.animallove.Classes.Recycler_item;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        List<Recycler_item> items=new ArrayList<>();
        Recycler_item[] item=new Recycler_item[5];
        item[0]=new Recycler_item(R.drawable.a,"#1");
        item[1]=new Recycler_item(R.drawable.a,"#2");
        item[2]=new Recycler_item(R.drawable.a,"#3");
        item[3]=new Recycler_item(R.drawable.a,"#4");
        item[4]=new Recycler_item(R.drawable.a,"#5");

        for(int i=0;i<5;i++) items.add(item[i]);

        recyclerView.setAdapter(new RecyclerAdapter(getApplicationContext(),items,R.layout.activity_main));
    }

}
