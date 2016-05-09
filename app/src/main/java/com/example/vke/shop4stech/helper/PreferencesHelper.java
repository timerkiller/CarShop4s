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

import com.example.vke.shop4stech.model.User;

/**
 * Easy storage and retrieval of preferences.
 */
public class PreferencesHelper {

    private static final String PLAYER_PREFERENCES = "playerPreferences";
    private static final String PREFERENCE_USER_NAME = PLAYER_PREFERENCES + ".firstName";
    private static final String PREFERENCE_PASSWORD = PLAYER_PREFERENCES + ".lastInitial";

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
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(PREFERENCE_USER_NAME, player.getUserName());
        editor.putString(PREFERENCE_PASSWORD, player.getPassword());
        editor.apply();
    }

    /**
     * Retrieves a {@link com.example.vke.shop4stech.model.User} from preferences.
     *
     * @param context The Context which to obtain the SharedPreferences from.
     * @return A previously saved player or <code>null</code> if none was saved previously.
     */
    public static User getUser(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        final String username = preferences.getString(PREFERENCE_USER_NAME, null);
        final String password = preferences.getString(PREFERENCE_PASSWORD, null);

        if (null == username || null == password ) {
            return null;
        }
        return new User(username, password);
    }

    /**
     * Signs out a player by removing all it's data.
     *
     * @param context The context which to obtain the SharedPreferences from.
     */
    public static void signOut(Context context) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.remove(PREFERENCE_USER_NAME);
        editor.remove(PREFERENCE_PASSWORD);
        editor.apply();
    }

    /**
     * Checks whether a player is currently signed in.
     *
     * @param context The context to check this in.
     * @return <code>true</code> if login data exists, else <code>false</code>.
     */
    public static boolean isSignedIn(Context context) {
        final SharedPreferences preferences = getSharedPreferences(context);
        return preferences.contains(PREFERENCE_USER_NAME) &&
                preferences.contains(PREFERENCE_PASSWORD);
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

    private static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.edit();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PLAYER_PREFERENCES, Context.MODE_PRIVATE);
    }
}
