package com.apps.vicky.addressmanager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Vicky cj on 25-11-2017.
 */

public class SharedConstants {

    private static DatabaseReference mDatabase;

    public static class Names{
        public static final String SHARED_PREFERENCE_NAME="shared-name";
        public static final String USER_NAME="user-name";
        public static final String IMAGE_URI="image-uri";
        public static final String LOG_OUT="log-out";
        public static final String LOG_IN="log-in";
        public static final int GOOGLE_SIGN_IN = 5832;
        public static final String DB_USERS_REF="users";
        public static final int ADD_ADDRESS=1;
        public static final int VIEW_ADDRESS=2;
    }



    public static DatabaseReference getDatabase(String userId) {

        if (mDatabase == null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);


            mDatabase = database.getReference().child(SharedConstants.Names.DB_USERS_REF).child(userId).getRef();

        }

        return mDatabase;
    }
}
