package de.kp.registry.server.neo4j.repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import org.neo4j.graphdb.Node;
import de.kp.registry.common.CanonicalConstants;
import de.kp.registry.server.neo4j.domain.NEOBase;
import de.kp.registry.server.neo4j.domain.exception.RegistryException;

/*
 * This class supports registration of repository items
 * as files on the local file system
 */

public class FSRepositoryManager implements RepositoryManager {

	private String root;
	
	public FSRepositoryManager() {		
		this.root = CanonicalConstants.REPOSITORY_ROOT;
	}

	public void setItem(Node node, DataHandler item) throws RegistryException {

		String nid = (String)node.getProperty(NEOBase.OASIS_RIM_ID);
		String path = getItemPath(nid);

        File riFile = new File(path);
        if (riFile.exists()) {
        	throw new RegistryException("[REPOSITORY MANAGER] The repository item with id '" + nid + "' already exists.");
        }

        FileOutputStream fos = null;

        try {
			fos = new FileOutputStream(path);
			item.writeTo(fos);
		       		
		} catch (Exception e) {
        	throw new RegistryException("[REPOSITORY MANAGER] " + e.getMessage());
		
		} finally {
        
			try {
				
				if (fos != null) {
					fos.flush();
					fos.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}

	}

	public void clearItem(Node node) throws RegistryException {

		String nid = (String)node.getProperty(NEOBase.OASIS_RIM_ID);
		String path = getItemPath(nid);

        File riFile = new File(path);
        if (!riFile.exists()) {
        	throw new RegistryException("[REPOSITORY MANAGER] The repository item with id '" + nid + "' does not exist.");
        }

        riFile.delete();

	}

	public DataHandler getItem(Node node) throws RegistryException {

		DataHandler item = null;

		String nid = (String)node.getProperty(NEOBase.OASIS_RIM_ID);
		String path = getItemPath(nid);

        try {
        	
        	// determine repository item from file system
        	
            File riFile = new File(path);
            if (!riFile.exists()) {
            	throw new RegistryException("[REPOSITORY MANAGER] The repository item with id '" + nid + "' does not exist.");
            }

            item = new DataHandler(new FileDataSource(riFile));

        } catch (Exception e) {
        	throw new RegistryException("[REPOSITORY MANAGER] " + e.getMessage());
        	
        }
        
		return item;
	
	}


	// strip the 'urn:uuid:' part from the unique 
    // identifier of a repository item 

	private String convert(String nid) {
		
        if (nid.startsWith("urn:uuid:")) {

        	nid = nid.substring(9).trim();
            nid = nid.replaceAll(":", "_");
 
        }

        return nid;
    
	}

    // get the path for a RepositoryItem given its nid.
	
    private String getItemPath(String nid) {

    	// strip urn:uuid since that is not part of file name
        return this.root + "/" + convert(nid);

    }

	public boolean isExists(Node node) {

		String nid = (String)node.getProperty(NEOBase.OASIS_RIM_ID);
		String path = getItemPath(nid);

        File riFile = new File(path);
        return riFile.exists();
        
	}

}
