package com.skipifzero.snakium;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

/**
 * Pretty good api, but slow, inefficient and somewhat ugly implementation. This class can probably be
 * used safely and then switched out for a newer version with better implementation without any larger
 * problems if needed. For now I'll roll with it.
 * 
 * @author Peter Hillerström
 * @since 2013-07-21
 * @version 1
 */
public class StringConfig {
	
	/*
	 * Static members
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	private static final char COMMENT = ';';
	private static final char ASSIGN = '=';
	private static final char NEWLINE = '\n';
	private static final char LEFT_VALUETYPE = '<';
	private static final char RIGHT_VALUETYPE = '>';
	private static final String BOOLEAN_SPEC = "bool";
	private static final String INT_SPEC = "int";
	private static final String DOUBLE_SPEC ="float";
	
	private static final List<String> BOOLEAN_TRUE_VALUES;
	private static final List<String> BOOLEAN_FALSE_VALUES;
	static {
		BOOLEAN_TRUE_VALUES = new ArrayList<String>();
		BOOLEAN_TRUE_VALUES.add("true");
		BOOLEAN_TRUE_VALUES.add("on");
		BOOLEAN_TRUE_VALUES.add("yes");
		BOOLEAN_TRUE_VALUES.add("1");
		
		BOOLEAN_FALSE_VALUES = new ArrayList<String>();
		BOOLEAN_FALSE_VALUES.add("false");
		BOOLEAN_FALSE_VALUES.add("off");
		BOOLEAN_FALSE_VALUES.add("no");
		BOOLEAN_FALSE_VALUES.add("0");
	}
	
	private static final String HEADER_STRING;
	static {
		StringBuilder b = new StringBuilder();
		b.append(COMMENT).append("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *").append(NEWLINE);
		b.append(COMMENT).append("This is a config file, it stores specific settings.").append(NEWLINE);
		b.append(COMMENT).append(NEWLINE);
		b.append(COMMENT).append("Lines that start with \"").append(COMMENT).append("\" are comments.").append(NEWLINE);
		b.append(COMMENT).append(NEWLINE);
		b.append(COMMENT).append("Format: setting").append(LEFT_VALUETYPE).append("valuetype").append(RIGHT_VALUETYPE).append(ASSIGN).append("value").append(NEWLINE);
		b.append(COMMENT).append("setting: The name of the setting.").append(NEWLINE);
		b.append(COMMENT).append("valuetype: The type of information stored.").append(NEWLINE);
		b.append(COMMENT).append("value: The value stored.").append(NEWLINE);
		b.append(COMMENT).append("Example: board_width").append(LEFT_VALUETYPE).append(INT_SPEC).append(RIGHT_VALUETYPE).append(ASSIGN).append("5 or fullscreen").append(LEFT_VALUETYPE).append(BOOLEAN_SPEC).append(RIGHT_VALUETYPE).append(ASSIGN).append("true").append(NEWLINE);
		
		//Boolean valuetype
		b.append(COMMENT).append(NEWLINE);
		b.append(COMMENT).append(BOOLEAN_SPEC).append(": Used for storing boolean values (true, false).").append(NEWLINE);
		b.append(COMMENT).append("Accepted true values: ");
		for(String str : BOOLEAN_TRUE_VALUES) {
			if(BOOLEAN_TRUE_VALUES.indexOf(str) != 0) {
				b.append(", ");
			}
			b.append(str);
		}
		b.append(".").append(NEWLINE);
		b.append(COMMENT).append("Accepted false values: ");
		for(String str : BOOLEAN_FALSE_VALUES) {
			if(BOOLEAN_FALSE_VALUES.indexOf(str) != 0) {
				b.append(", ");
			}
			b.append(str);
		}
		b.append(".").append(NEWLINE);
		
		//Int valuetype
		b.append(COMMENT).append(NEWLINE);
		b.append(COMMENT).append(INT_SPEC).append(": Used for storing integer values (-1, 0, 1, 2).").append(NEWLINE);
		
		//Double valuetype
		b.append(COMMENT).append(NEWLINE);
		b.append(COMMENT).append(DOUBLE_SPEC).append(": Used for storing decimal values (-1.2, 3.14, 2).").append(NEWLINE);
		b.append(COMMENT).append("Accepts only \".\" as the decimal mark.").append(NEWLINE);
		
		b.append(COMMENT).append("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
		HEADER_STRING = b.toString();
	}
	
	/*
	 * Members
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	private final StringBuilder builder = new StringBuilder();
	
	private final Map<String, Map<String, ? extends Object>> settingToMap = new HashMap<String, Map<String,? extends Object>>();
	private final Map<String, Boolean> boolMap = new HashMap<String, Boolean>();
	private final Map<String, Integer> intMap = new HashMap<String, Integer>();
	private final Map<String, Double> doubleMap = new HashMap<String, Double>();
	
	private final List<Integer> validRows = new ArrayList<Integer>();
	private final List<String> stringRows;
	private final List<String> newRows = new ArrayList<String>();
	
	/*
	 * Constructors
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	public StringConfig(Collection<? extends String> rows) {
		this.stringRows = new ArrayList<String>(rows);
		parseRows(stringRows);
	}
	
	public StringConfig() {
		this.stringRows = new ArrayList<String>();
	}
	
	/*
	 * Public methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	public boolean contains(String key) {
		return boolMap.containsKey(key) || intMap.containsKey(key) || doubleMap.containsKey(key);
	}
	
	public boolean containsBoolean(String key) {
		return boolMap.containsKey(key);
	}
	
	public boolean containsInt(String key) {
		return intMap.containsKey(key);
	}
	
	public boolean containsDouble(String key) {
		return doubleMap.containsKey(key);
	}
	
	public boolean getBoolean(String key) {
		if(!containsBoolean(key)) {
			throw new IllegalArgumentException("No boolean associated with specified key.");
		}
		return boolMap.get(key);
	}
	
	public int getInt(String key) {
		if(!containsInt(key)) {
			throw new IllegalArgumentException("No int associated with specified key.");
		}
		return intMap.get(key);
	}
	
	public double getDouble(String key) {
		if(!containsDouble(key)) {
			throw new IllegalArgumentException("No double associated with specified key.");
		}
		return doubleMap.get(key);
	}
	
	public List<String> listKeys() {
		List<String> keys = new ArrayList<String>();
		keys.addAll(boolMap.keySet());
		keys.addAll(intMap.keySet());
		keys.addAll(doubleMap.keySet());
		return keys;
	}
	
	public List<String> listBooleanKeys() {
		return new ArrayList<String>(boolMap.keySet());
	}
	
	public List<String> listIntKeys() {
		return new ArrayList<String>(intMap.keySet());
	}
	
	public List<String> listDoubleKeys() {
		return new ArrayList<String>(doubleMap.keySet());
	}
	
	public void setBoolean(String key, boolean b) {
		boolean exists = containsBoolean(key);
		if(contains(key) && !exists) {
			throw new IllegalArgumentException();
		}
		
		//Simple part
		boolMap.put(key, b);
		
		//Harder part
		if(exists) {
			int index = indexOfRow(stringRows, key);
			List<String> rows = stringRows;
			if(index == -1) {
				index = indexOfRow(newRows, key);
				if(index == -1) {
					throw new IllegalStateException("Error setting boolean."); //This really shouldn't be possible.
				}
				rows = newRows;
			}
			modifyRow(rows, index, Boolean.toString(b));
		} else {
			builder.delete(0, builder.length());
			builder.append(key).append(LEFT_VALUETYPE).append(BOOLEAN_SPEC).append(RIGHT_VALUETYPE).append(ASSIGN).append(b);
			newRows.add(builder.toString());
		}
	}
	
	public void setInt(String key, int i) {
		boolean exists = containsInt(key);
		if(contains(key) && !exists) {
			throw new IllegalArgumentException();
		}
		
		//Simple part
		intMap.put(key, i);
		
		//Harder part
		if(exists) {
			int index = indexOfRow(stringRows, key);
			List<String> rows = stringRows;
			if(index == -1) {
				index = indexOfRow(newRows, key);
				if(index == -1) {
					throw new IllegalStateException("Error setting int."); //This really shouldn't be possible.
				}
				rows = newRows;
			}
			modifyRow(rows, index, Integer.toString(i));
		} else {
			builder.delete(0, builder.length());
			builder.append(key).append(LEFT_VALUETYPE).append(INT_SPEC).append(RIGHT_VALUETYPE).append(ASSIGN).append(i);
			newRows.add(builder.toString());
		}
	}
	
	public void setDouble(String key, double d) {
		boolean exists = containsDouble(key);
		if(contains(key) && !exists) {
			throw new IllegalArgumentException();
		}
		
		//Simple part
		doubleMap.put(key, d);
		
		//Harder part
		if(exists) {
			int index = indexOfRow(stringRows, key);
			List<String> rows = stringRows;
			if(index == -1) {
				index = indexOfRow(newRows, key);
				if(index == -1) {
					throw new IllegalStateException("Error setting double."); //This really shouldn't be possible.
				}
				rows = newRows;
			}
			modifyRow(rows, index, Double.toString(d));
		} else {
			builder.delete(0, builder.length());
			builder.append(key).append(LEFT_VALUETYPE).append(DOUBLE_SPEC).append(RIGHT_VALUETYPE).append(ASSIGN).append(d);
			newRows.add(builder.toString());
		}
	}
	
	public List<String> toStringRowsAppend() {
		List<String> rows = new ArrayList<String>();
		rows.addAll(stringRows);
		rows.addAll(newRows);
		return rows;
	}
	
	public List<String> toStringRowsAppendRemoveInvalid() {
		List<String> rows = new ArrayList<String>();
		rows.addAll(Arrays.asList(HEADER_STRING.split(Character.toString(NEWLINE))));
		for(Integer i : validRows) {
			rows.add(stringRows.get(i));
		}
		rows.addAll(newRows);
		return rows;
	}
	
	public List<String> toStringRowsClean() {
		builder.delete(0, builder.length());
		builder.append(HEADER_STRING);
		for(String key : boolMap.keySet()) {
			builder.append(key).append(LEFT_VALUETYPE).append(BOOLEAN_SPEC).append(RIGHT_VALUETYPE).append(ASSIGN).append(boolMap.get(key)).append(NEWLINE);
		}
		for(String key : intMap.keySet()) {
			builder.append(key).append(LEFT_VALUETYPE).append(INT_SPEC).append(RIGHT_VALUETYPE).append(ASSIGN).append(intMap.get(key)).append(NEWLINE);
		}
		for(String key : doubleMap.keySet()) {
			builder.append(key).append(LEFT_VALUETYPE).append(DOUBLE_SPEC).append(RIGHT_VALUETYPE).append(ASSIGN).append(doubleMap.get(key)).append(NEWLINE);
		}
		return Arrays.asList(builder.toString().split(Character.toString(NEWLINE)));
	}
	
	/*
	 * Private methods
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 */
	
	private int indexOfRow(List<String> rows, String key) {
		if(rows == stringRows) {
			for(Integer i : validRows) {
				String row = rows.get(i);
				if(keyAssociatedWithThisRow(row, key)) {
					return i;
				}
			}
		} else {
			for(String row : rows) {
				if(keyAssociatedWithThisRow(row, key)) {
					return rows.indexOf(row);
				}
			}
		}
		
		return -1;
	}
	
	private boolean keyAssociatedWithThisRow(String row, String key) {
		builder.delete(0, builder.length());
		builder.append(row);
		int cutOffIndex = row.indexOf(LEFT_VALUETYPE);
		builder.delete(cutOffIndex, builder.length());
		return key.equalsIgnoreCase(builder.toString().trim());
	}
	
	private void modifyRow(List<String> rows, int index, String newValue) {
		builder.delete(0, builder.length());
		builder.append(rows.get(index));
		int assignIndex = builder.indexOf(Character.toString(ASSIGN));
		builder.delete(assignIndex+1, builder.length());
		builder.append(newValue);
		rows.remove(index);
		rows.add(index, builder.toString());
	}
	
	private void parseRows(List<String> rows) {
		for(String row : stringRows) {
			parseRow(row);
		}
	}
	
	/**
	 * Parses a row. Will return it as invalid if it is a comment or generally invalid.
	 * @param row
	 * @return whether the row was valid or not
	 */
	private boolean parseRow(String row) {
		//Skips row if it is empty or is a comment.
		if(row.length() <= 0) {
			return false;
		}
		if(row.charAt(0) == COMMENT) {
			return false;
		}
		
		//Skips row and prints error if it doesn't contain needed symbols.
		if(	!row.contains(Character.toString(LEFT_VALUETYPE)) ||
			!row.contains(Character.toString(RIGHT_VALUETYPE)) ||
			!row.contains(Character.toString(ASSIGN))) {
			
			reportInvalidRow(row, stringRows.indexOf(row));
			return false;
		}
		
		//Gets index of all symbols.
		int leftValuetypeIndex = row.indexOf(LEFT_VALUETYPE);
		int rightValuetypeIndex = row.indexOf(RIGHT_VALUETYPE);
		int assignIndex = row.indexOf(ASSIGN);
		
		//Skips row and prints error if the symbols doesn't appear in right order.
		if(leftValuetypeIndex >= rightValuetypeIndex || rightValuetypeIndex >= assignIndex) {
			reportInvalidRow(row, stringRows.indexOf(row));
			return false;
		}
		
		//Gets all substrings.
		String setting = row.substring(0, leftValuetypeIndex).trim();
		String valueType = row.substring(leftValuetypeIndex+1, rightValuetypeIndex).trim();
		String value = row.substring(assignIndex+1).trim();
		
		//Skips row and prints error if substrings are too short.
		if(setting.length() <= 0 || value.length() <= 0) {
			reportInvalidRow(row, stringRows.indexOf(row));
			return false;
		}
		
		//Skips row and prints error if setting already exists.
		if(contains(setting)) {
			reportInvalidRow(row, stringRows.indexOf(row));
			return false;
		}
		
		//Skips row and prints error if valueType substring doesn't equal any of the specified valueTypes.
		if(!valueType.equalsIgnoreCase(BOOLEAN_SPEC) && !valueType.equalsIgnoreCase(INT_SPEC) && !valueType.equalsIgnoreCase(DOUBLE_SPEC)) {
			reportInvalidRow(row, stringRows.indexOf(row));
			return false;
		}
		
		//ValueType Boolean
		if(valueType.equalsIgnoreCase(BOOLEAN_SPEC)) {
			boolean isSet = false;
			boolean valueBool = false;
			
			for(String trueStr : BOOLEAN_TRUE_VALUES) {
				if(trueStr.equalsIgnoreCase(value)) {
					valueBool = true;
					isSet = true;
					break;
				}
			}
			
			if(!isSet) {
				for(String falseStr : BOOLEAN_FALSE_VALUES) {
					if(falseStr.equalsIgnoreCase(value)) {
						valueBool = false;
						isSet = true;
						break;
					}
				}
			}
			
			//Skips row and prints error if value isn't valid.
			if(!isSet) {
				reportInvalidRow(row, stringRows.indexOf(row));
				return false;
			}
			
			//Save setting
			boolMap.put(setting, valueBool);
			settingToMap.put(setting, boolMap);
			validRows.add(stringRows.indexOf(row));
			return true;
		}
		
		//ValueType Int
		if(valueType.equalsIgnoreCase(INT_SPEC)) {
			int valueInt = -1;
			try {
				valueInt = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				//Skips row and prints error if value isn't valid.
				reportInvalidRow(row, stringRows.indexOf(row));
				return false;
			}
			
			//Save setting
			intMap.put(setting, valueInt);
			settingToMap.put(setting, intMap);
			validRows.add(stringRows.indexOf(row));
			return true;
		}
		
		//ValueType Double
		if(valueType.equalsIgnoreCase(DOUBLE_SPEC)) {
			double valueDouble = -1.0;
			try {
				valueDouble = Double.parseDouble(value);
			} catch (NumberFormatException e) {
				//Skips row and prints error if value isn't valid.
				reportInvalidRow(row, stringRows.indexOf(row));
				return false;
			}
			
			//Save setting
			doubleMap.put(setting, valueDouble);
			settingToMap.put(setting, doubleMap);
			validRows.add(stringRows.indexOf(row));
			return true;
		}
		
		//Huh, well this is weird.
		return false;
	}
	
	private void reportInvalidRow(String row, int rowIndex) {
		Log.d("StringConfig", "Row " + rowIndex  + " is invalid. Contains: " + row);
	}
}
