package com.cse3310.farmerlens;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class User {
    public String user_id;
    public String profession;
    public String dateOfBirth;

    public User(String user_id, String profession, String dateOfBirth) {
        this.user_id = user_id;
        this.profession = profession;
        this.dateOfBirth = dateOfBirth;
    }

    public String getUserID(User user){
        return user.user_id;
    }

    public String getProfession(User user){
        return user.profession;
    }

    public String getDateOfBirth(User user){
        return user.dateOfBirth;
    }
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("users");
    DatabaseReference usersRef = ref.child("users");
}
