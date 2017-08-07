package com.dhb.util;




public class PropFileUtil {
	

    public static String getByFileAndKey(String fileName,String key){
    	return (String) PropertiesAutoLoad.getInstance(fileName)
		.getValueFromPropFile(key);
    }
	public static void main(String[] args) {
		System.out.println(PropFileUtil.getByFileAndKey("cgb.properties", "url"));;
	}
}
