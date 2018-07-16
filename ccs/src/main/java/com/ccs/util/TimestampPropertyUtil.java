package com.ccs.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DefaultPropertiesPersister;

public class TimestampPropertyUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TimestampPropertyUtil.class);
	
	public static void updateProcessedOffset(String offsetName, String readOffsetTimestampUUID) {
		
		 FileOutputStream out = null;
		 FileInputStream in = null;
		 
		try {
			// get or create the file
		    File f = getFile();
			in = new FileInputStream(f);
			Properties properties = new Properties();
			properties.load(in);
			in.close();
			out = new FileOutputStream(f);
			// create and set properties into properties object
			properties.setProperty(offsetName, readOffsetTimestampUUID);
			// write into it
			DefaultPropertiesPersister persister = new DefaultPropertiesPersister();
			persister.store(properties, out, "Updated on");

		} catch (Exception e ) {
			   LOGGER.error(e.getMessage(), e);
		   } finally {
			   try {
				out.close();
			} catch (IOException e) {
				 LOGGER.error(e.getMessage(), e);
			}
			    
		   }
		}
	
	public static String readProcessedOffset(String offsetName) {
		//UUID "13814000-1dd2-11b2-8080-808080808080" corresponds to Date 01-JAN-1970
		String result = "13814000-1dd2-11b2-8080-808080808080";
		   try {
		     // create and set properties into properties object
		     Properties properties = new Properties();
		     
		     File f = getFile();
		     FileInputStream in = new FileInputStream( f );
		     properties.load(in);
		     String offset = properties.getProperty(offsetName);
		     if (StringUtils.isNotEmpty(offset)) {
		    	 String[] arg0 = offset.split("-");
		 		if (arg0.length == 5) {
		 			result = offset;
		 		}
		     }
		   } catch (Exception e ) {
			   LOGGER.error(e.getMessage(), e);
		   }
		   return result;
		}

	private static File getFile() {
		// get or create the file
		 URL url = TimestampPropertyUtil.class.getResource("offset.properties");
		   // get or create the file
		   File f = new File(url.getPath());
		return f;
	}

}
