/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.vke.shop4stech.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.example.vke.shop4stech.model.LoginUIAccountInfo;
import com.example.vke.shop4stech.model.User;

/**
 * Easy storage and retrieval of preferences.
 */
public class PreferencesHelper {
    private static final String mTag = "PreferencesHelper";

    private static final String PLAYER_PREFERENCES = "playerPreferences";
    private static final String PREFERENCE_USER_NAME = PLAYER_PREFERENCES + ".userName";
    private static final String PREFERENCE_PASSWORD = PLAYER_PREFERENCES + ".password";
    private static final String PREFERENCE_ACCESS_TOKEN= PLAYER_PREFERENCES + ".accessToken";

    private static final String UI_ACCOUNT_INFO_PREFERENCES = "uiInfoPreferences";
    private static final String PREFERENCE_UI_USER_NAME = UI_ACCOUNT_INFO_PREFERENCES + ".userName";
    private static final String PREFERENCE_UI_PASSWORD = UI_ACCOUNT_INFO_PREFERENCES + ".password";
    private static final String PREFERENCE_REMEMBER_ACCOUNT=UI_ACCOUNT_INFO_PREFERENCES +".rememberAccount";

    private PreferencesHelper() {
        //no instance
    }

    /**
     * Writes a {@link com.example.vke.shop4stech.model.User} to preferences.
     *
     * @param context The Context which to obtain the SharedPreferences from.
     * @param player  The {@link com.example.vke.shop4stech.model.User} to write.
     */
    public static void writeToPreferences(Context context, User player) {
        SharedPreferences.Editor editor = getEditor(context,PLAYER_PREFERENCES);
        editor.putString(PREFERENCE_USER_NAME, player.getUserName());
        editor.putString(PREFERENCE_PASSWORD, player.getPassword());
        editor.putString(PREFERENCE_ACCESS_TOKEN,player.getAccessToken());
        editor.apply();
    }

    /**
     * Writes a UI info data to preferences.
     *
     * @param context The Context which to obtain the SharedPreferences from.
     * @param uiAccountInfo  The userName to write.
     */
    public static void writeUiInfoToPreferences(Context context, LoginUIAccountInfo uiAccountInfo ) {
        SharedPreferences.Editor editor = getEditor(context,UI_ACCOUNT_INFO_PREFERENCES);
        editor.putString(PREFERENCE_UI_USER_NAME, uiAccountInfo.getUserName());
        editor.putString(PREFERENCE_UI_PASSWORD, uiAccountInfo.getPassword());
        editor.putBoolean(PREFERENCE_REMEMBER_ACCOUNT,uiAccountInfo.getRememberFlag());
        editor.apply();
    }

    /**
     * Retrieves a {@link com.example.vke.shop4stech.model.User} from preferences.
     *
     * @param context The Context which to obtain the SharedPreferences from.
     * @return A previously saved player or <code>null</code> if none was saved previously.
     */
    public static User getUser(Context context) {
        SharedPreferences preferences = getSharedPreferences(context,PLAYER_PREFERENCES);
        final String username = preferences.getString(PREFERENCE_USER_NAME, null);
        final String password = preferences.getString(PREFERENCE_PASSWORD, null);
        final String accessToken = preferences.getString(PREFERENCE_ACCESS_TOKEN,null);
        if (null == username || null == password || accessToken == null) {
            Log.w(mTag,"get User is null");
            return null;
        }

        return new User(username, password,accessToken);
    }

    /**
     * Retrieves a {@link com.example.vke.shop4stech.model.User} from preferences.
     *
     * @param context The Context which to obtain the SharedPreferences from.
     * @return 1.accessToken,2,null
     */
    public static String getPreferenceAccessToken(Context context){
        SharedPreferences preferences = getSharedPreferences(context,PLAYER_PREFERENCES);
        final String accessToken = preferences.getString(PREFERENCE_ACCESS_TOKEN,null);
        if (accessToken == null){
            return null;
        }
        return accessToken;
    }

    public static String getPreferenceUserName(Context context){
        SharedPreferences preferences = getSharedPreferences(context,PLAYER_PREFERENCES);
        final String username = preferences.getString(PREFERENCE_USER_NAME,null);
        if (username == null){
            return null;
        }
        return username;
    }

    public static LoginUIAccountInfo getUIAccountInfo(Context context){
        SharedPreferences preferences = getSharedPreferences(context,UI_ACCOUNT_INFO_PREFERENCES);
        final String username = preferences.getString(PREFERENCE_UI_USER_NAME, null);
        final String password = preferences.getString(PREFERENCE_UI_PASSWORD, null);
        final boolean flag = preferences.getBoolean(PREFERENCE_REMEMBER_ACCOUNT,false);
        if (null == username || null == password ) {
            return null;
        }

        return new LoginUIAccountInfo(username, password,flag);
    }

    /**
     * Signs out a player by removing all it's data.
     *
     * @param context The context which to obtain the SharedPreferences from.
     */
    public static void signOut(Context context) {
        SharedPreferences.Editor editor = getEditor(context,PLAYER_PREFERENCES);
        editor.remove(PREFERENCE_USER_NAME);
        editor.remove(PREFERENCE_PASSWORD);
        editor.remove(PREFERENCE_ACCESS_TOKEN);
        editor.apply();
    }


    /**
     * Checks whether a player is currently signed in.
     *
     * @param context The context to check this in.
     * @return <code>true</code> if login data exists, else <code>false</code>.
     */
    public static boolean isSignedIn(Context context) {
        final SharedPreferences preferences = getSharedPreferences(context,PLAYER_PREFERENCES);
        return preferences.contains(PREFERENCE_USER_NAME) &&
                preferences.contains(PREFERENCE_PASSWORD) &&
                preferences.contains(PREFERENCE_ACCESS_TOKEN);
    }

    /**
     * Checks whether the player's input data is valid.
     *
     * @param firstName   The player's first name to be examined.
     * @param lastInitial The player's last initial to be examined.
     * @return <code>true</code> if both strings are not null nor 0-length, else <code>false</code>.
     */
    public static boolean isInputDataValid(CharSequence firstName, CharSequence lastInitial) {
        return !TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastInitial);
    }

    private static SharedPreferences.Editor getEditor(Context context,final String preference ) {
        SharedPreferences preferences = getSharedPreferences(context,preference);
        return preferences.edit();
    }

    private static SharedPreferences getSharedPreferences(Context context,final String preferences) {
        return context.getSharedPreferences(preferences, Context.MODE_PRIVATE);
    }
}
