package com.escandiuzzi.contactsapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.escandiuzzi.contactsapp.CallActivity;
import com.escandiuzzi.contactsapp.EditDeleteActivity;
import com.escandiuzzi.contactsapp.Model.Contact;
import com.escandiuzzi.contactsapp.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private final List<Contact> contacts;
    private final Context context;

    public ContactAdapter(Context context, List<Contact> contacts) {
        this.context = context;
        this.contacts = contacts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_layout, parent ,false);

        return new ViewHolder(view);
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.item_layout, parent, false);

        TextView tvLayoutName = (TextView) view.findViewById(R.id.tvNameItem);
        TextView tvLayoutPhone = (TextView) view.findViewById(R.id.tvPhoneItem);
        TextView tvLayoutCellphone = (TextView) view.findViewById(R.id.tvCellphoneItem);
        PhotoView pvLLayoutPhoto = (PhotoView) view.findViewById(R.id.pvUserItem);

        tvLayoutName.setText(contacts.get(position).getName());
        tvLayoutPhone.setText(contacts.get(position).getPhone());
        tvLayoutCellphone.setText(contacts.get(position).getCellphone());
        pvLLayoutPhoto.setImageBitmap(contacts.get(position).getUserImage());

        return rowView;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(contacts.get(position));
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvLayoutName;
        TextView tvLayoutPhone;
        TextView tvLayoutCellphone;
        PhotoView pvLayoutImage;

        public ViewHolder(View itemView) {
            super(itemView);

            tvLayoutName = (TextView) itemView.findViewById(R.id.tvNameItem);
            tvLayoutPhone= (TextView) itemView.findViewById(R.id.tvPhoneItem);
            tvLayoutCellphone =(TextView) itemView.findViewById(R.id.tvCellphoneItem);
            pvLayoutImage = (PhotoView) itemView.findViewById(R.id.pvUserItem);

            itemView.setOnClickListener(this);
        }

        private void setData(Contact contact){
            tvLayoutName.setText(contact.getName());
            tvLayoutPhone.setText(contact.getPhone());
            tvLayoutCellphone.setText(contact.getCellphone());
            pvLayoutImage.setImageBitmap(contact.getUserImage());
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), CallActivity.class);

            Contact contact = contacts.get(getLayoutPosition());

            intent.putExtra("ID", contact.getId());
            intent.putExtra("Name", contact.getName());
            intent.putExtra("Phone", contact.getPhone());
            intent.putExtra("Cellphone", contact.getCellphone());
            intent.putExtra("Photo", contact.getUserImageDB());


            v.getContext().startActivity(intent);
        }

    }
}
