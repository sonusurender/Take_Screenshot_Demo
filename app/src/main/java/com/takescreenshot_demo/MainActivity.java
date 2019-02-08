package com.takescreenshot_demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button fullPageScreenshot, customPageScreenshot;
    private LinearLayout rootContent;
    private ImageView imageView;
    private TextView hiddenText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        implementClickEvents();
    }

    /*  Find all views Ids  */
    private void findViews() {
        fullPageScreenshot = (Button) findViewById(R.id.full_page_screenshot);
        customPageScreenshot = (Button) findViewById(R.id.custom_page_screenshot);

        rootContent = (LinearLayout) findViewById(R.id.root_content);

        imageView = (ImageView) findViewById(R.id.image_view);

        hiddenText = (TextView) findViewById(R.id.hidden_text);
    }

    /*  Implement Click events over Buttons */
    private void implementClickEvents() {
        fullPageScreenshot.setOnClickListener(this);
        customPageScreenshot.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.full_page_screenshot:
                takeScreenshot(ScreenshotType.FULL);
                break;
            case R.id.custom_page_screenshot:
                takeScreenshot(ScreenshotType.CUSTOM);
                break;
        }
    }

    /*  Method which will take screenshot on Basis of Screenshot Type ENUM  */
    private void takeScreenshot(ScreenshotType screenshotType) {
        Bitmap b = null;
        switch (screenshotType) {
            case FULL:
                //If Screenshot type is FULL take full page screenshot i.e our root content.
                b = ScreenshotUtils.getScreenShot(rootContent);
                break;
            case CUSTOM:
                //If Screenshot type is CUSTOM

                fullPageScreenshot.setVisibility(View.INVISIBLE);//set the visibility to INVISIBLE of first button
                hiddenText.setVisibility(View.VISIBLE);//set the visibility to VISIBLE of hidden text

                b = ScreenshotUtils.getScreenShot(rootContent);

                //After taking screenshot reset the button and view again
                fullPageScreenshot.setVisibility(View.VISIBLE);//set the visibility to VISIBLE of first button again
                hiddenText.setVisibility(View.INVISIBLE);//set the visibility to INVISIBLE of hidden text

                //NOTE:  You need to use visibility INVISIBLE instead of GONE to remove the view from frame else it wont consider the view in frame and you will not get screenshot as you required.
                break;
        }

        //If bitmap is not null
        if (b != null) {
            showScreenShotImage(b);//show bitmap over imageview

            File saveFile = ScreenshotUtils.getMainDirectoryName(this);//get the path to save screenshot
            File file = ScreenshotUtils.store(b, "screenshot" + screenshotType + ".jpg", saveFile);//save the screenshot to selected path
            shareScreenshot(file);//finally share screenshot
        } else
            //If bitmap is null show toast message
            Toast.makeText(this, R.string.screenshot_take_failed, Toast.LENGTH_SHORT).show();

    }

    /*  Show screenshot Bitmap */
    private void showScreenShotImage(Bitmap b) {
        imageView.setImageBitmap(b);
    }

    /*  Share Screenshot  */
    private void shareScreenshot(File file) {
        Uri uri = Uri.fromFile(file);//Convert file path into Uri for sharing
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.sharing_text));
        intent.putExtra(Intent.EXTRA_STREAM, uri);//pass uri here
        startActivity(Intent.createChooser(intent, getString(R.string.share_title)));
    }

}
