package de.kp.registry.common;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*/
 * Represents a URN as defined by RFC 2141.
 * Based upon http://www.ietf.org/rfc/rfc2141.txt
 *
 * @author Farrukh S. Najmi
 */

/*
 * This class is an adapted version of URN from OMAR v3.1; 
 * the original code is subject to the freebxml License, Version 1.1
 */

public class URN {
    
    /** Regular expression to validate RFC 2141 URN syntax. */
    public final static String REGEXP_URN =
        "^urn:[a-zA-Z0-9][a-zA-Z0-9-]{1,31}:([a-zA-Z0-9()+,.:=@;$_!*'-]|%[0-9A-Fa-f]{2})+$";
    public final static Pattern PATTERN_URN = Pattern.compile(URN.REGEXP_URN);
    
    private String urn;
    private String namespace;
    private String suffix;
    
    /** Creates a new instance of URN */
    public URN(String spec)  {
	urn = spec;
    }
    
    /**
     * Performs validation of the URN according to RFC 2141.
     */
    public void validate() throws URISyntaxException {
        
    	try {
            Matcher urnMatcher = PATTERN_URN.matcher(urn);
            if (!urnMatcher.matches()) {
                throw new URISyntaxException(urn, "[URN] URI Syntax Exception.");                
            }
                        
        } catch (URISyntaxException e) {
            throw e;
        
        } catch(Exception  nse) {
 
        	URISyntaxException e = new URISyntaxException(urn, "[URN] Exception during URN validation.");
            e.initCause(nse);
            
            throw e;
        
        }        
    
    }
    
    /**
     * Modifies a potentially invalid URN by replacing any invalid characters with '_' to make it a valid URN.
     */
    public void makeValid() throws URISyntaxException {
        try {
            validate();
            //No need to validate as it is already valid.
            return;
        } catch (Exception e) {
            //Need to continue to make valid.
        }
        
        try {            
            
        	// Get namespace
            
        	parseURN();
            if ((namespace == null) || (namespace.length() == 0)) {
            
            	String defaultNamespacePrefix = CanonicalConstants.DEFAULT_NS_PREFIX;
                if (defaultNamespacePrefix == null) {
                    throw new URISyntaxException(urn, "[URN] No default URN prefix.");
                }
                
                urn = defaultNamespacePrefix + ":" + suffix;
                parseURN();
            
            }
            
            //Replace invalid chars with '-'
            namespace = namespace.replaceAll("[^A-Za-z0-9-]", "-");
            
            while ((namespace.length() >= 1) && (namespace.startsWith("-"))) {
                //Skip first char if it is '-'
                namespace = namespace.substring(1);
            }
            
            //Replace '/' and '\' chars with ':'
            suffix = suffix.replace('/', ':');
            suffix = suffix.replace('\\', ':');
            
            //Replace invalid chars with '_'
            suffix = suffix.replaceAll("[^a-zA-Z0-9()+,.:=@;$_!*'%-]", "_");
            
            urn = "urn:" + namespace + ":" + suffix;
            
            //Remove any repeated ':' chars
            urn = urn.replaceAll("[:]{2,}", ":");
                        
            //Now validate to double check makeValid's output
            validate();
        } catch(NoSuchElementException  nse) {
            throw new URISyntaxException(urn, "[URN] Incomplete URN.");
            
        } catch (Exception e) {
            URISyntaxException e1 = new URISyntaxException(urn, "[URN] Exception during URN validation.");
            e1.initCause(e);
            
            throw e1;
        }        
    
    }
    
    private void parseURN() throws URISyntaxException {

    	String delimeter = "[:]";
        String nameSpaceAndSuffix = new String(urn);
        
        if (urn.startsWith("urn:")) {
            nameSpaceAndSuffix = urn.substring(4);
        } else if (urn.startsWith("http://")) {
            nameSpaceAndSuffix = urn.substring(7);
            delimeter = "[/\\\\]";
        } else if (urn.startsWith("./")) {
            nameSpaceAndSuffix = urn.substring(1);
            delimeter = "[/\\\\]";
        } else if (urn.startsWith(".\\")) {
            nameSpaceAndSuffix = urn.substring(1);
            delimeter = "[/\\\\]";
        } else if (urn.startsWith("../")) {
            nameSpaceAndSuffix = urn.substring(2);
            delimeter = "[/\\\\]";
        } else if (urn.startsWith("..\\")) {
            nameSpaceAndSuffix = urn.substring(2);
            delimeter = "[/\\\\]";
        } else {
            try {
                URL url = new URL(urn);
                namespace = null;
                suffix = url.getPath();
                return;
            } catch (MalformedURLException e) {
                namespace = null;
                suffix = new String(urn);
                return;
            }
        }
        
        String[] components = nameSpaceAndSuffix.split(delimeter, 2);
        if (components.length == 0) {
            namespace = null;
            suffix = null;            
        } else if (components.length == 1) {
            namespace = null;
            suffix = nameSpaceAndSuffix;
        } else if (components.length == 2) {
            namespace = components[0];
            suffix = components[1];
        }
    }
        
    public String getURN() {
        return urn;
    }
    
    public String getNamespace() {
        return namespace;
    }
    
    public String getSuffix() {
        return suffix;
    }
    
    public String toExternalForm() {
        return urn;
    }
    
    public boolean equals(Object o) {
        if(o instanceof URN)
            return equals((URN)o);
        else
            return false;
    }
    
    public boolean equals(URN _urn) {        
        if(!(_urn instanceof URN))
            return false;
        
        URN otherURN = (URN)_urn;
        
        return (otherURN.namespace.equals(namespace) &&
                otherURN.suffix.equals(suffix));
    }
    
    public String toString() {
        return toExternalForm();
    }
    
    
}
