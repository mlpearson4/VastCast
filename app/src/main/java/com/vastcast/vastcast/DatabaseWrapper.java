/*package com.vastcast.vastcast;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

public class DatabaseWrapper {
    private DatabaseReference myRef;
    private StorageReference mStorageRef;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // for data persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        myRef=FirebaseDatabase.getInstance().getReference("User");
        userID= myRef.push().getKey();
    }
    private void addCollection(Collection col){
        myRef.child().setValue(col);
    }
    private void updateCollection(Collection col){
        myRef.child(userID).child("library").setValue();
        myRef.child(userID).child("").setValue();
    }
    private void removePerson(String name){
        /*Query deleteQuery = myDatabaseReference.orderByChild("fullName").equalTo(name);
        deleteQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });*/
/*
        myDatabaseReference.child(personId).removeValue();
    }

}
*/