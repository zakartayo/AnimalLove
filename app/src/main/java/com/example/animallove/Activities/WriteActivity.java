package com.example.animallove.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
    private EditText animalContents;
    private ImageView ivPreview, ivDown;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private AlertDialog.Builder ab;
    private EditText animalGender;
    private EditText animalArea;
    private EditText animalSpecies;
    private String getSpecies;
    private String getArea;
    private String getGender;
    private String [] gender = {"수컷", "암컷"};
    private String[] species={"닥스훈트","말티즈","비글","비숑프리제","스파니엘","시추","슈나우저","웰시코기","요크셔테리어",
            "치와와","코카스파니엘","프렌치불독","푸들","포메라니안","페키니즈","기타"};
    private Uri filePath;
    private int num1=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        btChoose = (Button) findViewById(R.id.photobtn);
        btUpload = (Button) findViewById(R.id.commit);
        ivPreview = (ImageView) findViewById(R.id.photo);
        animalName =(EditText)findViewById(R.id.name);
        animalContents =(EditText)findViewById(R.id.shipper_field);
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
                final AlertDialog.Builder alert = new AlertDialog.Builder(WriteActivity.this);
                final AlertDialog alertd = alert.create();
                alert.setTitle("성별을 선택해주세요");
                alert.setSingleChoiceItems(gender, 2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getGender = gender[i];
                    }
                }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (num1 == 1) {
                            chooseSpecies();
                        }
                        animalGender.setText(getGender);

                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        alertd.dismiss();
                    }
                });
                alert.show();
            }
        });
        animalArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyBoard();
                final AlertDialog.Builder alert = new AlertDialog.Builder(WriteActivity.this);
                final AlertDialog alertd = alert.create();
                alert.setTitle("지역을 입력해주세요");
                alert.setMessage("ex)서울, 부산");
                final EditText et = new EditText(WriteActivity.this);
                alert.setView(et);
                alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getArea = (et.getText().toString());
                        animalArea.setText(getArea);
                        if(num1==1) {
                            chooseGender();
                        }
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        alertd.dismiss();
                    }
                });
                alert.show();
            }

        });
        animalSpecies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(WriteActivity.this);
                final AlertDialog alertd = alert.create();
                hideKeyBoard();
                alert.setTitle("종을 선택해주세요");
                alert.setSingleChoiceItems(species, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getSpecies = species[i];
                    }
                }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(getSpecies.equals("기타")){
                            dialogInterface.cancel();
                            alertd.dismiss();
                            dialog();

                        }
                        else {
                            animalSpecies.setText(getSpecies);
                            animalContents.requestFocus();
                        }
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        alertd.dismiss();
                    }
                });
                alert.show();
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
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                    if(num1==1) {
                        animalArea.requestFocus();
                        chooseArea();

                    }

                    return true;
                }
                return false;
            }
        });
        animalContents.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode()== KeyEvent.KEYCODE_ENTER){
                    hideKeyBoard();
                    return true;
                }
                return false;
            }
        });
    }

    private void hideKeyBoard (){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_IMPLICIT_ONLY);
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
    private void chooseSpecies(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(WriteActivity.this);
        final AlertDialog alertd = alert.create();
        hideKeyBoard();
        alert.setTitle("종을 선택해주세요");
        alert.setSingleChoiceItems(species, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getSpecies = species[i];
                num1+=1;
            }
        }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(getSpecies.equals("기타")){
                    dialogInterface.cancel();
                    alertd.dismiss();
                    dialog();

                }
                else {
                    animalSpecies.setText(getSpecies);
                    animalContents.requestFocus();
                }
            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                alertd.dismiss();
            }
        });
        alert.show();
    }

    private void chooseArea() {
        hideKeyBoard();
        final AlertDialog.Builder alert = new AlertDialog.Builder(WriteActivity.this);
        final AlertDialog alertd = alert.create();
        alert.setTitle("지역을 입력해주세요");
        alert.setMessage("ex)서울, 부산");
        final EditText et = new EditText(WriteActivity.this);
        alert.setView(et);
        alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getArea = (et.getText().toString());
                animalArea.setText(getArea);
                chooseGender();
            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                alertd.dismiss();
            }
        });
        alert.show();
    }

    private void chooseGender(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(WriteActivity.this);
        final AlertDialog alertd = alert.create();
        alert.setTitle("성별을 선택해주세요");
        alert.setSingleChoiceItems(gender, 2,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getGender = gender[i];
            }
        }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                chooseSpecies();
                animalGender.setText(getGender);

            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                alertd.dismiss();
            }
        });
        alert.show();
    }

    private void dialog(){

        ab.setTitle("종을 입력해주세요");
        ab.setMessage("");
        final EditText et = new EditText(WriteActivity.this);
        ab.setView(et);
        ab.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getSpecies = et.getText().toString();
                animalSpecies.setText(getSpecies);
                animalContents.requestFocus();
                num1++;
            }
        });
        ab.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        ab.show();
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

            String title = animalContents.getText().toString();
            String animal_Name = animalName.getText().toString();
            String animal_Gender =animalGender.getText().toString();
            onWriteData(filename, animal_Name, getArea, animal_Gender, getSpecies, title);

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