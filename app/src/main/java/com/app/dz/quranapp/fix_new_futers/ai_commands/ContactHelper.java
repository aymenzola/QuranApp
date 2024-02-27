package com.app.dz.quranapp.fix_new_futers.ai_commands;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

public class ContactHelper {

    public static void makePhoneCall(Context context, String contactName) {
        // Find the contact by name
        String contactNumber = getContactNumber(context, contactName);

        if (contactNumber != null && !contactNumber.isEmpty()) {
            // Make a phone call
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactNumber));
            context.startActivity(intent);
        }
    }

    private static String getContactNumber(Context context, String contactName) {
        Log.e("steptag", "searching for " + contactName);
        String phoneNumber = null;
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = {Phone.NUMBER};

        // Convert contactName to lowercase for case-insensitive comparison
        contactName = contactName.toLowerCase();

        String selection = "LOWER(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") = ?";
        String[] selectionArgs = {contactName};
        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int numberColumnIndex = cursor.getColumnIndex(Phone.NUMBER);
                phoneNumber = cursor.getString(numberColumnIndex);
            }
            cursor.close();
        }

        Log.e("steptag", "phoneNumber is " + phoneNumber);
        return phoneNumber;
    }

    public static void viewContactWithName(Context context, String contactName) {
        Uri uri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri.encode(contactName));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static void viewContactWithNumber(Context context, String contactName) {
        String contactNumber = getContactNumber(context, contactName);

        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + contactNumber));
        context.startActivity(callIntent);
    }



}
