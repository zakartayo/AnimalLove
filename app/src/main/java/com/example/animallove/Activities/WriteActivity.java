package com.example.animallove.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.animallove.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WriteActivity extends AppCompatActivity {

    private Button btChoose;
    private Button btUpload;
    private ImageView ivPreview, ivDown;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private EditText ed_name, ed_region, ed_gender, ed_kind, ed_desc;

    private Uri filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        btChoose = (Button) findViewById(R.id.bt_choose);
        btUpload = (Button) findViewById(R.id.bt_upload);
        ivPreview = (ImageView) findViewById(R.id.iv_preview);
        ed_name = (EditText)findViewById(R.id.write_name);
        ed_region = (EditText)findViewById(R.id.write_region);
        ed_gender = (EditText)findViewById(R.id.write_gender);
        ed_kind = (EditText)findViewById(R.id.write_kind);
        ed_desc = (EditText)findViewById(R.id.write_desc);
        //ivDown = (ImageView)findViewById(R.id.iv_down);

        storage = FirebaseStorage.getInstance();

        //버튼 클릭 이벤트
        btChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //이미지를 선택
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
            }
        });

        btUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //업로드
                uploadFile();
            }
        });

    }

    //결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK){
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ivPreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //upload the file
    private void uploadFile() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("업로드중...");
            progressDialog.show();

            FirebaseStorage storage = FirebaseStorage.getInstance();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
            Date now = new Date();

            String filename = formatter.format(now) + ".png";
            String name = ed_name.getText().toString();
            String region = ed_region.getText().toString();
            String gender = ed_gender.getText().toString();
            String kind = ed_kind.getText().toString();
            String desc = ed_desc.getText().toString();

            onWriteData(filename, name, region, gender, kind, desc);

            //storage 주소와 폴더 파일명을 지정해 준다.
            StorageReference storageRef = storage.getReferenceFromUrl("gs://animallove-63f5c.appspot.com").child(filename);

            storageRef.putFile(filePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests")
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onWriteData(String img, String name, String region, String gender, String kind, String desc ) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("data");
        String key = myRef.push().getKey();

        myRef.child(key).child("img").setValue(img);
        myRef.child(key).child("name").setValue(name);
        myRef.child(key).child("region").setValue(region);
        myRef.child(key).child("gender").setValue(gender);
        myRef.child(key).child("kind").setValue(kind);
        myRef.child(key).child("desc").setValue(desc);

    }

}

