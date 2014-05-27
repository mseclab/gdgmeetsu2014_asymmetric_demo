package com.example.gdgmeetsu2014_asymmetric_demo;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.gdgmeetsu2014_asymmetric_demo.R;

public class ActivityAndroidKeyStoreEncrypt extends Activity  {

	private final static String ALIAS = "DEVKEY1";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_androidkeystoreencrypt);
		
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
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
	
	
	 /* A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements
			View.OnClickListener {

		private Button exit_Button;
		private Button mGenChiaviButton;
		private Button mCifraButton;
		private Button mDecifraButton;
		private TextView mDebugText;
		private EditText mInData;
		private EditText mOutData;
		private ScrollView mScrollView;

		ProgressDialog progressdialog;

		private static final String SIGN_ALG = "SHA256withRSA";
		private static final String TAG = "AndroidKeyStoreDemo";
		private boolean flag = false;
		
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_androidkeystoreencrypt, container,
					false);

			// Bottoni
			exit_Button = (Button) rootView.findViewById(R.id.exit_button);
			exit_Button.setOnClickListener(this);
			mGenChiaviButton = (Button) rootView
					.findViewById(R.id.generate_button);
			mGenChiaviButton.setOnClickListener(this);
			mCifraButton = (Button) rootView.findViewById(R.id.cifra_button);
			mCifraButton.setOnClickListener(this);
			mDecifraButton = (Button) rootView
					.findViewById(R.id.decifra_button);
			mDecifraButton.setOnClickListener(this);

			// Text View
			mInData = (EditText) rootView.findViewById(R.id.inDataText);
			mOutData = (EditText) rootView.findViewById(R.id.outDataText);
			mDebugText = (TextView) rootView.findViewById(R.id.debugText);
			mScrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
			return rootView;
		}

		@Override
		public void onClick(View view) {

			switch (view.getId()) {
			case R.id.exit_button:
				debug("Click on exit");
				this.getActivity().finish();
				break;
			case R.id.generate_button:
				debug("Click on gen keys");
				generaChiavi();
				break;
			case R.id.cifra_button:
				debug("Click on encrypt");
				cifraData();
				break;
			case R.id.decifra_button:
				debug("Click on decrypt");
				decifraData();
				break;

			}
		}

		private KeyStore keyStore = null;
		private KeyStore.Entry entry = null;

		private void cifraData() {
			
			// TODO Auto-generated method stub
			String data = mInData.getText().toString();
			debug("String to encrypt:" + data);
			byte[] rawData = data.getBytes();

			KeyStore.PrivateKeyEntry entry = null;

			try {
				keyStore = initKeyStore();
				if (keyStore == null)
					return;
				
				entry = (KeyStore.PrivateKeyEntry) dammiElementoDalKeystore();
				if (entry == null){
					debug("Key not found");
					return;
				}
					
				PublicKey publicKeyEnc = ((KeyStore.PrivateKeyEntry) entry)
						.getCertificate().getPublicKey();
				
				KeyFactory factory = null;
				try {
					factory = KeyFactory.getInstance("RSA");
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}

				RSAPublicKeySpec rsa_public_key = null;
				try {
					rsa_public_key = factory.getKeySpec(publicKeyEnc,
							RSAPublicKeySpec.class);
				} catch (InvalidKeySpecException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				debug("Key size " + Integer.toString(rsa_public_key.getModulus().bitLength()));
				
				Cipher encCipher = null;
				byte[] ecryptedText = null;
				encCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
				encCipher.init(Cipher.ENCRYPT_MODE, publicKeyEnc);
				ecryptedText = encCipher.doFinal(rawData);
				debug("Text encrypted : " + ecryptedText.toString());
				String encryptedDataBase64 = Base64.encodeToString(
						ecryptedText, Base64.DEFAULT);
				mOutData.setText(encryptedDataBase64);
				debug("Base64 text encrypted: " + encryptedDataBase64);

			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				debug(e.toString());
			} catch (IllegalBlockSizeException e) {
				debug(e.toString());
			} catch (BadPaddingException e) {
				debug(e.toString());
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				debug(e.toString());
			} catch (NoSuchPaddingException e) {
				debug(e.toString());
			}
			flag = true;
		}

		private void decifraData() {
			
			if(flag==false){
				debug("Encrypt before");
				return;
			}
				
			// TODO Auto-generated method stub
			KeyStore.PrivateKeyEntry entry = null;

			byte[] data = mInData.getText().toString().getBytes();
			byte[] stringEncrypted = mOutData.getText().toString().getBytes();

			byte[] encrypted = null;
			try {
				encrypted = Base64.decode(stringEncrypted, Base64.DEFAULT);

			} catch (IllegalArgumentException e) {
				debug("String Base64 is not valid");
				return;
			}
			try {
				keyStore = initKeyStore();
				if (keyStore == null)
					return;
				entry = (KeyStore.PrivateKeyEntry) dammiElementoDalKeystore();
				if (entry == null){
					debug("Key not found");
					return;
				}
				Cipher decCipher = null;
				byte[] plainTextByte = null;
				decCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
				decCipher.init(Cipher.DECRYPT_MODE,
						((KeyStore.PrivateKeyEntry) entry).getPrivateKey());
				plainTextByte = decCipher.doFinal(encrypted);
				String plainText = new String(plainTextByte);
				if (plainText.equalsIgnoreCase(new String(data))){
					debug("Text decrypted: " + plainText);
					mOutData.setText(plainText);
				}else
					debug("Error in decryption");

			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				debug(e.toString());
			} catch (IllegalBlockSizeException e) {
				debug(e.toString());
			} catch (BadPaddingException e) {
				debug(e.toString());
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				debug(e.toString());
			} catch (NoSuchPaddingException e) {
				debug(e.toString());
			}

		}

		@SuppressLint("NewApi")
		private void generaChiavi() {
			new AsyncTask<Void, String, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					// TODO Auto-generated method stub
					Context cx = getActivity();
					// Generate a key pair inside the AndroidKeyStore
					Calendar notBefore = Calendar.getInstance();
					Calendar notAfter = Calendar.getInstance();
					notAfter.add(1, Calendar.YEAR);

					android.security.KeyPairGeneratorSpec.Builder builder = new KeyPairGeneratorSpec.Builder(
							cx);
					builder.setAlias(ALIAS);
					String infocert = String.format("CN=%s, OU=%s", ALIAS,
							cx.getPackageName());
					builder.setSubject(new X500Principal(infocert));
					builder.setSerialNumber(BigInteger.ONE);
					builder.setStartDate(notBefore.getTime());
					builder.setEndDate(notAfter.getTime());
					KeyPairGeneratorSpec spec = builder.build();

					KeyPairGenerator kpGenerator;
					KeyPair kp = null;
					try {
						kpGenerator = KeyPairGenerator.getInstance("RSA",
								"AndroidKeyStore");
						kpGenerator.initialize(spec);
						kp = kpGenerator.generateKeyPair();

						publishProgress("Generated key pair : " + kp.toString());
						PublicKey publickey = kp.getPublic();
						PrivateKey privateKey = kp.getPrivate();
						publishProgress("Public key format : "
								+ publickey.getFormat());
						publishProgress("Used algorithm : "
								+ publickey.getAlgorithm());
						if (privateKey.getEncoded() == null)
							publishProgress("Is not possible directly access to private key :-(");

					} catch (NoSuchAlgorithmException e) {
						debug(e.toString());
					} catch (NoSuchProviderException e) {
						debug(e.toString());
					} catch (InvalidAlgorithmParameterException e) {
						debug(e.toString());
					}
					return null;
				}

				protected void onProgressUpdate(String... values) {
					debug(values[0]);
				}

				@Override
				protected void onPostExecute(Void result) {
					// TODO Auto-generated method stub
					progressdialog.dismiss();

				}

				@Override
				protected void onPreExecute() {
					progressdialog = ProgressDialog.show(getActivity(),
							"Please wait...", "Generating keys...");
				}

			}.execute();

		}

		// Load the key pair from the Android Key Store
		private KeyStore.Entry dammiElementoDalKeystore() {
			KeyStore.Entry entry = null;
			try {
				entry = keyStore.getEntry(ALIAS, null);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnrecoverableEntryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return entry;

		}

		private KeyStore initKeyStore() {
			KeyStore keyStore = null;
			try {
				keyStore = KeyStore.getInstance("AndroidKeyStore");
				keyStore.load(null);

			} catch (KeyStoreException e) {
				debug("KeyStore Exception Error: " + e);
			} catch (NoSuchAlgorithmException e) {
				debug(e.toString());
			} catch (CertificateException e) {
				debug(e.toString());
			} catch (IOException e) {
				debug(e.toString());
			}
			return keyStore;
		}

		private void debug(String message) {
			mDebugText.append(message + "\n");
			Log.v(TAG, message);
			mScrollView.post(new Runnable()
		    {
		        public void run()
		        {
		        	mScrollView.fullScroll(View.FOCUS_DOWN);
		        }
		    });
		}
	}


}
