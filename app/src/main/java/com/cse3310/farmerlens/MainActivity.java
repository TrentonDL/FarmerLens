package com.cse3310.farmerlens;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cse3310.farmerlens.ml.Model;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


import com.cse3310.farmerlens.PlantIdResponse;

import java.io.BufferedReader;
import java.io.IOException;
import android.util.Log;

import java.io.InputStreamReader;
import java.lang.Throwable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.graphics.Bitmap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST_CODE = 100;
    private PlantIdResponse plantIdResponse;

    TextView textView;
    ImageButton Logout_button;
    ImageButton Menu_Button;
    FirebaseUser user;
    String display_name;

    int imageSize = 32;

    Button gallerybtn, searchBtn;
    ImageButton camerabtn;
    TextView result;
    ImageView imageView;
    Bitmap bitmap;

    String searchQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        getCameraPermission();

        String[] labels = new String[13];
        int count = 0;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open("seedlabels.txt")));
            String line = bufferedReader.readLine();
            while(line != null){
                labels[count] = line;
                count++;
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Menu_Button = findViewById(R.id.menu_button);
        textView = findViewById(R.id.user_details);

        gallerybtn = findViewById(R.id.galleryButton);
        searchBtn = findViewById(R.id.conduct_search);
        camerabtn = findViewById(R.id.cameraButton);
        result = findViewById(R.id.result);
        imageView = findViewById(R.id.seedImageView);

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    textView.setText(user.getDisplayName());
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        

        Menu_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                startActivity(intent);
                finish();
            }
        });


        gallerybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 10);
            }
        });

        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 12);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    Model model = Model.newInstance(MainActivity.this);

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 32, 32, 3}, DataType.FLOAT32);

                    bitmap = Bitmap.createScaledBitmap(bitmap, 32, 32, false);

                    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
                    byteBuffer.order(ByteOrder.nativeOrder());

                    int[] intValues = new int[imageSize * imageSize];
                    bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
                    int pixel = 0;

                    for(int i = 0; i < imageSize; i++) {

                        for(int j = 0; j < imageSize; j++) {

                            int val = intValues[pixel++];
                            byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 1));
                            byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 1));
                            byteBuffer.putFloat((val & 0xFF) * (1.f / 1));
                        }
                    }

                    inputFeature0.loadBuffer(byteBuffer);

                    // Runs model inference and gets result.
                    Model.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    result.setText(labels[getMax(outputFeature0.getFloatArray())]+" ");

                    searchQuery = labels[getMax(outputFeature0.getFloatArray())];

                    result.setEnabled(true);

                    result.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onTextViewClick(view);
                        }
                    });

                    // Releases model resources if no longer used.
                    model.close();

                } catch (IOException e) {
                    // TODO Handle the exception
                }

            }
        });



    }

    public void onTextViewClick(View view) {

        String searchURL = "https://en.wikipedia.org/w/index.php?search=" + Uri.encode(searchQuery);

        Intent searchIntent = new Intent(Intent.ACTION_VIEW);
        searchIntent.setData(Uri.parse(searchURL));

        startActivity(searchIntent);

    }

    int getMax(float[] arr) {
        int max = 0;
        for(int i = 0; i < arr.length; i++) {
            if(arr[i] > arr[max]) max = i;
        }
        return max;
    }

    private void getCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CAMERA}, 11);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 11) {
            if(grantResults.length > 0) {
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    this.getCameraPermission();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 10 && data != null) {

            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                searchBtn.setEnabled(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(bitmap);

        }
        else if (requestCode == 12) {

            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            searchBtn.setEnabled(true);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }

    public class Constants {
        public static final String PLANT_ID_API_KEY = "Farmer Lens";
    }

}