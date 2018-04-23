package com.vastcast.vastcast;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

public class DatabaseWrapper {
    private static DatabaseReference myRef;
    private static DatabaseReference myData;
    private static String userID;
    private static boolean initStat;

    protected static void initializeDB() {
        // for data persistence
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        myRef=FirebaseDatabase.getInstance().getReference();
        myData=myRef.child("Database");
        //userID=myRef.push().getKey();
        initStat=true;
    }
    public static void addCollection(Collection col){
        if(!initStat) {
            initializeDB();
        }
        //pushing Collection
        if(myData==null){
           Log.d("DatabaseWrapper", "please no");
        }
        //read from db to see if source is in db already

        myData.push().setValue(col);
        //get Collection ID
        //=myData.getRef(col);
        //Put collection ID into Users Library
        //myRef.child("Users").child(userID).child("Library").push().setValue(col);

    }
    public static void addToQueue(Collection col){
        myRef.child("Users").child(userID).child("Queue").push().setValue(col);
    }
    public static void updateCollection(Collection col){
        //this isn't entirely right atm
        myRef.child("Users").child(userID).child("Library").setValue(col);
    }
    public static void updateQueue(Collection col){
        //this isn't entirely right atm
        myRef.child("Users").child(userID).child("Queue").setValue(col);
    }
    public static void removeFromCollection(Collection col){
       // myRef.child("Users").child(userID).child("Library").removeValue();
    }
    public static void removeFromQueue(Collection col){
        // myRef.child("Users").child(userID).child("Queue").removeValue();
    }
}
