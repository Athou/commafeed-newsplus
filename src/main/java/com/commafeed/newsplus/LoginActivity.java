package com.commafeed.newsplus;

import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.noinnion.android.reader.api.ReaderException;
import com.noinnion.android.reader.api.ReaderExtension;
import com.noinnion.android.reader.api.util.Utils;

public class LoginActivity extends FragmentActivity implements OnClickListener, OnEditorActionListener {

	private ProgressDialog busyDialog;

	private TextView serverText;
	private TextView userNameText;
	private TextView passwordText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Context c = getApplicationContext();

		String action = getIntent().getAction();
		if (action != null && action.equals(ReaderExtension.ACTION_LOGOUT)) {
			logout();
		}

		if (Prefs.isLoggedIn(c)) {
			setResult(RESULT_OK);
			finish();
		}
		setContentView(R.layout.login);
		setTitle(R.string.txt_login);

		serverText = (TextView) findViewById(R.id.edit_server);
		userNameText = (TextView) findViewById(R.id.edit_login_id);
		userNameText.requestFocus();
		userNameText.setOnEditorActionListener(this);
		passwordText = (TextView) findViewById(R.id.edit_password);
		passwordText.setOnEditorActionListener(this);

		String server = Prefs.getServer(c);
		if (server != null)
			serverText.setText(server);

		String userName = Prefs.getUserName(c);
		if (userName != null)
			userNameText.setText(userName);

		findViewById(R.id.btn_login).setOnClickListener(this);
		findViewById(R.id.btn_cancel).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_login:
			login();
			break;
		case R.id.btn_cancel:
			finish();
			break;
		}
	}

	@Override
	public boolean onEditorAction(TextView view, int actionId, KeyEvent e) {
		login();
		return true;
	}

	private void login() {
		String server = serverText.getText().toString();
		String userName = userNameText.getText().toString();
		String password = passwordText.getText().toString();
		if (server.length() == 0 || userName.length() == 0 || password.length() == 0) {
			Utils.showToast(this, getText(R.string.msg_login_fail));
		} else {
			new SaveInputLoginTask().execute(server, userName, password);
		}
	}

	private void logout() {
		final Context c = getApplicationContext();
		Prefs.setLoggedIn(c, false);
		Prefs.removeLoginData(c);
		setResult(ReaderExtension.RESULT_LOGOUT);
		finish();
	}

	private class SaveInputLoginTask extends AsyncTask<String, Void, Boolean> {

		protected void onPreExecute() {
			busyDialog = ProgressDialog.show(LoginActivity.this, null, getText(R.string.msg_login_running), true, true);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			String server = params[0];
			String userName = params[1];
			String password = params[2];

			Context c = getApplicationContext();
			Prefs.setServer(c, server);
			Prefs.setUserName(c, userName);
			Prefs.setUserPassword(c, password);
			CommaFeedExtension client = new CommaFeedExtension(c);
			try {
				if (client.ping()) {
					return true;
				} else {
					Utils.showToast(LoginActivity.this, getText(R.string.msg_login_fail));
				}
			} catch (IOException e) {
				e.printStackTrace();
				Utils.showToast(LoginActivity.this, getText(R.string.err_io) + " (" + e.getLocalizedMessage() + ")");
			} catch (ReaderException e) {
				e.printStackTrace();
				Utils.showToast(LoginActivity.this, getText(R.string.msg_login_fail));
			} catch (Throwable e) {
				e.printStackTrace();
				Utils.showToast(LoginActivity.this, e.getLocalizedMessage());
			}
			return null;
		}

		protected void onPostExecute(Boolean result) {
			Context c = getApplicationContext();
			try {
				if (busyDialog != null && busyDialog.isShowing())
					busyDialog.dismiss();
			} catch (Exception e) {
			}
			if (result != null)
				processLogin();
			else
				Prefs.setLoggedIn(c, false);
		}

		private void processLogin() {
			Context c = getApplicationContext();
			Prefs.setLoggedIn(c, true);
			setResult(ReaderExtension.RESULT_LOGIN);
			finish();
		}
	}

}