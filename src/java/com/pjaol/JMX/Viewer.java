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

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.io.ADataCollector;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyMinimumViewport;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.util.Range;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pjaol.JMX.config.ConfigHolder;

public class Viewer {

	private Chart2D chart = new Chart2D();
	private JSlider m_latencyTimeSlider;
	
	private String name ;
	private long latency = 1000l;
	private int colorPart =0;
	Color[] colors = new Color[]{  Color.BLUE,
								   Color.BLACK,
								   Color.GREEN,
								   Color.ORANGE,
								   Color.RED};
	
	private List<ADataCollector> collectors = new ArrayList<ADataCollector>();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Viewer(String name){
		this.name = name;
		
	}
	
	
	public static void main(String[] args) {
		Viewer vi = new Viewer("A chart");
		vi.doChart();
	}
	
	
	public void addLine(JMXCollector jmxCollector, String domain, String beanName, String attribute){
		ITrace2D trace = new Trace2DLtd(200);
		trace.setColor(getNextColor());
		trace.setName(beanName+":"+attribute);
		
		
		String units = ConfigHolder.getCustomUnitsLabel(domain, beanName, attribute);
		
		if (units == null){
			units = "ms"; // default to milliseconds
		}
		
		trace.setPhysicalUnits("ms", units);
		JMXDataCollector collector = null;
		
		String customBeanCollector = ConfigHolder.getCustomBeanCollector(domain, beanName, attribute);
		
		chart.addTrace(trace);
		if (customBeanCollector == null){
			collector = new JMXDataCollector(trace, latency);
		} else {
			// Reflection is easy... the exceptions are a pain
			try {
				Class<?> c = Class.forName(customBeanCollector);
				Constructor<?> con = c.getConstructor(new Class[]{ITrace2D.class, long.class});
				collector = (JMXDataCollector) con.newInstance(new Object[]{trace, latency});
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		collector.setCollector(jmxCollector, domain, beanName, attribute);
		collector.start();
		
		collectors.add(collector);
		
	}
	
	private Color getNextColor(){
		
		Color result = null;
		if (colorPart >= colors.length)
			colorPart =0;
		
		result = colors[colorPart];
		colorPart++;
		return result;
	}
	
	public void doChart(){
		

		// Make it visible:
		// Create a frame.
		JFrame frame = new JFrame(name);
		// add the chart to the frame:
		createLatencySlider();
		
		chart.getAxisY().setRangePolicy(new RangePolicyMinimumViewport(new Range(0, 1)));
	    chart.setGridColor(Color.LIGHT_GRAY);
	    chart.enablePointHighlighting(true);
	    chart.setToolTipType(Chart2D.ToolTipType.VALUE_SNAP_TO_TRACEPOINTS);
	    
		
		frame.getContentPane().add(chart);
		frame.getContentPane().add(m_latencyTimeSlider, BorderLayout.SOUTH);
		
		frame.setSize(400, 300);

	    frame.addWindowListener(new WindowAdapter() {
	        /**
	         * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
	         */
	        @Override
	        public void windowClosing(final WindowEvent e) {
	          stopCollectors();
	          super.windowClosing(e);
	        }
	      });
		
		frame.setVisible(true);
		
	}
	
	private void stopCollectors(){
		for(ADataCollector ad: collectors){
			ad.stop();
			ad = null;
		}
	}
	
	
    private void createLatencySlider() {
        // Latency slider:
    	int baseLatency = (int) latency;
    	
    	m_latencyTimeSlider = new JSlider((baseLatency / 2), (int) latency * 5);
        m_latencyTimeSlider.setBackground(Color.WHITE);
        m_latencyTimeSlider.setValue((int) latency);
        m_latencyTimeSlider.setMajorTickSpacing(baseLatency);
        m_latencyTimeSlider.setMinorTickSpacing((baseLatency / 2));
        m_latencyTimeSlider.setSnapToTicks(true);
        m_latencyTimeSlider.setPaintLabels(true);
        m_latencyTimeSlider.setBorder(BorderFactory.createTitledBorder(BorderFactory
            .createEtchedBorder(), "Latency for adding points.", TitledBorder.LEFT,
            TitledBorder.BELOW_TOP));
        m_latencyTimeSlider.setPaintTicks(true);

        m_latencyTimeSlider.addChangeListener(new ChangeListener() {
          public void stateChanged(final ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            // Only if not currently dragged...
            if (!source.getValueIsAdjusting()) {
              int value = source.getValue();
              for(ADataCollector ad: collectors){
            	  ad.setLatency(value);
              }
            }
          }
        });
      }
	
}
