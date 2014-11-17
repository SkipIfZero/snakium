package com.skipifzero.snakium.framework.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.Log;

/**
 * Utility class to simplify IO with USB storage.
 * 
 * Requires <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/> in the
 * AndroidManifest.xml file. Possibly not if you're just reading.
 * 
 * @author Peter Hillerström
 * @since 2013-07-20
 * @version 2
 */
public final class USBIO {
	
	private static final String EXTERNAL_STORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;

	private USBIO() {
		throw new AssertionError();
	}

	/**
	 * Checks if a file at specified path exists.
	 * @param path the specified path
	 * @return whether file exists or not
	 */
	public static boolean fileExists(String path) {
		File file = new File(EXTERNAL_STORAGE_PATH + path);
		return file.exists();
	}
	
	/**
	 * Attempts to delete file at specified path. If specified file is a directory this method will
	 * simply return without attempting to delete it.
	 * @param path the specified path
	 */
	public static void deleteFile(String path) {
		File file = new File(EXTERNAL_STORAGE_PATH + path);
		
		//Checks if file exists.
		if(!file.exists()) {
			return;
		}
		
		//Makes sure file is not a directory.
		if(file.isDirectory()) {
			return;
		}
		
		file.delete();
	}
	
	/**
	 * Reads all the rows from file at specified path. Will return null if file doesn't exist.
	 * @param path the path to the file
	 * @return list of rows inside specified file
	 */
	public static List<String> readStringsFromFile(String path) {
		File file = new File(EXTERNAL_STORAGE_PATH + path);
		
		//Checks if file exists.
		if(!file.exists()) {
			return null;
		}
		
		List<String> rows = new ArrayList<String>();
		
		//Reads rows
		BufferedReader bf = null;
		try {
			bf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			while(true) {
				String currLine = bf.readLine();
				if(currLine == null) {
					break;
				}
				rows.add(currLine);
			}
		} catch(IOException e) {
			throw new RuntimeIOException("Problem reading file: " + e.getMessage());
		} finally {
			try {
				if(bf != null) {
					bf.close();
				}
			} catch(IOException e) {
				//Do nothing.
			}
		}
		
		return rows;
	}
	
	/**
	 * Writes all the rows to the file at the specified path. Will create an empty file if the 
	 * rows parameter is null. Will always overwrite the existing file, deleting
	 * everything previously in it.
	 * @param path the path to the file
	 * @param rows the list of rows to write
	 */
	public static void writeStringsToFile(String path, Collection<String> rows) {
		File file = new File(EXTERNAL_STORAGE_PATH + path);
		
		//Checks if file exists, if it doesn't make sure it's parent directories exists.
		if(!file.exists()) {
			file.getParentFile().mkdirs();
		}
		
		//Makes sure file is not a directory.
		if(file.isDirectory()) {
			throw new IllegalArgumentException("Specified path leads to a directory.");
		}
		
		if(rows == null) {
			rows = new ArrayList<String>();
			rows.add(""); //Minor hack to make sure the specified file is empty when null is specified.
		}
		
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			for(String row : rows) {
				bw.write(row);
				bw.newLine();
			}
		} catch (IOException e) {
			throw new RuntimeIOException("Problem writing to file: " + e.getMessage());
		} finally {
			try {
				if(bw != null) {
					bw.close();
				}
			} catch (IOException e) {
				//Do nothing.
			}
		}
	}
	
	/**
	 * Reads object from file at specified path. Will return null if file doesn't exist and sometimes
	 * if some weird error occurred while trying to read object.
	 * @param path the path to the file
	 * @return object
	 */
	public static Object readObjectFromFile(String path) {
		File file = new File(EXTERNAL_STORAGE_PATH + path);
		
		//Checks if file exists.
		if(!file.exists()) {
			return null;
		}
		
		ObjectInputStream ois = null;
		Object obj = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(file));
			try {
				obj = ois.readObject();
			} catch (ClassNotFoundException e) {
				obj = null;
			}
		} catch (IOException e) {
			throw new RuntimeIOException("Problem reading object from file: " + e.getMessage());
		} finally {
			try {
				if(ois != null) {
					ois.close();
				}
			} catch (IOException e) {
				//Do nothing.
			}
		}
		
		return obj;
	}
	
	/**
	 * Writes object to file at specified path.
	 * @param path the path to the file
	 * @param obj the object to write
	 */
	public static void writeObjectToFile(String path, Object obj) {
		File file = new File(EXTERNAL_STORAGE_PATH + path);
		
		//Checks if file exists, if it doesn't make sure it's parent directories exists.
		if(!file.exists()) {
			file.getParentFile().mkdirs();
		}
		
		//Makes sure file is not a directory.
		if(file.isDirectory()) {
			throw new IllegalArgumentException("Specified path leads to a directory.");
		}
		
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(obj);
		} catch (IOException e) {
			throw new RuntimeIOException("Problem writing object to file: " + e.getMessage());
		} finally {
			try {
				if(oos != null) {
					oos.close();
				}
			} catch (IOException e) {
				//Do nothing.
			}
		}
	}
	
	/**
	 * Writes a bitmap to the specified path.
	 * @param path the specified path
	 * @param bitmap the bitmap
	 */
	public static void writeBitmapToFile(String path, Bitmap bitmap) {
		File file = new File(EXTERNAL_STORAGE_PATH + path);
		
		//Checks if file exists, if it doesn't make sure it's parent directories exists.
		if(!file.exists()) {
			file.getParentFile().mkdirs();
		}
		
		//Makes sure file is not a directory.
		if(file.isDirectory()) {
			throw new IllegalArgumentException("Specified path leads to a directory.");
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			if(bitmap.compress(CompressFormat.PNG, 100, out)) {
				Log.d("USBIO", "Bitmap write success");
			} else {
				Log.d("USBIO", "Bitmap write failure");
			}
		} catch (FileNotFoundException e) {
			//Do nothing, I'm lazy.
		} finally {
			try {
				if(out != null) {
					out.close();
				}
			} catch (IOException e) {
				//Do nothing.
			}
		}
	}
}
