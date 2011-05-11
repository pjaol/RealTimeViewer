/*
   Copyright (c) 2011 Patrick O'Leary

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

public class JMXCPUCollector extends JMXDataCollector {

	private double previous = Double.NaN;
	
	public JMXCPUCollector(ITrace2D trace, long latency) {
		super(trace, latency);
	}
	
	@Override
	public ITracePoint2D collectData() {
		String attributeValue = new String();
		try {
			attributeValue = getJmxCollector().getBeanAttribute(getDomain(), getBeanName(), getAttribute());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long now = System.currentTimeMillis();
		double x = (double) now - getStartT();
		
		Double y = new Double(attributeValue).doubleValue();
		
		if (Double.isNaN(previous)){ // first display
			previous = y;
			ITracePoint2D p = new TracePoint2D(x, 0.0d);
			
			return p;
		}
		
		double elapsedCpu = y - previous;
		
		// should have # CPU's here,
		// but this should match *nix top 
		double cpuUsage = elapsedCpu / (x * 10000F );  
		
		DecimalFormat twoDForm = new DecimalFormat("###.#"); // no decimal places
		cpuUsage = Double.valueOf(twoDForm.format(cpuUsage));
		
		
		ITracePoint2D p = new TracePoint2D(x, cpuUsage);
		return p;
	}

}
