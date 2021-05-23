package com.escandiuzzi.contactsapp.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;

import com.escandiuzzi.contactsapp.Model.Contact;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.util.ArrayList;

public class DBSQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ContactsDB";
    private static final String DATABASE_TABLE = "contacts";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String PHONE = "phone";
    private static final String CELLPHONE = "cellphone";
    private static final String IMAGE = "image";

    private static final String[] COLUMNS = new String[] { ID, NAME, PHONE, CELLPHONE, IMAGE };

    public DBSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + DATABASE_TABLE +
                " ( " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME + " TEXT, " +
                PHONE + " TEXT, " +
                CELLPHONE + " TEXT, " +
                IMAGE + " BLOB)";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        this.onCreate(db);
    }

    public void addContact(Contact contact) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(NAME, contact.getName());
        contentValues.put(PHONE, contact.getPhone());
        contentValues.put(CELLPHONE, contact.getCellphone());
        contentValues.put(IMAGE, contact.getUserImageDB());

        sqLiteDatabase.insertOrThrow(DATABASE_TABLE, null, contentValues);
        sqLiteDatabase.close();
    }

    public Contact getContact(int id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(DATABASE_TABLE,
                COLUMNS,
                "id = ?",
                new String[] { String.valueOf(id)},
                null,
                null,
                null,
                null);

        if(cursor == null) {
            return null;
        } else {
            cursor.moveToFirst();
            Contact post = cursorToContact(cursor);
            sqLiteDatabase.close();

            return post;
        }
    }

    public ArrayList<Contact> getAllContacts() {

        ArrayList<Contact> contacts = new ArrayList<Contact>();

        String query = "SELECT * FROM " + DATABASE_TABLE + " ORDER BY " + NAME;

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Contact post = cursorToContact(cursor);
                contacts.add(post);
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();

        return contacts;
    }

    public int updateContact(Contact contact) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(NAME, contact.getName());
        contentValues.put(PHONE, contact.getPhone());
        contentValues.put(CELLPHONE, contact.getCellphone());
        contentValues.put(IMAGE, contact.getUserImageDB());

        int i = sqLiteDatabase.update(DATABASE_TABLE,
                contentValues,
                ID + " = ?",
                new String[] { String.valueOf(contact.getId()) });

        sqLiteDatabase.close();

        return i;
    }

    public int deleteContact(int id) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        int i = sqLiteDatabase.delete(DATABASE_TABLE,
                ID + " = ?",
                new String[] { String.valueOf(id) });

        sqLiteDatabase.close();

        return i;
    }

    private Contact cursorToContact(Cursor cursor) {
        Contact contact = new Contact();

        contact.setId(cursor.getInt(0));
        contact.setName(cursor.getString(1));
        contact.setPhone(cursor.getString(2));
        contact.setCellphone(cursor.getString(3));
        contact.setUserImage(BitmapHelper.getImage(cursor.getBlob(4)));

        return contact;
    }
}
