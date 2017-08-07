package com.dhb.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.log4j.Logger;

/**
 * 
 * 配置文件properties自动加载类
 * 
 * @since
 */
public class PropertiesAutoLoad {


	/**
	 * 日志
	 */
	private static final Logger log = Logger
			.getLogger(PropertiesAutoLoad.class);

	private static final Map<String, PropertiesConfiguration> propConfigMap = new HashMap<String, PropertiesConfiguration>();

	private static PropertiesConfiguration propConfig;
	/**
	 * 自动保存
	 */
	private static boolean autoSave = false;

	/**
	 * 构造器私有化
	 */
	private PropertiesAutoLoad() {

	}

	private static class PropertiesAutoLoadHolder {
		/**
		 * Singleton
		 */
		private static final PropertiesAutoLoad AUTO_LOAD = new PropertiesAutoLoad();
	}

	/**
	 * properties文件路径
	 * 
	 * @param propertiesFile
	 * @return
	 * @see
	 */
	public static PropertiesAutoLoad getInstance(String propertiesFile) {
	
		// 执行初始化
		init(propertiesFile);
		propConfig = propConfigMap.get(propertiesFile);
		return PropertiesAutoLoadHolder.AUTO_LOAD;
	}

	/**
	 * 根据Key获得对应的value
	 * 
	 * @param key
	 * @return
	 * @see
	 */
	public Object getValueFromPropFile(String key) {
		return propConfig.getProperty(key);
	}

	/**
	 * 获得对应的value数组
	 * 
	 * @param key
	 * @return
	 * @see
	 */
	public String[] getArrayFromPropFile(String key) {
		return propConfig.getStringArray(key);
	}

	/**
	 * 设置属性
	 * 
	 * @param key
	 * @param value
	 * @see
	 */
	public void setProperty(String key, String value) {
		propConfig.setProperty(key, value);
	}

	/**
	 * 设置属性
	 * 
	 * @param map
	 * @see
	 */
	public void setProperty(Map<String, String> map) {
		for (String key : map.keySet()) {
			propConfig.setProperty(key, map.get(key));
		}
	}

	/**
	 * 初始化
	 * 
	 * @param propertiesFile
	 * @see
	 */
	private static void init(String propertiesFile) {
		try {
			if (!propConfigMap.containsKey(propertiesFile)) {
				synchronized (PropertiesAutoLoad.class) {
					if (!propConfigMap.containsKey(propertiesFile)) {
						PropertiesConfiguration propConfig = new PropertiesConfiguration(
								propertiesFile);
						// 自动重新加载
						propConfig
								.setReloadingStrategy(new FileChangedReloadingStrategy());
						// 自动保存
						propConfig.setAutoSave(autoSave);
						propConfigMap.put(propertiesFile, propConfig);
					}
				}
			}
		} catch (ConfigurationException e) {
			log.error(e.getMessage());
		}
	}
	/**
	 * Test
	 * 
	 * @param args
	 * @throws InterruptedException
	 * @see
	 */

}
