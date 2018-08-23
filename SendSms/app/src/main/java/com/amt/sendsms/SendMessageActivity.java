package com.amt.sendsms;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SendMessageActivity extends AppCompatActivity {
    String txtName, txtPhone;
    Button send;
    EditText text;
    RecyclerView recyclerView;
    ArrayList<SMSModelClass> contactsList = new ArrayList<>();
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        txtName = getIntent().getStringExtra("name");
        txtPhone = getIntent().getStringExtra("phone");
        getSupportActionBar().setTitle(txtName + " : " + txtPhone);

        send = findViewById(R.id.send);
        text = findViewById(R.id.text);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        checkPermission();
        send.setEnabled(false);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    send.setEnabled(false);
                } else if (count >= 1) {
                    send.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(txtPhone, null, text.getText().toString().trim(), null, null);
                Toast.makeText(SendMessageActivity.this, "Sent", Toast.LENGTH_SHORT).show();
                text.setText("");
                checkPermission();
            }
        });
    }

    private void showContacts() {

        Uri uri = Uri.parse("content://sms/");
        ContentResolver contentResolver = getContentResolver();
        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
        cursor = contentResolver.query(uri, projection, "address='" + txtPhone + "'", null, "date ASC");
        contactsList.clear();
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow("_id")).toString();
            String address = cursor.getString(cursor.getColumnIndexOrThrow("address")).toString();
//            String person = cursor.getString(cursor.getColumnIndexOrThrow("person")).toString();
            String body = cursor.getString(cursor.getColumnIndexOrThrow("body")).toString();
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date")).toString();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = formatter.format(new Date(Long.parseLong(date)));
            String type = cursor.getString(cursor.getColumnIndexOrThrow("type")).toString();
            //String phone = cursor.getString(cursor.getColumnIndexOrThrow("address")).toString();
            contactsList.add(new SMSModelClass(id, address, body, date, type));
        }
        recyclerView.setAdapter(new MessagesAdapter(contactsList, SendMessageActivity.this));
        cursor.close();
    }

    private void checkPermission() {
        Dexter.withActivity(SendMessageActivity.this)
                .withPermissions(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_SMS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            //Toast.makeText(mContext, "All permissions are granted!", Toast.LENGTH_SHORT).show();
                            showContacts();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(SendMessageActivity.this, "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
}