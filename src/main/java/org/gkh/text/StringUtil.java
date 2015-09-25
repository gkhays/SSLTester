package org.gkh.text;

public class StringUtil {
	
	private StringUtil() {
	}

	/**
	 * Determines whether or not the given string is null or contains an empty
	 * value.
	 * 
	 * @param s
	 * @return true if the given string is null or empty; otherwise returns
	 *         false
	 */
	public static boolean isNullOrEmpty(String s) {
		if (s == null)
			return true;
		return s.isEmpty();
	}
}
