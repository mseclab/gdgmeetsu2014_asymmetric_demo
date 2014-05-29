package com.example.gdgmeetsu2014_asymmetric_demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.example.gdgmeetsu2014_asymmetric_demo.R;

public class MainActivity extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button GenKeyKeyStoreCifraDecifra = (Button) this
				.findViewById(R.id.androidkeystoreencrypt);
		GenKeyKeyStoreCifraDecifra.setOnClickListener(this);

		Button GenerateRSAKey = (Button) this.findViewById(R.id.generatersakey);
		GenerateRSAKey.setOnClickListener(this);

		Button AndroidKeyChain = (Button) this
				.findViewById(R.id.androidkeychain);
		AndroidKeyChain.setOnClickListener(this);

		Button AndroidKeyStoreGenerateKey = (Button) this
				.findViewById(R.id.androidkeystoregeneratekey);
		AndroidKeyStoreGenerateKey.setOnClickListener(this);

		Button AndroidKeyStoreSign = (Button) this
				.findViewById(R.id.androidkeystoresign);
		AndroidKeyStoreSign.setOnClickListener(this);

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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void startActivityAndroidKeyStoreEncrypt(View v) {
		Intent intent = new Intent(MainActivity.this,
				ActivityAndroidKeyStoreEncrypt.class);
		startActivityForResult(intent, 1);
	}

	public void startGenerateRSAKey(View v) {
		Intent intent = new Intent(MainActivity.this,
				ActivityGenerateRSAKey.class);
		startActivityForResult(intent, 1);
	}

	public void startAndroidKeyChain(View v) {
		Intent intent = new Intent(MainActivity.this,
				ActivityAndroidKeyChain.class);
		startActivityForResult(intent, 1);
	}

	public void startAndroidKeyStoreGenerateKey(View v) {
		Intent intent = new Intent(MainActivity.this,
				ActivityAndroidKeyStoreGenerateKey.class);
		startActivityForResult(intent, 1);
	}

	public void startAndroidKeyStoreSign(View v) {
		Intent intent = new Intent(MainActivity.this,
				ActivityAndroidKeyStoreSign.class);
		startActivityForResult(intent, 1);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.generatersakey:
			startGenerateRSAKey(v);
			break;
		case R.id.androidkeystoregeneratekey:
			startAndroidKeyStoreGenerateKey(v);
			break;
		case R.id.androidkeystoresign:
			startAndroidKeyStoreSign(v);
			break;
		
		case R.id.androidkeystoreencrypt:
			startActivityAndroidKeyStoreEncrypt(v);
			break;
		case R.id.androidkeychain:
			startAndroidKeyChain(v);
			break;
		default:
			break;
		}

	}

}
