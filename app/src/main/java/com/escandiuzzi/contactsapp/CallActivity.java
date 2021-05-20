package com.escandiuzzi.contactsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.escandiuzzi.contactsapp.Helper.BitmapHelper;
import com.escandiuzzi.contactsapp.Model.Contact;
import com.github.chrisbanes.photoview.PhotoView;

public class CallActivity extends AppCompatActivity {

    private final static int REQUEST_CODE = 1;

    Button btnCall;

    TextView tvName;
    TextView tvPhone;
    TextView tvCellphone;

    PhotoView pvImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        btnCall = (Button) findViewById(R.id.btnCall);

        tvName = (TextView) findViewById(R.id.tvCallName);
        tvPhone = (TextView) findViewById(R.id.tvCallPhone);
        tvCellphone = (TextView) findViewById(R.id.tvCallCellphone);

        pvImage = (PhotoView) findViewById(R.id.pvCallPhoto);

        int id = getIntent().getIntExtra("ID", -1);

        String name =  getIntent().getStringExtra("Name");
        String phone =  getIntent().getStringExtra("Phone");
        String cellphone =  getIntent().getStringExtra("Cellphone");
        Bitmap photo = BitmapHelper.getImage(getIntent().getByteArrayExtra("Photo"));

        Contact contact = new Contact(id, name, phone, cellphone, photo);

        tvName.setText(contact.getName());
        tvPhone.setText(contact.getPhone());
        tvCellphone.setText(contact.getCellphone());
        pvImage.setImageBitmap(contact.getUserImage());


        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (view.getId() == R.id.btnCall &&
                        ActivityCompat.checkSelfPermission(CallActivity.this, Manifest.permission.CALL_PHONE)
                                != PackageManager.PERMISSION_GRANTED)
                {
                    Log.d("STATE", "Call Button DOES NOT WORK");
                    ActivityCompat.requestPermissions(CallActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);
                    return;
                }
                Log.d("STATE", "Call Button DOES WORK");
                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                phoneIntent.setData(Uri.parse("tel:" + contact.getCellphone()));
                startActivity(phoneIntent);
            }
        });
    }
}