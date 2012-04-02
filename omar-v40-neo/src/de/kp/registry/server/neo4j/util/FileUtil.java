package de.kp.registry.server.neo4j.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtil {

    public static final String FILE_SEPARATOR = System.getProperty("file.separator");

    // extracts zip file contents relative to baseDir
	public static ArrayList<File> unzip(String baseDir, InputStream is) throws IOException {
		
       ArrayList<File> files = new ArrayList<File>();
       ZipInputStream zis = new ZipInputStream(is);

       while (true) {

    	   // get the next zip entry, and break out of the 
    	   // loop if there are no more
           
    	   ZipEntry zipEntry = zis.getNextEntry();
           if (zipEntry == null) break;

           String entryName = zipEntry.getName();
           if (FILE_SEPARATOR.equalsIgnoreCase("\\")) {        	   
               // convert '/' to windows file separator
               entryName = entryName.replaceAll("/", "\\\\");
           }
           
           String fileName = baseDir + FILE_SEPARATOR + entryName;
           
           // make sure that directory exists
           String dirName = fileName.substring(0, fileName.lastIndexOf(FILE_SEPARATOR));
           File dir = new File(dirName);
           dir.mkdirs();
           
           // entry could be a directory
           if (!(zipEntry.isDirectory())) {

        	   // entry is a file not a directory, so write
               // out the content of of entry to file 
               
        	   File file = new File(fileName);
               files.add(file);
               
               FileOutputStream fos = new FileOutputStream(file);

               // read data from the zip entry.  The read() method 
               // will return -1 when there is no more data to read
               byte [] buffer = new byte [1000];

               int n;
               while ((n = zis.read(buffer)) > -1) {
                   // in real life, you'd probably write the data to a file.
                   fos.write(buffer, 0, n);
               }

               zis.closeEntry();
               fos.close();            
           
           } else {            
               zis.closeEntry();
           
           }
       }

       zis.close();
       return files;
       
	}
    
	public static String absolutize(String name) {

		try {
 
			URL baseURL = new File(".").getCanonicalFile().toURI().toURL();
            return new URL(baseURL, name).toExternalForm();
        
		} catch(IOException e) {
			// do nothing
		}
        
		return name;
    }

    public static String getFileOrURLName(String fileOrURL) {

    	URL url = null;
    	try {
            url = new URL(fileOrURL);
        
    	} catch (MalformedURLException e) {
            
    		try {
				url = new File(fileOrURL).getCanonicalFile().toURI().toURL();
    		
    		} catch (Exception e1) {
    			// do nothing
            }
            
    	}
    	
    	if (url == null) return fileOrURL;
    	return url.toExternalForm();
 
    }

    public static String getCompleteRelativeFileName(String relativeFileName) {
    	
        if (FILE_SEPARATOR.equalsIgnoreCase("\\")) {                

        	// Convert '/' to Windows file separator
            relativeFileName = relativeFileName.replaceAll("/", "\\\\");
        
        }
        
        String completeRelativeFileName = relativeFileName;
        
        // Check if this is a URL first
        try {

        	new URL(relativeFileName);
            // This is a URL. Do not process
            return completeRelativeFileName;
        
        } catch (MalformedURLException ex) {
           // Not a URL. Continue processing
        }
        
        return completeRelativeFileName;
    
    }
 
}
