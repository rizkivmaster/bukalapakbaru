package com.example.bukalapakdummy;

import listener.APIListener;
import model.business.Credential;
import model.business.CredentialEditor;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import api.Authenticate;
import com.bukalapakdummy.R;

public class LoginActivity extends Activity {
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		Credential credential = CredentialEditor.loadCredential(context);
		if (credential != null) {
			startActivity(new Intent(LoginActivity.this,
					ResponsiveUIActivity.class));
			finish();
		} else {
			setContentView(R.layout.login);
			Button submitBtn = (Button) findViewById(R.id.btnLogin);
			submitBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					EditText u = (EditText) findViewById(R.id.login_email_username);
					EditText p = (EditText) findViewById(R.id.login_password);
					String username = u.getText().toString();
					String password = p.getText().toString();
					Credential credential = new Credential(username, password);
					final Authenticate auth = new Authenticate(context,
							credential);
					auth.setAPIListener(new APIListener<Credential>() {
						ProgressDialog pd;

						@Override
						public void onSuccess(Credential res) {
							pd.dismiss();
							CredentialEditor.saveCredential(res, context);
							startActivity(new Intent(LoginActivity.this,
									ResponsiveUIActivity.class));
							finish();
						}

						@Override
						public void onFailure(Exception e) {
							pd.dismiss();
							Toast.makeText(LoginActivity.this, e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onExecute() {
							pd = new ProgressDialog(context);
							pd.setTitle("Login");

							pd.setMessage("Tunggu sebentar, sedang otorisasi...");

							pd.setCancelable(true);
							pd.setOnCancelListener(new OnCancelListener() {

								@Override
								public void onCancel(DialogInterface dialog) {
									auth.cancel();
									finish();
								}
							});

							pd.setIndeterminate(false);
							pd.show();
						}
					}).execute();

				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);

		return true;
	}

}
