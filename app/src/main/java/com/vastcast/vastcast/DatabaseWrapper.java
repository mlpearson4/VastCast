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
    /*public static Collection checkCollection(URL source){
        String s=source.toString();
        String result = null;
        try
        {
            result = URLEncoder.encode(s, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~")
                    .replaceAll("\\.","%2E");
        }

        // This exception should never occur.
        catch (UnsupportedEncodingException e)
        {
            result = s;
        }
        /*Query q=myData.equalTo(result).addListenerForSingleValueEvent();
        );
        if(q!=null){
            //return results from query right here
            //Collection c = q.getClass(Collection);
            return c;
        }
        return null;
    }*/
    public static void addCollection(Collection col){
        if(!initStat) {
            initializeDB();
        }
       /* Log.d("DatabaseWrapper","pre-encoded");
        String result = null;

        try
        {
            result = URLEncoder.encode(col.getSource(), "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~")
                    .replaceAll("\\.","%2E");
        }

        // This exception should never occur.
        catch (UnsupportedEncodingException e)
        {
            result = col.getSource();
        }*/
       //String result = URLEncoder.encode(col.getSource(), "UTF-8");

        //pushing Collection
        //myData.child(result).setValue(col);
        myData.push().setValue(col);
        //Put collection ID into Users Library
        myUser.child(userID).child("Playlists").push().setValue(col);
        //myUser.child(userID).child("Playlists").setValue(result);
    }
    public static void addToUserLibrary(Collection col){
        if(!initStat) {
            initializeDB();
        }
        myUser.child(userID).child("Playlists").push().setValue(col);
    }
    public static void addToQueue(Collection col){
        if(!initStat) {
            initializeDB();
        }
        myUser.child(userID).child("Queue").push().setValue(col);
    }
    public static void updateCollection(Collection col){
        //this isn't entirely right atm
        //find key

        //then set value at key
        //myData.child("<keyishere>").setValue(col);
    }
    public static void updateQueue(Collection col){
        //this isn't entirely right atm
        //myRef.child("Users").child(userID).child("Queue").setValue(col);
    }
    public static void removeFromCollection(Collection col){
        // myRef.child("Users").child(userID).child("Library").removeValue();
    }
    public static void removeFromQueue(Collection col){
        // myRef.child("Users").child(userID).child("Queue").removeValue();
    }
}
