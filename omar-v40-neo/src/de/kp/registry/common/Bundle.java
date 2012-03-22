package de.kp.registry.common;

import java.util.Enumeration;
import java.util.ResourceBundle;

public class Bundle extends ResourceBundle {

 public static final String BASE_NAME = "de.kp.registry.common.Settings";
    private static Bundle instance;

 private ResourceBundle bundle;

    protected Bundle() {
        // Load the resource bundle of default locale
        bundle = ResourceBundle.getBundle(BASE_NAME);
    }

    public synchronized static Bundle getInstance() {
        if (instance == null) instance = new Bundle();
        return instance;
    }
 
 public ResourceBundle getBundle() {
  return bundle;
 }

    protected Object handleGetObject(String key) {
        return getBundle().getObject(key);
     }

    public final Enumeration<String> getKeys() {
        return getBundle().getKeys();
     }


}