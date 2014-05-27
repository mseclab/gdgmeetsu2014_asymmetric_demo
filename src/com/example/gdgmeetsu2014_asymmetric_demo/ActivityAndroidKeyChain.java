package com.example.gdgmeetsu2014_asymmetric_demo;

import java.io.File;
import java.io.FileInputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.X509Certificate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.security.KeyChain;
import android.security.KeyChainAliasCallback;
import android.security.KeyChainException;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.gdgmeetsu2014_asymmetric_demo.R;

public class ActivityAndroidKeyChain extends Activity implements
		OnClickListener, KeyChainAliasCallback {

	private static final String TAG = "ActivityAndroidKeyChain";

	private static final String PKCS12_FILENAME = "gdgmeetsu2014.p12";
	private static final String CA_CERT_FILENAME = "cacert.pem";

	private Button exit_Button;
	private Button mImportP12Button;
	private Button mUsePrivateKey;
	private EditText mInData;
	private EditText mOutData;
	private static TextView mDebugText;
	private static ScrollView mScrollView;

	private Object[] privpub = new Object[2];

	private boolean flag = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_androidkeychain);
		// Buttons
		exit_Button = (Button) findViewById(R.id.exit_button);
		exit_Button.setOnClickListener(this);
		mImportP12Button = (Button) findViewById(R.id.import_button);
		mImportP12Button.setOnClickListener(this);
		mUsePrivateKey = (Button) findViewById(R.id.useprivatekey_button);
		mUsePrivateKey.setOnClickListener(this);

		// Text View
		mInData = (EditText) findViewById(R.id.inDataText);
		mOutData = (EditText) findViewById(R.id.outDataText);
		mDebugText = (TextView) findViewById(R.id.debugText);
		mScrollView = (ScrollView) findViewById(R.id.scrollView);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.import_button:
			debug("Click on import");
			Intent intent = KeyChain.createInstallIntent();
			byte[] p12 = null;
			try {
				p12 = readFile(PKCS12_FILENAME);
			} catch (Exception e) {
				debug("Exception");
			}
			intent.putExtra(KeyChain.EXTRA_PKCS12, p12);
			startActivity(intent);
			flag = true;
			break;

		case R.id.useprivatekey_button:
			debug("Click on use key");
			if (flag == true)
				KeyChain.choosePrivateKeyAlias(this, this,
						new String[] { "RSA" }, null, null, -1, null);
			else
				debug("Import p12 file before");
			break;

		case R.id.exit_button:
			debug("Click on exit");
			finish();
			break;

		}
	}

	private byte[] readFile(String filename) throws Exception {
		File file = new File(Environment.getExternalStorageDirectory(),
				filename);
		byte[] result = new byte[(int) file.length()];
		FileInputStream in = new FileInputStream(file);
		in.read(result);
		in.close();
		return result;
	}

	@SuppressLint("NewApi")
	private void debug(String message) {
		mDebugText.append(message + "\n");
		Log.v(TAG, message);
		mScrollView.post(new Runnable() {
			public void run() {
				mScrollView.fullScroll(View.FOCUS_DOWN);
			}
		});
	}

	private void clearText() {
		mDebugText.setText("");
	}

	public void use_key() {
		boolean valid = false;
		PrivateKey private_key = null;
		PublicKey public_key = null;
		X509Certificate certificate = null;

		try {
			private_key = (PrivateKey) privpub[0];
			certificate = (X509Certificate) privpub[1];

			debug("Subject-DN: " + certificate.getSubjectDN().getName());
			debug("Issuer-DN: " + certificate.getIssuerDN().getName());

			public_key = certificate.getPublicKey();

			byte[] data = null;

			String textToSign = "GDG-MeetsU-Aquila-2014!";

			mInData.setText(textToSign);

			textToSign = mInData.getText().toString();

			debug("Text to sign : " + textToSign);

			data = textToSign.getBytes();

			byte[] base64_data = Base64.encode(data, Base64.DEFAULT);

			Signature signature = null;

			signature = Signature.getInstance("SHA1withRSA");

			signature.initSign(private_key);

			signature.update(base64_data);

			byte[] signed = null;

			signed = signature.sign();

			mOutData.setText(Base64.encode(signed, Base64.DEFAULT).toString());

			signature.initVerify(public_key);

			signature.update(base64_data);

			valid = signature.verify(signed);

			debug("Verified = " + valid);

		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void alias(final String alias) {
		// TODO Auto-generated method stub

		final Context context = ActivityAndroidKeyChain.this;
		Runnable r = new Runnable() {
			public void run() {
				new AsyncTask<Void, Void, Boolean>() {
					@Override
					protected Boolean doInBackground(Void... params) {
						try {
							privpub[0] = KeyChain.getPrivateKey(context, alias);
							X509Certificate[] chain = null;
							chain = KeyChain.getCertificateChain(context,
									"gdgmeetsu");
							privpub[1] = chain[0];
							return true;
						} catch (KeyChainException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return false;
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return false;
						}

					}

					@Override
					protected void onPostExecute(Boolean result) {
						// TODO Auto-generated method stub
						super.onPostExecute(result);
						if (result == true)
							use_key();
					}
				}.execute();
			}

		};
		runOnUiThread(r);
	}
}
