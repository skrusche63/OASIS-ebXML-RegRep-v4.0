package de.kp.registry.server.neo4j.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLUtil {

	public static final String FILE_SEPARATOR = System.getProperty("file.separator");

    /*
     * Validates URIs according to Registry specs.
     *
     * From Registry specs: "If the URI is a URL then a registry MUST validate the
     * URL to be resolvable at the time of submission before accepting an
     * ExternalLink submission to the registry."
     *
     * Any non-Http URLs and other types of URIs will not be checked. If the http
     * response code is smaller than 200 or bigger than 299, the http URL is
     * considered invalid.
     */

	public static boolean isValidURI(String uri) {

		if (uri == null) return false;       
        try {
            new java.net.URI(uri);
        
        } catch (java.net.URISyntaxException e) {
            // Not an URI. return false
            return false;
        }

        try {
        
        	URL url = new URL(uri);
            if (uri.startsWith("http:")) {
                
            	java.net.HttpURLConnection httpUrlConn = (java.net.HttpURLConnection) url.openConnection();
                int responseCode = httpUrlConn.getResponseCode();

                if ((responseCode < 200) || (responseCode > 299)) {
                    return false;

                } else {
                    return true;
                }
            
            } else {
                url.openConnection();
            
            }
        
        } catch (java.net.MalformedURLException e) {
            // Not an URL, will not try to resolve. Valid URI
        
        } catch (IOException e) {
            return false;
        }
        
        return true;
    }

	// fixes an input URN to comply with URN syntax by replacing invalid chars with '_'
	   
	public static String fixURN(String input) {

		String output = input.replace(FILE_SEPARATOR.charAt( 0 ), ':');
		if (!(output.startsWith("urn:")))  output = "urn:" + output;
     
		output = output.replaceAll("[^a-zA-Z_0-9:_]", "_");
		return output;
 
	}

    // this method is used to retrieve a QName prefix

	public static String getPrefix(String qName) {
    
		int i = qName.indexOf(':');
        if (i == -1) return null;

        return qName.substring(0, i);
    
	}

    // this method is used to retrieve the local part of a QName

	public static String getLocalPart(String qName) {
    
		int i = qName.lastIndexOf(':');
        if (i == -1) return qName;

        return qName.substring(i + 1);

	}

	public static Document parse(InputSource source) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder builder = createDocumentBuilder();
        return builder.parse(source); 
	}

	public static Document parse(String fileName) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder builder = createDocumentBuilder();
        return builder.parse(new File(fileName)); 
	}
	
	public static DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        
		factory.setNamespaceAware(true);
        factory.setValidating(false);
        
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(new ErrorHandler() {
                
        	public void error(SAXParseException e)
                throws SAXParseException {
                throw e;
            }

            public void fatalError(SAXParseException e)
                throws SAXParseException {
                throw e;
            }

            public void warning(SAXParseException err) throws SAXParseException {
                // do nothing
            }
        });
		
        return builder;
        
	}
}
