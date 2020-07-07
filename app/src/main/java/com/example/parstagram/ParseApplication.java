package com.example.parstagram;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Register your parse models
        ParseObject.registerSubclass(Post.class);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("ushana-parstagram") // should correspond to APP_ID env variable
                .clientKey("FBU2020CodePathParstagram")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://ushana-parstagram.herokuapp.com/parse/").build());
    }
}
