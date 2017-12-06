package com.example.animallove.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.animallove.MainActivity;
import com.example.animallove.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WriteActivity extends AppCompatActivity {

    private Button btChoose;
    private Button btUpload;
    private EditText animalName;
    private EditText animalTitle;
    private ImageView ivPreview, ivDown;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private AlertDialog.Builder ab;
    private EditText animalGender;
    private EditText animalArea;
    private EditText animalSpecies;

    private String [] gender = {"수컷", "암컷"};
    private String[] arrText = {"species", "area", "gender"};
    private String[] area = {"서울","경기","부산","광주","대구","대전","강원"};
    private String[] species={"푸들","말티즈","시추","포메라니안","웰시코기","슈나우저"};
    private Uri filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);


        btChoose = (Button) findViewById(R.id.photobtn);
        btUpload = (Button) findViewById(R.id.commit);
        ivPreview = (ImageView) findViewById(R.id.photo);
        //ivDown = (ImageView)findViewById(R.id.iv_down);
        animalName =(EditText)findViewById(R.id.name);
        animalTitle =(EditText)findViewById(R.id.title);
        animalGender =(EditText)findViewById(R.id.gender);
        animalArea=(EditText)findViewById(R.id.area);
        animalSpecies=(EditText)findViewById(R.id.species);
        ab = new AlertDialog.Builder(WriteActivity.this);

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
        animalGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ab.setTitle("성별을 선택해주세요");
                ab.setSingleChoiceItems(gender, 2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        animalGender.setText(gender[i]);
                    }
                }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                ab.show();
            }
        });
        animalArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ab.setTitle("지역을 선택해주세요");
                ab.setSingleChoiceItems(area, 9, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        animalArea.setText(area[i]);
                    }
                }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                ab.show();
            }
        });
        animalSpecies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ab.setTitle("종을 선택해주세요");
                ab.setSingleChoiceItems(species, 9, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        animalSpecies.setText(species[i+1]);
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                ab.show();
            }
        });


        btUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //업로드
                uploadFile();
            }
        });
        animalName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode()== KeyEvent.KEYCODE_ENTER){
                    hideKeyBoard(animalName);
                    return true;
                }
                return false;
            }
        });
        animalTitle.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode()== KeyEvent.KEYCODE_ENTER){
                    hideKeyBoard(animalName);
                    return true;
                }
                return false;
            }
        });
    }
    private void hideKeyBoard (EditText e){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(e.getWindowToken(),0);
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
            }catch (FileNotFoundException e) {
                e.printStackTrace();
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

            String title = animalTitle.getText().toString();
            String animal_Name = animalName.getText().toString();
            String animal_Area =animalArea.getText().toString();
            String animal_Gender =animalGender.getText().toString();
            String animal_Species = animalSpecies.getText().toString();
            onWriteData(filename, animal_Name, animal_Area, animal_Gender, animal_Species, title);

            //storage 주소와 폴더 파일명을 지정해 준다.
            StorageReference storageRef = storage.getReferenceFromUrl("gs://animallove-63f5c.appspot.com").child(filename);

            storageRef.putFile(filePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Intent intent = new Intent (WriteActivity.this, MainActivity.class);
                            startActivity(intent);
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

