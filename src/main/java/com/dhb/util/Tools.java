package com.dhb.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

public class Tools {
	
	public static final String OWNER_CPIC  = "CPIC";
	public static final String OWNER_CPICA = "CPICA";
	public static final String OWNER_NONE = "";

	public static String trimLeftRightWhitespace(String srcStr) {
		return CharMatcher.WHITESPACE.trimFrom(srcStr);
	}

	public static boolean isDigit(String str) {
		return CharMatcher.DIGIT.matchesAllOf(str);
	}

	public static boolean isDecimal(String str) {
		return Pattern.compile("([1-9]+[0-9]*|0)(\\.[\\d]+)?").matcher(str)
				.matches();
	}

	public static String getCurrentTime() {
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		time.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		return time.format(new Date());
	}

	public static String getUUID() {
		return StringUtils.remove(UUID.randomUUID().toString(),"-");
	}

	/**
	 * 字符串是否为空
	 * 
	 * @param str
	 * @return boolean
	 */
	public static boolean isEmpty(String str) {
		if (str == null) {
			return true;
		}
		if ("".equals(str.trim())) {
			return true;
		}

		return false;
	}
	
	public static boolean checkValue(String valueStr)
	{
		boolean status = true;
		if (("").equals(valueStr) || ("null").equals(valueStr)
				|| ("NULL").equals(valueStr) || valueStr == null
				|| ("undefined").equals(valueStr))
		{
			status = false;
		}
		return status;
	}
	 public static String toUnicodeString(String s) {
	       StringBuffer sb = new StringBuffer();
	       for (int i = 0; i < s.length(); i++) {
	         char c = s.charAt(i);
	         if (c >= 0 && c <= 255) {
	           sb.append(c);
	         }
	         else {
	          sb.append("\\u"+Integer.toHexString(c));
	         }
	       }
	       return sb.toString();
	     }
	 public   static   String   unicodeToGB(String   s)   {      
       StringBuffer   sb   =   new   StringBuffer();      
       StringTokenizer   st   =   new   StringTokenizer(s,   "\\u");      
       while   (st.hasMoreTokens())   {      
           sb.append(   (char)   Integer.parseInt(st.nextToken(),   16));      
       }      
       return   sb.toString();      
   }  
	 
	public static String toLlinuxPath(String path){
		String tempPath = path;
		if(Strings.isNullOrEmpty(tempPath)){
			return "";
		}
		if(tempPath.indexOf("\\")>0){
			tempPath =tempPath.replace("\\", "/");
			
		}
		return tempPath;
	}
}
