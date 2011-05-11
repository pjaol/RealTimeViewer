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
package com.pjaol.JMX;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;


public class JMXCollector {



	private String jmxURI ; //= "service:jmx:rmi:///jndi/rmi://127.0.0.1:1617/jmxrmi";
	private JMXServiceURL jmxUrl;
	private JMXConnector connector;
	private String host = "127.0.0.1";
	private int port = 1617;
	private String user, passwd;
	
	
	public JMXCollector(String host, int port){
		this(host, port, null, null);
	}
	

	public JMXCollector(String host, int port, String user, String passwd){
		this.host = host;
		this.port = port;
		this.jmxURI = "service:jmx:rmi:///jndi/rmi://"+host+":"+port+"/jmxrmi";
		this.user = user;
		this.passwd = passwd;
	}

	public static void main(String[] args) {

		JMXCollector j = new JMXCollector("127.0.0.1", 1617); // test config
		try {
			j.connect();
			for(String d: j.getDomains()){
				System.out.println(d);
			}
			for(String b: j.getBeans("java.lang")){
				System.out.println(b);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedObjectNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void connect() throws IOException {

		jmxUrl = new JMXServiceURL(jmxURI);
		Map<String, String[]> env = null;
		
		if (user != null && passwd != null){
		
			env = new HashMap<String, String[]>();
			String[] creds = {user, passwd};
			env.put(JMXConnector.CREDENTIALS, creds);
			
		} 
			
		connector = JMXConnectorFactory.connect(jmxUrl, env);
		
	}

	public String[] getDomains() throws IOException {
		MBeanServerConnection mbsc = connector.getMBeanServerConnection();
		String[] domains = mbsc.getDomains();
		return domains;

	}

	public String[] getBeans(String domain) throws Exception {
		
		MBeanServerConnection mbsc = connector.getMBeanServerConnection();
//		ObjectName on = new ObjectName(domain+":type=*");
		
		Set<ObjectName> names = mbsc.queryNames(null, null);
		
		List<String> na = new ArrayList<String>();
		for(ObjectName n: names){
			String bn = n.getCanonicalName();
			//System.out.println(bn);
			if (bn.startsWith(domain+":")){
				bn = bn.replaceFirst(domain+":", "");
				na.add(bn);//n.getKeyProperty("name");
				
			}
		}
		
		String[] beanNames = new String[na.size()];
		beanNames = na.toArray(beanNames);
		
		
		return beanNames;
	}
	
	public String[] getAttributes(String domain, String beanName) throws Exception{
		
		MBeanServerConnection mbsc = connector.getMBeanServerConnection();
		ObjectName on = new ObjectName(domain+":"+beanName);
		
		MBeanInfo mbeanInfo = mbsc.getMBeanInfo(on);
		MBeanAttributeInfo[] attInfos = mbeanInfo.getAttributes();
		String[] names = new String[attInfos.length];
		
		int i = 0;
		
		for(MBeanAttributeInfo ai : attInfos){
			names[i] = ai.getName();
			//System.out.println(ai.getType());
			i++;
		}
		
		return names;
		
	}
	
	public String getBeanAttribute(String domain, String beanName, String attribute) throws Exception{
	
		MBeanServerConnection mbsc = connector.getMBeanServerConnection();
		ObjectName on = new ObjectName(domain+":"+beanName);
		Object o = mbsc.getAttribute(on, attribute);
		if (o instanceof javax.management.openmbean.CompositeDataSupport){
			CompositeDataSupport c = (CompositeDataSupport)o;
			o = c.get("used"); // mirroring memory, not always true
		}
		
		//System.out.println(o);
		
		return o.toString();
		
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
}
