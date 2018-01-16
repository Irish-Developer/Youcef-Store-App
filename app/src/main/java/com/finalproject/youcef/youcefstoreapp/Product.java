package com.finalproject.youcef.youcefstoreapp;

/**
 * Created by Youcef on 11/12/2016.
 */




//This class is called by the DataAvtivity class

public class Product {

    public String pName;
    public String price;

    public Product(){

    }

    //constructor for DataSnapshot.getValue
    public Product(String pName, String price) {
        this.pName = pName;
        this.price = price;
    }

}
