package com.example.gdgmeetsu2014_asymmetric_demo;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
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



public class ActivityGenerateRSAKey extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generatersakey);

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

	/*
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements
			View.OnClickListener {

		private Button exit_Button;
		private Button mGenChiaviButton;
		private Button viewParameterButton;
		private Button mCifraButton;
		private Button mDecifraButton;
		private ScrollView mScrollView;
		private TextView mDebugText;
		private EditText mInData;
		private EditText mOutData;
		ProgressDialog progressdialog;

		private static final String TAG = "ActivityGenerateRSAKey";

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_generatersakey,
					container, false);

			// Bottoni
			exit_Button = (Button) rootView.findViewById(R.id.exit_button);
			exit_Button.setOnClickListener(this);
			mGenChiaviButton = (Button) rootView
					.findViewById(R.id.generate_button);
			mGenChiaviButton.setOnClickListener(this);

			viewParameterButton = (Button) rootView
					.findViewById(R.id.view_param_button);
			viewParameterButton.setOnClickListener(this);

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
				debug("Cliccato Chiudi");
				this.getActivity().finish();
				break;
			case R.id.generate_button:
				debug("Click on Gen Keys");
				genKeys();
				break;
			case R.id.view_param_button:
				debug("Click on View Parameters");
				view_m_e_d();
				break;

			case R.id.cifra_button:
				debug("Click on Encrypt");
				encrypt();
				break;
			case R.id.decifra_button:
				debug("Click on Dencrypt");
				decrypt();
				break;

			}
		}

		Key publicKey = null;
		Key privateKey = null;
		KeyPair keypair = null;

		BigInteger m = null;
		BigInteger e = null;
		BigInteger d = null;

		byte[] encryptedData = null;
		byte[] decryptedData = null;

		boolean flag = false;
		
		//Keys Generation
		private void genKeys() {

			KeyPairGenerator kpg = null;
			try {
				kpg = KeyPairGenerator.getInstance("RSA");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}

			debug("Used Provider : " + kpg.getProvider().getName());

			SecureRandom sr = null;
			sr = new SecureRandom();
			
			kpg.initialize(2048, sr);

			keypair = kpg.genKeyPair();

			publicKey = keypair.getPublic();

			privateKey = keypair.getPrivate();

			view_m_e_d();

		}
		
		//RSA Parameters
		private void view_m_e_d() {
			clearText();
			if (keypair == null){
				debug("Keys not found!");
				return;
			}		
			KeyFactory factory = null;
			try {
				factory = KeyFactory.getInstance("RSA");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}

			RSAPublicKeySpec rsa_public_key = null;
			try {
				rsa_public_key = factory.getKeySpec(keypair.getPublic(),
						RSAPublicKeySpec.class);
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			RSAPrivateKeySpec rsa_private_key = null;
			try {
				rsa_private_key = factory.getKeySpec(keypair.getPrivate(),
						RSAPrivateKeySpec.class);
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			debug("----Private Key Parameters----");
			debug("Hex Private Exponent: "
					+ rsa_private_key.getPrivateExponent().toString(16));
			debug("Hex Public Module: "
					+ rsa_private_key.getModulus().toString(16));
			debug("-----------------------------");
			debug("----Public Key Parameters----");
			debug("Hex Public Module: "
					+ rsa_public_key.getModulus().toString(16));
			debug("Hex Public Exponent: "
					+ rsa_public_key.getPublicExponent().toString(16));
			debug("----------------------------\n");
			
			m = rsa_public_key.getModulus();
			e = rsa_public_key.getPublicExponent();
			d = rsa_private_key.getPrivateExponent();

		}


		private void encrypt() {
			if (keypair == null){
				debug("Keys not found!");
				return;
			}
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
			KeyFactory factory = null;
			try {
				factory = KeyFactory.getInstance("RSA");
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			PublicKey pubKey = null;
			
			try {
				pubKey = factory.generatePublic(keySpec);
			} catch (InvalidKeySpecException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			Cipher cipher = null;
			try {
				cipher = Cipher.getInstance("RSA");

			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchPaddingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			} catch (InvalidKeyException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			debug("Used Provider : " + cipher.getProvider());
			String plainText = mInData.getText().toString();
			try {
				encryptedData = cipher.doFinal(plainText.getBytes());
			} catch (IllegalBlockSizeException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (BadPaddingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			String base64_cipheredData = Base64.encodeToString(encryptedData,
					Base64.DEFAULT);
			mOutData.setText(base64_cipheredData);
			flag = true;

		}

		private void decrypt() {
			
			if (keypair == null){
				debug("Keys not found!");
				return;
			}
			if(flag==false){
				debug("Encrypt before");
				return;
			}
			// TODO Auto-generated method stub
			RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, d);
			KeyFactory keyfactory = null;
			PrivateKey privKey = null;
			try {
				keyfactory = KeyFactory.getInstance("RSA");
			} catch (NoSuchAlgorithmException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			try {
				privKey = keyfactory.generatePrivate(keySpec);
			} catch (InvalidKeySpecException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			Cipher de_cipher = null;
			try {
				de_cipher = Cipher.getInstance("RSA");
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchPaddingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				de_cipher.init(Cipher.DECRYPT_MODE, privKey);
			} catch (InvalidKeyException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			byte[] base64_cipheredData = mOutData.getText().toString()
					.getBytes();
				 
		
			byte[] cipheredData = Base64.decode(base64_cipheredData,
					Base64.DEFAULT);

			try {
				decryptedData = de_cipher.doFinal(cipheredData);
			} catch (IllegalBlockSizeException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (BadPaddingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			String decryptedText = new String(decryptedData);
			mOutData.setText(decryptedText);
			debug("Decrypted Text: " + decryptedText);

			flag = false;
		}

		
		@SuppressLint("NewApi")
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

		private void clearText() {
			mDebugText.setText("");
			
		}
	}

}
