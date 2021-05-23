package com.escandiuzzi.contactsapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.escandiuzzi.contactsapp.Helper.BitmapHelper;
import com.escandiuzzi.contactsapp.Helper.DBSQLiteHelper;
import com.escandiuzzi.contactsapp.Model.Contact;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditDeleteActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int GALLERY_INTENT = 2;

    Button btnTakePhoto;
    Button btnGallery;
    Button btnDelete;
    Button btnEdit;

    TextInputEditText tiName;
    TextInputEditText tiPhone;
    TextInputEditText tiCellphone;

    PhotoView pvImage;

    DBSQLiteHelper dbsqLiteHelper;

    Bitmap photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delete);

        btnEdit = (Button) findViewById(R.id.btnEdit);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnTakePhoto = (Button) findViewById(R.id.btnPhotoEdit);
        btnGallery = (Button) findViewById(R.id.btnGalleryEdit);

        tiName = (TextInputEditText) findViewById(R.id.tiNameEdit);
        tiPhone = (TextInputEditText) findViewById(R.id.tiPhoneEdit);
        tiCellphone = (TextInputEditText) findViewById(R.id.tiCellphoneEdit);

        pvImage = (PhotoView) findViewById(R.id.pvProfilePicEdit);

        int id = getIntent().getIntExtra("ID", -1);

        String name =  getIntent().getStringExtra("Name");
        String phone =  getIntent().getStringExtra("Phone");
        String cellphone =  getIntent().getStringExtra("Cellphone");
        photo = BitmapHelper.getImage(getIntent().getByteArrayExtra("Photo"));

        Contact contact = new Contact(id, name, phone, cellphone, photo);

        tiName.setText(contact.getName());
        tiPhone.setText(contact.getPhone());
        tiCellphone.setText(contact.getCellphone());
        pvImage.setImageBitmap(contact.getUserImage());

        dbsqLiteHelper = new DBSQLiteHelper(this);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String formattedPhone = PhoneNumberUtils.formatNumber(tiPhone.getText().toString(), "BR");
                String formattedCellphone = PhoneNumberUtils.formatNumber(tiCellphone.getText().toString(), "BR");

                contact.setName(tiName.getText().toString());
                contact.setPhone(formattedPhone);
                contact.setCellphone(formattedCellphone);
                contact.setUserImage(photo);

                dbsqLiteHelper.updateContact(contact);

                Intent intent = new Intent(EditDeleteActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbsqLiteHelper.deleteContact(contact.getId());

                Intent intent = new Intent(EditDeleteActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { takePhoto(v); }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            photo = (Bitmap) extras.get("data");
            pvImage.setImageBitmap(photo);
            try {
                createFile(photo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            Uri pickedImage = data.getData();
            photo = getBitmap(pickedImage, this.getContentResolver());
            pvImage.setImageBitmap(Bitmap.createScaledBitmap(photo, 200, 250, false));
        }

    }

    private Bitmap getBitmap(Uri file, ContentResolver cr) {

        Bitmap bitmap = null;

        try {
            InputStream inputStream = cr.openInputStream(file);
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException ex){}

        return bitmap;
    }

    private void takePhoto(View v) {
        Intent takePictureIntent = new
                Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            showAlert(getString(R.string.error), e.getMessage());
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_INTENT);
    }

    private void createFile(Bitmap photo) throws IOException {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

        String timeStamp = new
                SimpleDateFormat("yyyyMMdd_Hhmmss").format(
                new Date());
        File folder = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File file = new File(folder.getPath() + File.separator
                + "JPG_" + timeStamp + ".jpg");

        photo.compress(Bitmap.CompressFormat.PNG, 0, bytes);

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes.toByteArray());
        fos.flush();
        fos.close();

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);

        this.sendBroadcast(mediaScanIntent);
    }

    private void showAlert(String title, String message) {
        AlertDialog alertDialog = new
                AlertDialog.Builder(EditDeleteActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,
                getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}