package com.cse3310.farmerlens;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//import kotlinx.coroutines.scheduling.Task;

public class Register extends Activity {

    TextInputEditText editTextFName,editTextLName, editTextEmail, editTextPassword, editTextUserId, editTextDateOfBirth, editTextProfession;
    Button buttonReg;
    FirebaseAuth mAuth;
    TextView textView;
    ImageButton backButton;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser !=null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth= FirebaseAuth.getInstance();
        editTextFName = findViewById(R.id.first_name);
        editTextLName = findViewById(R.id.last_name);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextUserId = findViewById(R.id.user_id);
        editTextDateOfBirth = findViewById(R.id.date_of_birth);
        editTextProfession = findViewById(R.id.profession);
        buttonReg = findViewById(R.id.btn_register);
        textView = findViewById(R.id.loginNow);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

       textView.setOnClickListener(new  View.OnClickListener(){
           public void onClick(View view){
               Intent intent = new Intent(getApplicationContext(), Login.class);
               startActivity(intent);
               finish();
           }
       });
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String f_name = String.valueOf(editTextFName.getText());
                String l_name = String.valueOf(editTextLName.getText());
                String email = String.valueOf(editTextEmail.getText());
                String password = String.valueOf(editTextPassword.getText());
                String user_id = String.valueOf(editTextUserId.getText());
                String date_of_birth = String.valueOf(editTextDateOfBirth.getText());
                String profession = String.valueOf(editTextProfession.getText());



                if (TextUtils.isEmpty(f_name)) {
                    Toast.makeText(Register.this, "Enter First Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(l_name)) {
                    Toast.makeText(Register.this, "Enter Last Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Register.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Register.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(user_id)) {
                    Toast.makeText(Register.this, "Enter your user ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(date_of_birth)) {
                    Toast.makeText(Register.this, "Enter your date of birth", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(profession)) {
                    Toast.makeText(Register.this, "Enter your profession", Toast.LENGTH_SHORT).show();
                    return;
                }

                createUserWithEmailAndPassword(email, password, user_id, date_of_birth, f_name, l_name, profession);
            }
        });
    }

      private void createUserWithEmailAndPassword(String email, String password, String user_id, String date_of_birth, String f_name, String l_name, String profession) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest updateDisplayName = new UserProfileChangeRequest.Builder().setDisplayName(f_name + " " + l_name).build();
                            user.updateProfile(updateDisplayName);
                            if (user != null) {
                                String userID = user.getUid();
                                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference usersRef = database.getReference("users");
                                usersRef.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                        User newUser = snapshot.getValue(User.class);
                                        newUser.user_id = user_id;
                                        newUser.profession = profession;
                                        newUser.dateOfBirth = date_of_birth;
                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                usersRef.child(userID).child("user_id").setValue(user_id);
                                usersRef.child(userID).child("date_of_birth").setValue(date_of_birth);
                                usersRef.child(userID).child("profession").setValue(profession);
                            }
                            Log.d("RegisterActivity", "Registration successful.");
                            Toast.makeText(Register.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Exception exception = task.getException();
                            if (exception instanceof FirebaseAuthUserCollisionException) {
                                Log.e("RegisterActivity", "Registration failed: Email already in use.");
                                Toast.makeText(Register.this, "Email already in use.", Toast.LENGTH_SHORT).show();
                            } else {

                                Log.e("RegisterActivity", "Registration failed: " + exception.getMessage());
                                Toast.makeText(Register.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                });
    }

}
