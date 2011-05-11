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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

import com.pjaol.JMX.config.ConfigHolder;

public class SelectList {

	private JMXCollector jmxCollector;

	private final JList domains = new JList();
	private final JList attributes = new JList();
	private final JList beans = new JList();
	private final JButton jb = new JButton("Graph");

	private boolean allowGraph = false;
	private String host;
	private int port;
	private String selectedDomain;
	private String selectedBeanName;
	private String selectedAttribute;
	private Map<String, String[]> defaultShortCuts = new HashMap<String, String[]>();
	private ConfigHolder configHolder = ConfigHolder.getInstance();

	public SelectList(String host, int port) {
		this(host, port, null, null);
	}

	public SelectList(String host, int port, String user, String passwd) {
		this.host = host;
		this.port = port;

		jmxCollector = configHolder.getJMXCollector(host, port, user, passwd);
	}

	public static void main(String args[]) {

		SelectList sl = new SelectList("127.0.0.1", 1617);
		sl.draw();

	}

	public void draw() {

		String labels[] = null;
		String[] defaults = configHolder.getDefaults();
		
		
		for (String s : defaults) {
			defaultShortCuts.put(s, configHolder.getDefault(s));
		}
		
		DomainsListCellRenderer lcr = new DomainsListCellRenderer();
		lcr.setDefaultShortCuts(defaultShortCuts);
		
		domains.setCellRenderer(lcr);
		
		try {
			jmxCollector.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {

			String[] d = jmxCollector.getDomains();
			int sz = defaults.length + d.length;
			labels = new String[sz];

			System.out.println("sz =" + sz + " defaults=" + defaults.length
					+ " d=" + d.length);

			System.arraycopy(defaults, 0, labels, 0, defaults.length);
			System.arraycopy(d, 0, labels, defaults.length, d.length);

		} catch (IOException e) {
			e.printStackTrace();
		}

		JFrame frame = new JFrame("jmx://" + host + ":" + port);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = frame.getContentPane();

		domains.setListData(labels);

		JScrollPane scrollPane1 = new JScrollPane(domains);
		JScrollPane scrollPane2 = new JScrollPane(beans);
		JScrollPane scrollPane3 = new JScrollPane(attributes);

		contentPane.add(scrollPane1, BorderLayout.WEST);
		contentPane.add(scrollPane2, BorderLayout.CENTER);
		contentPane.add(scrollPane3, BorderLayout.EAST);
		contentPane.add(jb, BorderLayout.SOUTH);

		addDomainsListener();
		addBeansListener();
		addAttributesListener();
		addButtonListener();

		frame.setSize(700, 250);
		frame.setVisible(true);
	}

	private void addDomainsListener() {

		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseEvent) {
				JList theList = (JList) mouseEvent.getSource();
				if (mouseEvent.getClickCount() == 2) {
					int index = theList.locationToIndex(mouseEvent.getPoint());
					if (index >= 0) {
						Object o = theList.getModel().getElementAt(index);
						String selected = o.toString();
						System.out.println("Double-clicked on: "
								+ selected);

						if (defaultShortCuts.containsKey(selected)) {
							String[] vals = defaultShortCuts
									.get(selected);
							selectedDomain = vals[0];
							selectedBeanName = vals[1];
							selectedAttribute = vals[2];
							createChart(host+":"+port+" "+selected);
						} else {
							
							selectedDomain = selected;
							
							String[] beanNames = null;
							try {
								beanNames = jmxCollector
										.getBeans(selectedDomain);
							} catch (Exception e) {
								e.printStackTrace();
							}
							attributes.setListData(new String[0]);
							Arrays.sort(beanNames); // make human readable
							beans.setListData(beanNames);
						}
					}
				}
			}
		};

		domains.addMouseListener(mouseListener);

	}

	private void addBeansListener() {

		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseEvent) {
				JList theList = (JList) mouseEvent.getSource();
				attributes.setListData(new String[0]);
				if (mouseEvent.getClickCount() == 2) {
					int index = theList.locationToIndex(mouseEvent.getPoint());
					if (index >= 0) {
						Object o = theList.getModel().getElementAt(index);
						selectedBeanName = o.toString();
						System.out.println("Double-clicked on: "
								+ selectedBeanName);
						String[] attNames = null;
						try {
							attNames = jmxCollector.getAttributes(
									selectedDomain, selectedBeanName);
						} catch (Exception e) {
							e.printStackTrace();
						}

						attributes.setListData(attNames);
					}
				}
			}
		};

		beans.addMouseListener(mouseListener);

	}

	private void addAttributesListener() {

		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseEvent) {
				JList theList = (JList) mouseEvent.getSource();
				int index = theList.locationToIndex(mouseEvent.getPoint());
				Object o = theList.getModel().getElementAt(index);
				selectedAttribute = o.toString();
				allowGraph = true;

				if (mouseEvent.getClickCount() == 2) {

					if (index >= 0) {

						Object attribute = null;
						try {
							attribute = jmxCollector.getBeanAttribute(
									selectedDomain, selectedBeanName,
									selectedAttribute);

						} catch (Exception e) {
							e.printStackTrace();
						}
						System.out.println("Double-clicked on: "
								+ selectedAttribute + " value: " + attribute);
						// attributes.setListData(attNames);
					}
				}
			}
		};

		attributes.addMouseListener(mouseListener);

	}

	private void addButtonListener() {
		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!allowGraph)
					return;
				System.out.println("Doing Stuff on button click "
						+ selectedDomain + " --" + selectedBeanName + " --"
						+ selectedAttribute);
				createChart(null);
			}
		});
	}

	private void createChart(String shortName) {
		
		String title = (shortName == null)? host + ":" + port + "/" + selectedBeanName + "/"
				+ selectedAttribute : shortName;
		
		Viewer v = new Viewer(title);
		
		System.out.println("Doing Stuff on button click "
				+ selectedDomain + " --" + selectedBeanName + " --"
				+ selectedAttribute);
		v.doChart();
		v.addLine(jmxCollector, selectedDomain, selectedBeanName,
				selectedAttribute);
		
	}
}
