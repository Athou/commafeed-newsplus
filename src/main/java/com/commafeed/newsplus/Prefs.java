package com.commafeed.newsplus;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.noinnion.android.reader.api.ExtensionPrefs;

public class Prefs extends ExtensionPrefs {

	public static final String KEY_SERVER = "server";
	public static final String KEY_USERNAME = "user_name";
	public static final String KEY_USERPASSWORD = "user_passwd";

	public static final String KEY_LOGGED_IN = "logged_in";

	public static boolean isLoggedIn(Context c) {
		return getBoolean(c, KEY_LOGGED_IN, false);
	}

	public static void setLoggedIn(Context c, boolean value) {
		putBoolean(c, KEY_LOGGED_IN, value);
	}

	public static void removeLoginData(Context c) {
		SharedPreferences sp = getPrefs(c);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(KEY_USERNAME);
		editor.remove(KEY_USERPASSWORD);
		editor.commit();
	}

	public static String getServer(Context c) {
		String server = getString(c, KEY_SERVER);
		return TextUtils.isEmpty(server) ? "https://www.commafeed.com" : server;
	}

	public static void setServer(Context c, String server) {
		putString(c, KEY_SERVER, server);
	}

	public static String getUserName(Context c) {
		return getString(c, KEY_USERNAME);
	}

	public static void setUserName(Context c, String userName) {
		putString(c, KEY_USERNAME, userName);
	}

	public static String getUserPassword(Context c) {
		return getString(c, KEY_USERPASSWORD);
	}

	public static void setUserPassword(Context c, String password) {
		putString(c, KEY_USERPASSWORD, password);
	}

}