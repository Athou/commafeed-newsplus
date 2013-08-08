package com.commafeed.newsplus;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.noinnion.android.reader.api.util.Utils;

public class WelcomeActivity extends Activity implements View.OnClickListener {

	public static final String TAG = "WelcomeActivity";

	public static final String NEWSPLUS_PACKAGE = "com.noinnion.android.newsplus";
	public static final String NEWSPLUS_PRO_PACKAGE = "com.noinnion.android.newspluspro";

	private String appPackage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		initButton();
	}

	private void initButton() {
		boolean installed = Utils.appInstalledOrNot(this, NEWSPLUS_PACKAGE);
		if (installed) {
			appPackage = NEWSPLUS_PACKAGE;
		} else {
			installed = Utils.appInstalledOrNot(this, NEWSPLUS_PRO_PACKAGE);
			if (installed) {
				appPackage = NEWSPLUS_PRO_PACKAGE;
			}
		}

		Button button = (Button) findViewById(R.id.btn_ok);
		button.setText(installed ? R.string.txt_start_app : R.string.txt_download_app);
		button.setEnabled(true);
		button.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok:
			if (TextUtils.isEmpty(appPackage)) {
				Utils.startMarketApp(this, NEWSPLUS_PACKAGE);
			} else {
				Utils.startAppPackage(this, appPackage);
				finish();
			}
			break;
		}
	}

}
