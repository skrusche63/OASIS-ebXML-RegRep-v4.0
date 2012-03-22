package de.kp.registry.common.security;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import de.kp.registry.common.CanonicalConstants;

public class CertificateUtil {

	private KeyStore keyStore;
	
	private PrivateKey privateKey;
	private X509Certificate certificate;
	
	private Certificate[] certificateChain;

	private Object keyStoreWriteLock = new Object();
	
	public CertificateUtil(String predefinedUser) {

		// invoke keystore for predefined user (alias)
		// and retrieve associated credentials

		this.keyStore = getKeyStore();
		if (this.keyStore == null) return;
		
		try {

			// retrieve private key from keystore
			this.privateKey = (java.security.PrivateKey) this.keyStore.getKey(predefinedUser, predefinedUser.toCharArray());

			// retrieve X509 certificate
			this.certificate = (X509Certificate) this.keyStore.getCertificate(predefinedUser);

			// retrieve certificate chain
			this.certificateChain = this.keyStore.getCertificateChain(predefinedUser);
			
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();

		} catch (KeyStoreException e) {
			e.printStackTrace();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
	}
	
	public X509Certificate getCertificate() {	
		return this.certificate;
	}
	
	public Certificate[] getCertificateChain() {
		return this.certificateChain;		
	}
	
	public PrivateKey getPrivateKey() {
		return this.privateKey;
	}
	
	private KeyStore getKeyStore() {

		synchronized (keyStoreWriteLock) {
			
			if (this.keyStore == null) {
				FileInputStream fis = null;

				try {

					String keystoreType = CanonicalConstants.KEYSTORE_TYPE;
					this.keyStore = KeyStore.getInstance(keystoreType);

					fis = new FileInputStream(CanonicalConstants.KEYSTORE_FILE);

					String keystorePass = CanonicalConstants.KEYSTORE_PASS;
					this.keyStore.load(fis, keystorePass.toCharArray());
					
				} catch (Exception e) {
					e.printStackTrace();
				
				} finally {

					if (fis != null) {
						try {
							fis.close();
						
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}

			return this.keyStore;
		}
	}
}
