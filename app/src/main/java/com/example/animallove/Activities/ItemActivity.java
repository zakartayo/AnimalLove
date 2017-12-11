package com.example.animallove.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.animallove.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ItemActivity extends AppCompatActivity {

    ArrayList<String> items = new ArrayList<>();
    TextView item_kind, item_name, item_gender, item_region, item_email, item_text;
    ImageView  item_image, gmail;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        items = getIntent().getStringArrayListExtra("data");
        storage = FirebaseStorage.getInstance();

        item_kind = (TextView)findViewById(R.id.item_kind);
        item_name = (TextView)findViewById(R.id.item_name);
        item_gender = (TextView)findViewById(R.id.item_gender);
        item_region = (TextView)findViewById(R.id.item_region);
        item_text = (TextView)findViewById(R.id.item_text);
        item_email = (TextView)findViewById(R.id.item_email);

        item_image = (ImageView) findViewById(R.id.item_image);
        gmail = (ImageView)findViewById(R.id.gmail) ;

        item_kind.setText(items.get(0));
        item_name.setText(items.get(1));
        item_gender.setText(items.get(2));
        item_region.setText(items.get(3));
        item_email.setText(items.get(5));
        item_text.setText(items.get(6));

        final long ONE_MEGABYTE = 1024 * 1024 * 5;
        StorageReference storageRef = storage.getReferenceFromUrl("gs://animallove-63f5c.appspot.com").child(items.get(4));
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                item_image.setImageBitmap(bitmap);
            }
        });

        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemActivity.this, Email.class);
                intent.putExtra("email", items.get(5));
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

    }
}
