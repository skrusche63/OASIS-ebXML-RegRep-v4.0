package de.kp.registry.server.neo4j.event;

import java.io.StringWriter;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.oasis.ebxml.registry.bindings.rim.NotificationType;

import de.kp.registry.common.CanonicalConstants;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

public class MailNotifierImpl extends NotifierImpl {

	// This class sends a notification to an email endpoint
	// in a text/xml representation, i.e. the body of the 
	// email is a formatted xml document
	
	public MailNotifierImpl(String endpoint) {
		super(endpoint);
	}
	
	public void notifyTo(NotificationType notification) throws RegistryException {

		String endpoint = getEndpoint();

		if ((endpoint == null) || !endpoint.startsWith("mailto:")) {
            throw new RegistryException("[Mail Notifier] Endpoint not correctly defined.");
        }

		String subject = getSubject();
		String body    = getBody(notification);

		// the notification is sent as an xml document
		String contentType = "text/xml";
		
		// finally send message via predfined smtp service
		try {
			sendMail(endpoint, subject, body, contentType);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private String getBody(NotificationType notification) {

		String body = "";
		
		try {

			StringWriter stringWriter = new StringWriter();
			
			JAXBContext context = JAXBContext.newInstance("org.oasis.ebxml.registry.bindings.rim", this.getClass().getClassLoader());
            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(notification, stringWriter);

            body = stringWriter.toString();
            
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return body;
	
	}
	
	private String getSubject() {
		String subject = "[OASIS ebXML RegRep v4 Notification]";
		return subject;
	}
	
    private void sendMail(String endpoint, String subject, String body, String contentType) throws Exception {
 
    	String host = CanonicalConstants.SMTP_HOST;
    	String port = CanonicalConstants.SMTP_PORT;

    	String auth = CanonicalConstants.SMTP_AUTH;

        // sender
        String from = CanonicalConstants.MAIL_FROM;
            
        // get the recipient that follows 'mailto:'
        String recipient = endpoint.substring(7);
          
        // set properties
        Properties properties = new Properties();
        
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.host", host);
        
        if ((port != null) && (port.length() > 0)) {
            properties.put("mail.smtp.port", port);
        }
        
        // build mail session
        Session session;
 
        if ("tls".equals(auth)) {
                   	
        	String username = CanonicalConstants.SMTP_USER;
        	String password = CanonicalConstants.SMTP_PASSWORD;
        	
            Authenticator authenticator = new MailAuthenticator(username, password);
            
            properties.put("mail.user",      username);
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");

            session = Session.getInstance(properties, authenticator);
            
        } else {        	
            session = Session.getInstance(properties);
            
        }

        // create a message
        Message mimeMessage = new MimeMessage(session);

        // set the from and to address
        InternetAddress fromAddress = new InternetAddress(from);
        mimeMessage.setFrom(fromAddress);

        InternetAddress[] toAddress = new InternetAddress[1];
        toAddress[0] = new InternetAddress(recipient);
            
        mimeMessage.setRecipients(Message.RecipientType.TO, toAddress);

        // setting subject, body and content type
        mimeMessage.setSubject(subject);
        mimeMessage.setContent(body, contentType);
            
        // finally send message
        Transport.send(mimeMessage);
        
    }    
	
}
