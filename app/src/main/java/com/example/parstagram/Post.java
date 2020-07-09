package com.example.parstagram;

import android.content.Context;
import android.icu.text.RelativeDateTimeFormatter;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@ParseClassName("Post")
public class Post extends ParseObject {

    private static final String TAG = "Post";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_USER = "user";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_CREATED_AT = "createdAt";

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser) {
        put(KEY_USER, parseUser);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }


    public String getTimePosted() {
        String format = "H:mm";
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.ENGLISH);
        df.setLenient(true);

        return df.format(getCreatedAt());
    }

    public String getDatePosted() {
        String dateFormat = "MMMM d";
        SimpleDateFormat df = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        df.setLenient(true);

        return df.format(getCreatedAt());

    }

    // Get how long ago the tweet was posted
    public String getRelativeTimeAgo() {
        long dateMillis = getCreatedAt().getTime();

        return DateUtils.getRelativeTimeSpanString (dateMillis, System.currentTimeMillis(),0L).toString();
    }

}
