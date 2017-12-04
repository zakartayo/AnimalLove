package com.example.animallove;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.animallove.Activities.WriteActivity;
import com.example.animallove.Adapter.RecyclerAdapter;
import com.example.animallove.Classes.Recycler_item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private RecyclerView recyclerView;
    private Button write, search;
    private List<Recycler_item> items=new ArrayList<>();
    final CharSequence[] genders = {"수컷", "암컷"}; // 검색용
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        write = (Button)findViewById(R.id.write);
        search = (Button)findViewById(R.id.btn_search);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        show();

        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WriteActivity.class);
                startActivity(intent);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("성별을 선택하세요")
                       .setSingleChoiceItems(genders, -1, new DialogInterface.OnClickListener(){

                            public void onClick(DialogInterface dialog, int index){
                                //Toast.makeText(getApplicationContext(), items[index], Toast.LENGTH_SHORT).show();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void show() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("data");
        Query contacts = myRef;
        contacts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    String img = snapshot.child("img").getValue(String.class);
                    String name = snapshot.child("name").getValue(String.class);
                    String region = snapshot.child("region").getValue(String.class);
                    String gender = snapshot.child("gender").getValue(String.class);
                    String kind = snapshot.child("kind").getValue(String.class);
                    String desc = snapshot.child("desc").getValue(String.class);

                    items.add(new Recycler_item(img,name, region, gender, kind, desc));
                    recyclerView.setAdapter(new RecyclerAdapter(getApplicationContext(),items,R.layout.activity_main));
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }




}
