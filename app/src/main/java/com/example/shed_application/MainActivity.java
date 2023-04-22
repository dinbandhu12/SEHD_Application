package com.example.shed_application;

import org.tensorflow.lite.support.image.TensorImage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shed_application.ml.MobilenetV110224Quant;
import com.example.shed_application.ml.ModelUnquant;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final String Result = "com.dbs.SHED_Application.SeeFullResult";


    Button selectBtn, predictBtn, captureBtn;
    TextView result;
    ImageView imageView;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Menubar Icon
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView menubar_icon = findViewById(R.id.menubar_icon);
        menubar_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(MainActivity.this, "You Click the menubar", Toast.LENGTH_SHORT).show();

                showMenu(view);
            }
            private void showMenu(View v) {

                PopupMenu popupMenu = new PopupMenu(MainActivity.this,v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if(menuItem.getItemId() == R.id.One)
                            Toast.makeText(MainActivity.this, "You Click the First Button", Toast.LENGTH_SHORT).show();
                        if(menuItem.getItemId() == R.id.Two)
                            Toast.makeText(MainActivity.this, "You Click the Second Button", Toast.LENGTH_SHORT).show();
                        if(menuItem.getItemId() == R.id.Three)
                            Toast.makeText(MainActivity.this, "You Click the Third Button", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });


            }
        });



        // Permission
        getPermission();



        selectBtn = findViewById(R.id.selectBtn);
        predictBtn = findViewById(R.id.predictBtn);
        captureBtn = findViewById(R.id.captureBtn);
        result = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView);

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 10);
            }
        });

        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 12);
            }
//            private void startActivityForResult(Intent intent, int i) {
//            }
        });

        predictBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {


                try {
                    Context context;
                    ModelUnquant model = ModelUnquant.newInstance(MainActivity.this);

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);

                    bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);

                    inputFeature0.loadBuffer(TensorImage.fromBitmap(bitmap).getBuffer());

                    // Runs model inference and gets result.
                    ModelUnquant.Outputs outputs = model.process(inputFeature0);

                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                    result.setText(getMax(outputFeature0.getFloatArray())+"");

                    // Releases model resources if no longer used.
                    model.close();
                } catch (IOException e) {
                    // TODO Handle the exception
                }



            }
        });



    }

    int getMax(float[] arr) {
        int max = 0;
        for(int i=0; i<arr.length; i++) {
            if(arr[i] >arr[max]) max=i;
        }
        return max;
    }

    void getPermission() {
        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, 11);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 11) {
            if(grantResults.length > 0) {
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    this.getPermission();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==10) {
            if(data != null) {
                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        else if(requestCode == 12) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


//    Place Order button activity
    public void see_full_result(View view) {
     // Here can show the result
     // to building Intent for going next activity (page)
     Intent intent = new Intent(this, SeeFullResult.class);
        @SuppressLint("WrongViewCast") EditText editText = findViewById(R.id.result);
        String Full_result_msg = editText.getText().toString();
        intent.putExtra(Result, Full_result_msg);
        startActivity(intent);
    }



}