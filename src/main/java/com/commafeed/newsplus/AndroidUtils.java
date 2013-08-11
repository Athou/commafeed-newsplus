package com.commafeed.newsplus;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class AndroidUtils {

	public static void showToast(final Context context, final CharSequence text) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			public void run() {
				if (context == null) {
					return;
				}
				Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
			}
		});
	}
}