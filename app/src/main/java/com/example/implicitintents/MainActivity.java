package com.example.implicitintents;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_SELECT_PHONE_NUMBER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickOpenWebsiteButton(View v){
        String url = "https://github.com/keldavis";
        openWebsite(url);
    }

    public void onClickOpenMapLocationButton(View v){
        String address = "85 10th Ave, New York, NY 10011";

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("geo")
                .path("0,0")
                .appendQueryParameter("q", address);
        Uri addressUri = builder.build();
        openMap(addressUri);
    }

    public void onClickShareTextButton(View v){
        String text =
                "Recap of Day 33 of #100DaysOfCode: I played around with implicit intents " +
                        "for #AndroidDev";

        shareText(text);
    }

    public void onClickMakeCallButton(View v){
        String phoneNumber = "tel:2296711027";
        makeCall(phoneNumber);
    }

    public void onClickSendEmailButton(View view) {
        String[] emails = new String[]{"lshame28@yahoo.com", "lshame28@gmail.com"};
        String subject = "Implicit Intents are fun";
        sendEmail(emails, subject);
    }

    public void onClickSelectContactPhoneNumber(View view) {
        selectContact();
    }

    private void selectContact() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_SELECT_PHONE_NUMBER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Get the URI and query the content provider for the phone number
        Uri contactUri = data.getData();
        if (requestCode == REQUEST_SELECT_PHONE_NUMBER && resultCode == RESULT_OK){
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor cursor = getContentResolver().query(contactUri, projection,
                    null, null, null);

            // If the cursor returned is valid, get the phone number
            if(cursor != null && cursor.moveToFirst()){
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds
                .Phone.NUMBER);
                String number = cursor.getString(numberIndex);
                shareText(number);
            }
        }
    }

    public void sendEmail(String[] emails, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, emails);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, "Implicit intents do not declare the class name of " +
                "the component to start, but instead declare an action to perform. The action " +
                "specifies the thing you want to do, such as view, edit, send, or get something.");

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
    }

    private void makeCall(String phoneNumber) {
        Uri number = Uri.parse(phoneNumber);
        Intent intent = new Intent(Intent.ACTION_DIAL, number);
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
    }

    private void shareText(String text){
        /*
         * You can think of MIME types similarly to file extensions. They aren't the exact same,
         * but MIME types help a computer determine which applications can open which content. For
         * example, if you double click on a .pdf file, you will be presented with a list of
         * programs that can open PDFs. Specifying the MIME type as text/plain has a similar affect
         * on our implicit Intent. With text/plain specified, all apps that can handle text content
         * in some way will be offered when we call startActivity on this particular Intent.
         */
        String mimeType = "text/plain";

        // Title of the window that will pop up
        String chooserTitle = "Sharing is caring";

        /* ShareCompat.IntentBuilder provides a fluent API for creating Intents */
        ShareCompat.IntentBuilder
                // The context from which the share is coming from
                .from(this)
                .setType(mimeType)
                .setChooserTitle(chooserTitle)
                .setText(text)
                .startChooser();
    }

    private void openMap(Uri location){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(location);
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
    }

    /**
     * This method creates an implicit intent to open a website
     *
     * @param url Url of website to open. Should start with http:// or https:// as that is the
     *            scheme of the URI expected with this Intent according to the Common Intents page
     */
    private void openWebsite(String url){
        Uri website = Uri.parse(url);

        /*
         * Create the Intent with the action of ACTION_VIEW. This action allows the user
         * to view particular content.
         */
        Intent intent = new Intent(Intent.ACTION_VIEW, website);

        /*
         * This is a check we perform with every implicit Intent that we launch. In some cases,
         * the device where this code is running might not have an Activity to perform the action
         * with the data we've specified. Without this check, in those cases your app would crash.
         */
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
    }

    public void onClickAddCalendarEventButton(View view) {
        String title = "Game of Thrones Premiere";
        String location = "1100 Avenue Of The Americas " +
                "New York, New York 10036";
        String startDate = "APR 14 2019 20:00:00.000 EST";
        String endDate = "APR 14 2019 22:00:00.000 EST";
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");
        Date dateStart = null;
        Date dateEnd = null;
        try {
            dateStart = simpleDateFormat.parse(startDate);
            dateEnd = simpleDateFormat.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long begin = dateStart.getTime();
        long end = dateEnd.getTime();
        addEvent(title, location, begin, end);
    }

    private void addEvent(String title, String location, long begin, long end) {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end);

        if (intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
    }
}
