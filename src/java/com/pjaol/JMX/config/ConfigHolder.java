/*
 * Copyright (c) 2011 Patrick O'Leary

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package com.pjaol.JMX.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.pjaol.JMX.JMXCollector;

public class ConfigHolder {

	private static ConfigHolder config;

	private Map<String, JMXCollector> jmxCollectors = new HashMap<String, JMXCollector>();
	private static Properties properties = new Properties();;
	private static final String propFile = "realtimeview.properties";
	private static final String propertyCollector = "JMXDataCollector";
	private static final String UNITS = "UNITS";
	private static final String JMXDefaults = "JMXDefaults";
	private static final String JMXCustomDefaults = "JMXCustomDefaults";

	private ConfigHolder() {



		try {
			properties = load(propFile); // optional properties file

		} catch (Exception e) {
			System.err.println(propFile + " not found in classpath, ignoring.");
			e.printStackTrace();
		}

		
		if (System.getProperty("propFile") != null) {
			File f = new File(System.getProperty("propFile"));
			try {
				Properties customProperties = new Properties();
				FileInputStream in = new FileInputStream(f);
				customProperties.load(in);
				properties.putAll(customProperties);
				in.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static ConfigHolder getInstance() {
		if (config == null)
			config = new ConfigHolder();

		return config;
	}

	/**
	 * return an existing {@link JMXCollector} if one exists for this host:port
	 * or create a new {@link JMXCollector} and cache it.
	 * 
	 * @param host
	 * @param port
	 * @return {@link JMXCollector}
	 */
	public JMXCollector getJMXCollector(String host, int port, String user,
			String passwd) {
		String key = host + ":" + port + ":" + user + ":" + passwd;

		if (jmxCollectors.containsKey(key))
			return jmxCollectors.get(key);

		JMXCollector jc = new JMXCollector(host, port, user, passwd);
		jmxCollectors.put(key, jc);

		return jc;
	}

	public String[] getCollectorNames() {

		Set<String> keys = jmxCollectors.keySet();
		String[] names = new String[keys.size()];
		names = keys.toArray(names);
		Arrays.sort(names);

		return names;
	}

	private static Properties load(String propsName) throws IOException {
		Properties props = new Properties();
		URL url = ConfigHolder.class.getClassLoader().getResource(propsName);
		props.load(url.openStream());
		return props;
	}

	public static String getCustomUnitsLabel(String domain, String beanName,
			String attribute) {
		String key = getKey(domain, beanName, attribute);
		String v = properties.getProperty(UNITS + "." + key);
		System.out.println("Looking for custom UNITS :" + UNITS + "." + key);
		return v;
	}

	public static String getCustomBeanCollector(String domain, String beanName,
			String attribute) {
		String key = getKey(domain, beanName, attribute);

		String v = properties.getProperty(propertyCollector + "." + key);
		System.out.println("Looking for custom JMXDataCollector :"
				+ propertyCollector + "." + key);
		return v;
	}

	public static String getKey(String domain, String beanName, String attribute) {
		String propKey = domain + "." + beanName + "." + attribute;
		propKey = propKey.replaceAll("[^a-zA-Z0-0,]", ".");
		return propKey;
	}
	
	public String[] getDefaults(){
		String defaults = properties.getProperty(JMXDefaults);
		if (properties.contains(JMXCustomDefaults)){
			defaults += properties.getProperty(JMXCustomDefaults);
		}
		return defaults.split(",");
	}
	public String[] getDefault(String defaultShortCut){
		return properties.getProperty(JMXCustomDefaults+"."+defaultShortCut).split(",");
	}
}
