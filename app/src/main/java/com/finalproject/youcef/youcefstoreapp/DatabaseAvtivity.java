package com.finalproject.youcef.youcefstoreapp;

/**
 * Created by Youcef on 11/12/2016.
 * Student Number: x13114557
 */



import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.TextUtils;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;



public class DatabaseAvtivity extends AppCompatActivity {

    private static final String TAG = DatabaseAvtivity.class.getSimpleName();
    private TextView txtDetails;
    private EditText inputProdName, inputPrice;
    private Button btnSave;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtDetails = (TextView) findViewById(R.id.text_prodDetails);
        inputProdName = (EditText) findViewById(R.id.add_prodName);
        inputPrice = (EditText) findViewById(R.id.addPrice);
        btnSave = (Button) findViewById(R.id.save_Btn);

        mFirebaseInstance = FirebaseDatabase.getInstance();

        //says im interested in the products portion of the database
        mFirebaseDatabase = mFirebaseInstance.getReference("products");


        // Save product data
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pName = inputProdName.getText().toString();
                String price = inputPrice.getText().toString();

                // Check for already existed productId
                if (TextUtils.isEmpty(productId)) {
                    createProduct(pName, price);
                } else {
                    updateProduct(pName, price);
                }
            }
        });


    }



    // this method puts the added product name and price under one productId
    private void createProduct(String pName, String price) {

        if (TextUtils.isEmpty(productId)) {
            productId = mFirebaseDatabase.push().getKey();
        }

        Product product = new Product(pName, price);

        mFirebaseDatabase.child(productId).setValue(product);

        addProductChangeListener();
    }

    // This method listens out for any changes in the data and then displays the change
    private void addProductChangeListener() {
        mFirebaseDatabase.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);

                // Check for null
                if (product == null) {
                    Log.e(TAG, "Product data is null!");
                    return;
                }

                Log.e(TAG, "Product data has changed!" + product.pName + ", " + product.price);

                // Displays new product name and price
                txtDetails.setText(product.pName + ", " + product.price);

                // clears the  edit text fields
                inputPrice.setText("");
                inputProdName.setText("");

            }

            //If there is an error retrieving product data
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read product", error.toException());
            }
        });
    }

    //This method updates the product data in the database when product is added
    private void updateProduct(String pName, String price) {

        // updating product name location on the database (child node)
        if (!TextUtils.isEmpty(pName))
            mFirebaseDatabase.child(productId).child("product_name").setValue(pName);

        // updating product price location on the database (child node)
        if (!TextUtils.isEmpty(price))
            mFirebaseDatabase.child(productId).child("price").setValue(price);
    }
}