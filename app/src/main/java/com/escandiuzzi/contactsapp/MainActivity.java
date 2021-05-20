package com.escandiuzzi.contactsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;

import com.escandiuzzi.contactsapp.Adapter.ContactAdapter;
import com.escandiuzzi.contactsapp.Helper.DBSQLiteHelper;
import com.escandiuzzi.contactsapp.Helper.RecyclerViewSwipeDecorator;
import com.escandiuzzi.contactsapp.Model.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    FloatingActionButton floatingActionButton;

    DBSQLiteHelper dbsqLiteHelper;

    ContactAdapter adapter;

    ArrayList<Contact> contacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateActivity.class);
                startActivity(intent);
            }
        });

        dbsqLiteHelper = new DBSQLiteHelper(this);

        ///Clear DB
        //dbsqLiteHelper.onUpgrade(dbsqLiteHelper.getReadableDatabase(), 0,1);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }


    @Override
    protected void onStart() {
        super.onStart();

        contacts = dbsqLiteHelper.getAllContacts();
        adapter = new ContactAdapter(this.getBaseContext(), contacts);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.RIGHT:
                    swipeLeftToRight(viewHolder, position);
                    break;
                case ItemTouchHelper.LEFT:
                    swipeRightToLeft(viewHolder, position);
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorBlue))
                    .addSwipeLeftActionIcon(R.drawable.outline_edit_white_18)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorRed))
                    .addSwipeRightActionIcon(R.drawable.outline_delete_white_18)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void swipeRightToLeft(RecyclerView.ViewHolder viewHolder, int position) {
            Intent intent = new Intent(MainActivity.this, EditDeleteActivity.class);

            Contact contact = contacts.get(position);

            intent.putExtra("ID", contact.getId());
            intent.putExtra("Name", contact.getName());
            intent.putExtra("Phone", contact.getPhone());
            intent.putExtra("Cellphone", contact.getCellphone());
            intent.putExtra("Photo", contact.getUserImageDB());

            startActivity(intent);
    }

    private void swipeLeftToRight(RecyclerView.ViewHolder viewHolder, int position) {
        new AlertDialog.Builder(viewHolder.itemView.getContext()).setMessage(R.string.confirmDelete)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbsqLiteHelper.deleteContact(contacts.get(position).getId());
                        contacts.remove(position);
                        adapter.notifyItemRemoved(position);
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.notifyItemChanged(viewHolder.getAdapterPosition());
            }
        }).create().show();
    }

}
