package de.kp.registry.common;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import org.opensaml.saml2.core.Assertion;

public class CredentialInfo {
    
    private String alias;
    
    private X509Certificate certificate;
    private Certificate[] certificateChain;
    
    private PrivateKey privateKey;

    private Assertion assertion;
    
    public CredentialInfo() {        
    }
    
    public void setAlias(String alias) {
    	this.alias = alias;
    }
 
    public String getAlias() {
    	return this.alias;
    }

    public void setAssertion(Assertion assertion) {
    	this.assertion = assertion;
    }
    
    public Assertion getAssertion() {
    	return this.assertion;
    }
    
    public void setCertificate(X509Certificate certificate) {
    	this.certificate = certificate;
    }
    
    public X509Certificate getCertificate() {
    	return this.certificate;
    }
    
    public void setCertificateChain(Certificate[] certificateChain) {
    	this.certificateChain = certificateChain;
    }
    
    public Certificate[] getCertificateChain() {
    	return this.certificateChain;
    }
    
    public void setPrivateKey(PrivateKey privateKey) {
    	this.privateKey = privateKey;
    }
    
    public PrivateKey getPrivateKey() {
    	return this.privateKey;
    }
}
