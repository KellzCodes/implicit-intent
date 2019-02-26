package com.example.implicitintents;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

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
                "Day 33 of #100DaysOfCode: I am playing around with implicit intents " +
                        "for #AndroidDev";

        shareText(text);
    }

    public void onClickMakeCallButton(View v){
        String phoneNumber = "tel:2296711027";
        makeCall(phoneNumber);
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
}
