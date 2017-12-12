package com.apps.vicky.addressmanager;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener,AddressAdapter.LongClickInterface {



    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private static String userId;
    private static String emailId;
    private static String userName;
    private static DatabaseReference mDatabase;
    private FloatingActionButton floatingActionButton;
    private RecyclerView mAddressRecycler;
    private AddressAdapter mAddressAdapter;
    private   AlertDialog dialog;
    private   AlertDialog editdialog;
    private TextInputEditText addressName;
    private TextInputEditText fullAddress;

    private TextInputEditText addressNameEdit;
    private TextInputEditText fullAddressEdit;

    private SearchView searchView;
    private  List<UserData> userDataTotalGlobal;
    private UserData editedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        floatingActionButton=findViewById(R.id.fab_add_address);
        mAddressRecycler=findViewById(R.id.recycler_address);
        mAuth = FirebaseAuth.getInstance();

        signDetails();
        recyclersLoad();
        frameAlertDialog();
        frameEditAlertDialog();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeNewAddress();
            }
        });





    }

    public void frameAlertDialog(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.add_address, (ViewGroup) findViewById(R.id.layout_root_add));
        addressName=layout.findViewById(R.id.address_name_et);
        fullAddress=layout.findViewById(R.id.full_address_et);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        builder.setTitle(getString(R.string.address_title));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.save_button_placeholder), null);
        builder.setNegativeButton(getString(R.string.cancel_button_placeholder), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addressName.setText("");
                fullAddress.setText("");
                dialog.dismiss();
            }
        });


        dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface View) {
                Button button=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveAddress();
                    }
                });
            }
        });
    }

    public void frameEditAlertDialog(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.add_address, (ViewGroup) findViewById(R.id.layout_root_add));
        addressNameEdit=layout.findViewById(R.id.address_name_et);
        fullAddressEdit=layout.findViewById(R.id.full_address_et);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        builder.setTitle(getString(R.string.edit_address_title));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.save_button_placeholder), null);
        builder.setNegativeButton(getString(R.string.cancel_button_placeholder), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addressNameEdit.setText("");
                fullAddressEdit.setText("");
                dialog.dismiss();
            }
        });
        editdialog = builder.create();
        editdialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface View) {
                Button button=editdialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveEditedAddress();
                    }
                });
            }
        });
    }



    public void saveAddress(){

            if(TextUtils.isEmpty(addressName.getText().toString().trim())){
                addressName.setError(getString(R.string.address_error));

                return;
            }

            if(TextUtils.isEmpty(fullAddress.getText().toString().trim())){
                fullAddress.setError(getString(R.string.address_error));

                return;
            }


        String addressText=addressName.getText().toString().trim();
        String fullAddressText=fullAddress.getText().toString().trim();

        mDatabase.push().setValue(new UserData(userName,addressText,fullAddressText,"",""));
        addressName.setText("");
        fullAddress.setText("");
        dialog.dismiss();
        Toast.makeText(this, getString(R.string.address_saved_message),Toast.LENGTH_LONG).show();

    }


    public void recyclersLoad(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mAddressRecycler.setLayoutManager(layoutManager);
        mAddressRecycler.setHasFixedSize(false);
        List<UserData> userDataTotal = new ArrayList<UserData>();
        mAddressAdapter = new AddressAdapter(userDataTotal,this);
        mAddressRecycler.setAdapter(mAddressAdapter);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mAddressRecycler);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallbackRight = new RecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallbackRight).attachToRecyclerView(mAddressRecycler);
    }


    public void signDetails(){

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getBaseContext(), "Connection Failed", Toast.LENGTH_LONG).show();
                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in

                    userId=user.getUid();
                    emailId=user.getEmail();
                    userName=user.getDisplayName();
                    mDatabase =SharedConstants.getDatabase(userId);
                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            refreshUI(dataSnapshot);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    // User is signed out
                    Intent loginIntent=new Intent(getApplicationContext(),LoginActivity.class);
                    loginIntent.setAction(SharedConstants.Names.LOG_IN);
                    startActivity(loginIntent);
                    finish();
                }
                // ...
            }
        };

    }


    public void refreshUI(DataSnapshot dataSnapshot){
        long count=dataSnapshot.getChildrenCount();



        List<UserData> userDataTotal;
        userDataTotal=new ArrayList<>();

        if(count>0){
            for(DataSnapshot data: dataSnapshot.getChildren()){
                UserData userData;
                userData=data.getValue(UserData.class);
                userData.setKey(data.getKey());
                userDataTotal.add(userData);
            }
        }



        userDataTotalGlobal=userDataTotal;
        mAddressAdapter.swapData(userDataTotal);

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();

        menuInflater.inflate(R.menu.settings,menu);


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAddressAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAddressAdapter.getFilter().filter(query);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.sign_out_menu) {
            signOut();
            return true;
        }

        if (id == R.id.action_search) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void signOut(){
        mAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Toast.makeText(getBaseContext(), "Logged Out", Toast.LENGTH_LONG).show();
                    }
                });
    }


    public void writeNewAddress(){

        ///mDatabase.push().setValue(new UserData(userName,"gfdgdf Home","fsjfhjsf ghsf gjhf fh gdhfghdfl","12-10-2017","vygj@gmail.com"));
        addressName.setError(null);
        fullAddress.setError(null);
        addressName.requestFocus();
        dialog.show();


    }


    public void leftSwiped(RecyclerView.ViewHolder viewHolder){
        final UserData deletedItem = userDataTotalGlobal.get(viewHolder.getAdapterPosition());
        final int deletedIndex = viewHolder.getAdapterPosition();


        mAddressAdapter.removeItem(viewHolder.getAdapterPosition());


        Snackbar snackbar=Snackbar.make(getWindow().getDecorView().getRootView(),getString(R.string.deleted_success),Snackbar.LENGTH_SHORT);
        snackbar.setAction(getString(R.string.undo_text), new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAddressAdapter.restoreItem(deletedItem, deletedIndex);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            snackbar.setActionTextColor(getColor(R.color.colorAccent));
        }else{
            snackbar.setActionTextColor(Color.CYAN);
        }
        snackbar.addCallback(new Snackbar.Callback(){
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if(event!=DISMISS_EVENT_ACTION){
                    mDatabase.child(deletedItem.getKey()).removeValue();
                }

            }
        });
        snackbar.show();
    }

    public void rightSwiped(RecyclerView.ViewHolder viewHolder){
        addressNameEdit.setError(null);
        fullAddressEdit.setError(null);
        editedItem = userDataTotalGlobal.get(viewHolder.getAdapterPosition());
        addressNameEdit.setText(editedItem.getAddressName());
        fullAddressEdit.setText(editedItem.getAddressValue());
        mAddressAdapter.swapData(userDataTotalGlobal);
        editdialog.show();

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if(direction==ItemTouchHelper.LEFT){
            if (viewHolder instanceof AddressAdapter.AddressViewHolder) {

                leftSwiped(viewHolder);
            }
        }else if(direction==ItemTouchHelper.RIGHT){
            if (viewHolder instanceof AddressAdapter.AddressViewHolder) {

                rightSwiped(viewHolder);
            }

        }



    }


    public void saveEditedAddress(){
        if(TextUtils.isEmpty(addressNameEdit.getText().toString().trim())){
            addressNameEdit.setError(getString(R.string.address_error));
            return;
        }

        if(TextUtils.isEmpty(fullAddressEdit.getText().toString().trim())){
            fullAddressEdit.setError(getString(R.string.address_error));
            return;
        }

        String addressText=addressNameEdit.getText().toString().trim();
        String fullAddressText=fullAddressEdit.getText().toString().trim();

        mDatabase.child(editedItem.getKey()).setValue(new UserData(userName,addressText,fullAddressText,"",""));


        addressNameEdit.setText("");
        fullAddressEdit.setText("");
        editdialog.dismiss();
        Toast.makeText(this, getString(R.string.address_saved_message),Toast.LENGTH_LONG).show();
    }

    @Override
    public void longClickEvent(int position) {
        UserData userData=userDataTotalGlobal.get(position);
        String shareBody = userData.getAddressName()+" - "+userData.getAddressValue();
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share "+userData.getAddressName()+" Address"));

    }
}
