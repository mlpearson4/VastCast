package com.vastcast.vastcast;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class DatabaseWrapper {
    private static DatabaseReference myRef;
    private static DatabaseReference myData;
    private static DatabaseReference myUser;
    private static FirebaseUser user;
    private static String userID;
    private static boolean initStat;
    private static Collection d;

    protected static void initializeDB() {
        myRef=FirebaseDatabase.getInstance().getReference();
        user= FirebaseAuth.getInstance().getCurrentUser();
        userID=user.getUid();
        myData=myRef.child("Database");
        myUser=myRef.child("Users");
        initStat=true;
    }

    public static void addCollection(Collection col){
        if(!initStat) {
            initializeDB();
        }
        myData.push().setValue(col);
        //myUser.child(userID).child("Playlists").push().setValue(col);
    }
    public static void addToUserLibrary(Collection col){
        if(!initStat) {
            initializeDB();
        }
       myUser.child(userID).child("Library").push().setValue(col);
    }
    public static void addToQueue(Collection col){
        if(!initStat) {
            initializeDB();
        }
        myUser.child(userID).child("Queue").push().setValue(col);
    }
    public static void updateCollection(Collection col){

    }
    public static void updateQueue(Collection col){

    }
    public static void removeFromCollection(Collection col){

    }
    public static void removeFromQueue(Collection col){

    }
}
