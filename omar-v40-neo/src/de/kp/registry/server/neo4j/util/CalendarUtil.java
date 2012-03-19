package de.kp.registry.server.neo4j.util;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class CalendarUtil {

	public static DatatypeFactory createFactory() {

        try {
            return DatatypeFactory.newInstance();
            
        } catch (DatatypeConfigurationException e) {          
        	throw new IllegalStateException("Exception while obtaining DatatypeFactory instance", e);
        }
        
	}

    public static XMLGregorianCalendar toXMLGregorianCalendar(Date date) {
        
    	if (date == null) return null;

    	GregorianCalendar calendar = new GregorianCalendar();
    	calendar.setTimeInMillis(date.getTime());
    	
    	DatatypeFactory factory = createFactory();
    	if (factory == null) return null;
    	
        return factory.newXMLGregorianCalendar(calendar);

    }

    public static Date asDate(XMLGregorianCalendar calendar) {
        
    	if (calendar == null)return null;       
        return calendar.toGregorianCalendar().getTime();

    }

}
