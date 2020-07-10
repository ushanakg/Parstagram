package com.example.parstagram;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

@ParseClassName("Post")
public class Post extends ParseObject {

    private static final String TAG = "Post";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_USER = "user";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_LIKED_BY = "likedBy";
    public static final String KEY_COMMENTS = "comments";

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

    private ArrayList<String> getLikes() {
        return (ArrayList<String>) get(KEY_LIKED_BY);
    }

    // return true if liked and false if disliked
    public boolean toggleLike(ParseUser user) {
        boolean liked;
        ArrayList<String> lst = getLikes();
        if (likedBy(user)) {
            lst.remove(user.getUsername());
            liked = false;
        } else {
            if (lst == null) {
                lst = new ArrayList<>();
            }
            lst.add(user.getUsername());
            liked = true;
        }
        put(KEY_LIKED_BY, lst);
        return liked;
    }

    public ArrayList<String> getComments() {
        ArrayList<String> lst = (ArrayList<String>) get(KEY_COMMENTS);
        if (lst == null) {
            return new ArrayList<>();
        }
        return lst;
    }

    public void addComment(ParseUser user, String comment) {
        ArrayList<String> lst = getComments();
        if (lst == null) {
            lst = new ArrayList<>();
        }
        lst.add(user.getUsername() + ": " + comment);
        put(KEY_COMMENTS, lst);
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

    public int getNumLikes() {
        ArrayList<String> lst = getLikes();
        if (lst == null) {
            return 0;
        }
        return lst.size();
    }

    public boolean likedBy(ParseUser user) {
        ArrayList<String> lst = getLikes();
        if (lst == null) {
            return false;
        }
        return lst.contains(user.getUsername());
    }
}
