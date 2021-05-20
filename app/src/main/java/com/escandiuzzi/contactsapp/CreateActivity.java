package com.escandiuzzi.contactsapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.escandiuzzi.contactsapp.Helper.DBSQLiteHelper;
import com.escandiuzzi.contactsapp.Helper.StringFormatter;
import com.escandiuzzi.contactsapp.Model.Contact;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateActivity extends AppCompatActivity {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int GALLERY_INTENT = 2;

    Button btnTakePhoto;
    Button btnInsert;
    Button btnGallery;

    TextView tvName;
    TextView tvPhone;
    TextView tvCellphone;

    PhotoView pvImage;

    DBSQLiteHelper dbsqLiteHelper;

    Bitmap photo;

    Boolean isUpdatingNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        btnInsert = (Button) findViewById(R.id.btnCreate);
        btnTakePhoto = (Button) findViewById(R.id.btnPhoto);
        btnGallery = (Button) findViewById(R.id.btnGallery);

        tvName = (TextView) findViewById(R.id.tiName);
        tvPhone = (TextView) findViewById(R.id.tiPhone);
        tvCellphone = (TextView) findViewById(R.id.tiCellphone);

        pvImage = (PhotoView) findViewById(R.id.pvProfilePic);

        dbsqLiteHelper = new DBSQLiteHelper(this);

        verifyStoragePermissions(CreateActivity.this);

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto(v);
            }
        });

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Contact contact = new Contact();
                contact.setName(tvName.getText().toString());
                contact.setPhone(tvPhone.getText().toString());
                contact.setCellphone(tvCellphone.getText().toString());
                contact.setUserImage(photo);

                dbsqLiteHelper.addContact(contact);

                Intent intent = new Intent(CreateActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
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
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_INTENT);
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
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

    private void showPhoto(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        pvImage.setImageBitmap(bitmap);
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
                AlertDialog.Builder(CreateActivity.this).create();
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
