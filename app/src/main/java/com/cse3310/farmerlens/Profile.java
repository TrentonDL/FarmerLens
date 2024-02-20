package com.cse3310.farmerlens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {
    ImageButton btn_exit_profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        btn_exit_profile = findViewById(R.id.exit_profile);
        TextView name = findViewById(R.id.display_name);
        TextView textEmail = findViewById(R.id.email_text);
        TextView userid = findViewById(R.id.text_userid);
        TextView profession = findViewById(R.id.text_profession);

        btn_exit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String displayName = user.getDisplayName();
        if(user != null){
            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(displayName).build();
            name.setText(displayName);
            textEmail.setText(user.getEmail());
            String userID = user.getUid();
            final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    userid.setText(user.user_id);
                    profession.setText(user.profession);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Profile.this, "User Data not Found",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

