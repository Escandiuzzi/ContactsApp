package com.escandiuzzi.contactsapp.Model;

import android.graphics.Bitmap;

import com.escandiuzzi.contactsapp.Helper.BitmapHelper;

import java.io.ByteArrayOutputStream;

public class Contact {

    int id;

    String name;
    String phone;
    String cellphone;

    Bitmap userImage;

    public Contact() { }

    public Contact(int id, String name, String phone, String cellphone, Bitmap userImage) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.cellphone = cellphone;
        this.userImage = userImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public Bitmap getUserImage() {
        return userImage;
    }

    public byte[] getUserImageDB() {

        if(userImage == null)
            return null;

        return BitmapHelper.getBitmapAsByteArray(userImage);
    }


    public void setUserImage(Bitmap userImage) {
        this.userImage = userImage;
    }
}
