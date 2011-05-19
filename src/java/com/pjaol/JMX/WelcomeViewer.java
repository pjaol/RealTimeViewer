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
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;

public class WelcomeViewer extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	Font f12 = new Font("Times New Roman", 0, 12);
	Font f121 = new Font("Times New Roman", 1, 12);

	private enum ACTIONS {
		Exit, About, GO
	};

	private JMenu jmenuFile, jmenuHelp;
	private JMenuItem jmenuitemExit, jmenuitemAbout;
	private JLabel jlbOutput;
	private JLabel jlbHost = new JLabel("Host");
	private JLabel jlbUser = new JLabel("UserName");
	private JLabel jlbPasswd = new JLabel("Password");

	private JTextField host = new JTextField(10);
	private JTextField user = new JTextField(10);
	private JPasswordField password = new JPasswordField(10);

	private JButton jbnGo = new JButton(ACTIONS.GO.toString());

	private JPanel jplMaster;
	private boolean exitJVM = false;
	private static final String APP_TITLE = "Real Time Viewer";

	public static void main(String[] args) {
	    System.setProperty("com.apple.mrj.application.apple.menu.about.name", APP_TITLE);
	    try {
			Console c = new Console();
			c.setLocationRelativeTo(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		WelcomeViewer wv = new WelcomeViewer();
		wv.display();
		wv.setVisible(true);
		wv.setSize(400, 520);
		wv.setResizable(false);
		wv.setTitle(APP_TITLE);
		
	}

	public void display() {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
				
				//System.out.println(info.getName());
			}
		} catch (Exception e) {
			// If Nimbus is not available, you can set the GUI to another look
			// and feel.
		}

		createMenu();
		JMenuBar mb = new JMenuBar();
		mb.add(jmenuFile);
		mb.add(jmenuHelp);
		setJMenuBar(mb);
		setTitle("Real Time Viewer");

		// /Set frame layout manager
		setBackground(Color.gray);
		jplMaster = new JPanel();
		jlbOutput = new JLabel();

		ImageIcon icon = createImageIcon("Images/SearchSaw.png",
				"SearchSaw Logo");

		jlbOutput.setHorizontalTextPosition(JLabel.RIGHT);
		jlbOutput.setBackground(Color.WHITE);
		jlbOutput.setOpaque(true);
		jlbOutput.setIcon(icon);

		GridBagConstraints gBC = new GridBagConstraints();
		gBC.fill = gBC.HORIZONTAL;

		jplMaster.setLayout(new GridBagLayout());

		// |----------------|-----------------|
		// | Host (0,0)     | Host input (0,1)|
		// |----------------|-----------------|
		// | UserName (1,0) | UserName (1,1)  |
		// |----------------|-----------------|--------------------|
		// | Password (2,0) | Password input (2,1)| GO Button (2,2)|
		// |----------------|-----------------|--------------------|

		// layout grid
		// host label at 0,0
		gBC.gridx = 0;
		gBC.gridy = 0;
		jplMaster.add(jlbHost, gBC);

		// host text input at 0,1
		gBC.gridx = 1;
		host.setText("127.0.0.1:1617");
		jplMaster.add(host, gBC);

		// user label at 1,0
		gBC.gridy = 1;
		gBC.gridx = 0;
		jplMaster.add(jlbUser, gBC);

		// user text input at 1,1
		gBC.gridx = 1;
		user.setText("");
		jplMaster.add(user, gBC);

		// user label at 1,0
		gBC.gridy = 2;
		gBC.gridx = 0;
		jplMaster.add(jlbPasswd, gBC);

		// user text input at 1,1
		gBC.gridx = 1;
		user.setText("");
		jplMaster.add(password, gBC);

		
		// go button at 1,2
		gBC.gridx = 2;
		
		//jbnGo.setBorder(BorderFactory.createRaisedBevelBorder());
		
		jplMaster.add(jbnGo, gBC);
		jbnGo.addActionListener(this);

		// Add components to frame
		getContentPane().add(jlbOutput, BorderLayout.NORTH);
		getContentPane().add(jplMaster, BorderLayout.SOUTH);
		jmenuitemExit.addActionListener(this);

		addWindowListener(new WindowAdapter() {

			public void windowClosed(WindowEvent e) {
				if (exitJVM) {
					System.exit(0);
				}
			}
		});
	}

	private void createMenu() {
		jmenuFile = new JMenu("File");
		jmenuFile.setFont(f121);
		jmenuFile.setMnemonic(KeyEvent.VK_F);

		jmenuitemExit = new JMenuItem(ACTIONS.Exit.toString());
		jmenuitemExit.setFont(f12);
		jmenuitemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
				ActionEvent.CTRL_MASK));

		jmenuFile.add(jmenuitemExit);
		jmenuHelp = new JMenu("Help");
		jmenuHelp.setFont(f121);
		jmenuHelp.setMnemonic(KeyEvent.VK_H);

		jmenuitemAbout = new JMenuItem(APP_TITLE);
		jmenuitemAbout.setFont(f12);
		jmenuHelp.add(jmenuitemAbout);

	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path, String description) {

		java.net.URL imgURL = WelcomeViewer.class.getResource(path);

		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	private void createViewer() {
		
		String[] h = host.getText().split(":");
		String hostName = h[0];
		String portText = h[1];
		String userText = user.getText();
		String passText = new String(password.getPassword());
		

		if (hostName != null && portText != null) {
			SelectList sl = new SelectList(hostName,
					new Integer(portText).intValue(), userText, passText);
			sl.draw();
			
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();

		switch (ACTIONS.valueOf(action)) {
		case Exit:
			exitJVM = true;
			dispose();

			break;
		case GO:
			createViewer();
			break;
		default:
			System.out.println(action.toString());
			break;
		}

	}
}
