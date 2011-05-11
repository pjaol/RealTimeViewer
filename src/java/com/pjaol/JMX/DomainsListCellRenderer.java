package com.pjaol.JMX;

import java.awt.Component;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class DomainsListCellRenderer extends JLabel implements ListCellRenderer {

	private final static ImageIcon arrowIcon = WelcomeViewer.createImageIcon("Images/arrow_121.gif", "Right Arrow List");
	private final static ImageIcon folderIcon = WelcomeViewer.createImageIcon("Images/folder_open.gif", "Open Folder");
	private Map<String, String[]> defaultShortCuts;
	
	public void setDefaultShortCuts (Map<String, String[]> defaultShortCuts){
		this.defaultShortCuts = defaultShortCuts;
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		String s = value.toString();
		
		if (defaultShortCuts.containsKey(s)){
			setIcon(arrowIcon);
		} else {
			setIcon(folderIcon);
		}
		
		setText(s);
		
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		setEnabled(list.isEnabled());
		setFont(list.getFont());
		setOpaque(true);
		return this;
	}
 
	
}
