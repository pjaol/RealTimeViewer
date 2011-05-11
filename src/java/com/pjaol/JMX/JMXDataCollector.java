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

import java.text.DecimalFormat;

import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.ITracePoint2D;
import info.monitorenter.gui.chart.TracePoint2D;
import info.monitorenter.gui.chart.io.ADataCollector;

public class JMXDataCollector extends ADataCollector {

	private JMXCollector jmxCollector;
	private String domain;
	private String beanName;
	private String attribute;
	private long startT = System.currentTimeMillis();
	
	public JMXDataCollector(ITrace2D trace, long latency) {
		super(trace, latency);
		
	}

	public void setCollector(JMXCollector jmxCollector, String domain, String beanName, String attribute){
		this.jmxCollector = jmxCollector;
		this.domain = domain;
		this.beanName = beanName;
		this.attribute = attribute;
	}
	
	@Override
	public ITracePoint2D collectData() {
		String attributeValue = new String();
		try {
			attributeValue = jmxCollector.getBeanAttribute(domain, beanName, attribute);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long now = System.currentTimeMillis();
		double x = (double) now - startT;
		Double y = new Double(attributeValue).doubleValue();
		if (y < 5 ){
			DecimalFormat twoDForm = new DecimalFormat("###.##"); // 2 decimal places
			y = Double.valueOf(twoDForm.format(y));
		} else if (y < 1){
			DecimalFormat twoDForm = new DecimalFormat("#.###"); // 3 decimal places
			y = Double.valueOf(twoDForm.format(y));
			
		}else {
			DecimalFormat twoDForm = new DecimalFormat("###"); // no decimal places 
			y = Double.valueOf(twoDForm.format(y));
		}
		
		ITracePoint2D p = new TracePoint2D(x, y);
		return p;
	}
	
	
	public JMXCollector getJmxCollector() {
		return jmxCollector;
	}

	public void setJmxCollector(JMXCollector jmxCollector) {
		this.jmxCollector = jmxCollector;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public long getStartT() {
		return startT;
	}

	public void setStartT(long startT) {
		this.startT = startT;
	}

}
