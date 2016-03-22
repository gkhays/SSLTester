package org.gkh.net.ssl.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.gkh.net.ssl.App;

public class SSLTesterFrame extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -599825464766414589L;
	
	private static final String NAME = "SSLTester";
	private static final String VERSION = "1.0";

	private JButton _exit;
	private JButton _connect;
	private JTextArea _textArea;
	private JTextField _hostName;
	private JTextField _portNumber;
	private JPasswordField _password;
	private JComboBox<String> _protocolList;

	public SSLTesterFrame() {
		initialize();
	}
	
	private void initialize() {
		//App.settings.setParentFrame(this);
        setSize(new Dimension(600, 300));
        setTitle(NAME + " " + VERSION);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        
        JPanel leftPanel = createTextAreaPanel();
		JPanel rightPanel = createSettingsPanel();
		this.add(leftPanel, "West");
		this.add(rightPanel, "East");
        setVisible(true);
        
        App.TASKPOOL.execute(new Runnable() {
            public void run() {
            }
        });
	}
	
	private JPanel createSettingsPanel() {
		JPanel panel = new JPanel();
		_hostName = new JTextField(10);
		JLabel hostLabel = new JLabel("Host Name:", JLabel.TRAILING);
		hostLabel.setLabelFor(_hostName);
		panel.add(hostLabel);
		panel.add(_hostName);
		
		_portNumber = new JTextField(10);
		JLabel portLabel = new JLabel("Port:", JLabel.TRAILING);
		portLabel.setLabelFor(_portNumber);
		panel.add(portLabel);
		panel.add(_portNumber);
		
		_password = new JPasswordField(10);
		_password.setEchoChar('*');
		JLabel passwordLabel = new JLabel("Password:", JLabel.TRAILING);
		passwordLabel.setLabelFor(_password);
		panel.add(passwordLabel);
		panel.add(_password);
		
		String[] protocolChoices = { "SSLv2", "SSLv3", "TLS", "TLSv1",
				"TLSv1.1", "TLSv1.2" };
		_protocolList = new JComboBox<>(protocolChoices);
		_protocolList.setSelectedIndex(2);
		_protocolList.addActionListener(this);
		JLabel protocolLabel = new JLabel("Protocol:", JLabel.TRAILING);
		protocolLabel.setLabelFor(_protocolList);
		panel.add(protocolLabel);
		panel.add(_protocolList);
		
		_connect = new JButton("Connect");
		_connect.addActionListener(this);
		_connect.setEnabled(false);
		TextChangedListener textListener = new TextChangedListener(_connect);
		textListener.addTextField(_hostName);
		_hostName.getDocument().addDocumentListener(textListener);
		textListener.addTextField(_portNumber);
		_portNumber.getDocument().addDocumentListener(textListener);
		panel.add(_connect);
		
		_exit = new JButton("Exit");
		_exit.addActionListener(new ExitListener());
		panel.add(_exit);
		panel.setLayout(new GridLayout(5, 2));//new SpringLayout());//
		
		return panel;
	}

	private JPanel createTextAreaPanel() {
		JPanel panel = new JPanel();
		_textArea = new JTextArea(15, 20);
		JScrollPane scrollPane = new JScrollPane(_textArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		_textArea.setEditable(false);
		panel.add(scrollPane);
		return panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JComboBox<?>) {
			@SuppressWarnings("unchecked")
			JComboBox<String> cb = (JComboBox<String>) e.getSource();
			String protocol = (String) cb.getSelectedItem();
			_textArea.append(String.format("Selected Protocol: %s\n", protocol));
			System.out.printf("Selected Protocol: %s\n", protocol);
		} else if (e.getSource() instanceof JButton) {
			if (e.getSource().equals(_connect)) {
				_textArea.append("Connect button pressed.\n");
				_textArea.append(String.format("Host Name: %s\n", _hostName.getText()));
				_textArea.append(String.format("Port Number: %s\n", _portNumber.getText()));
				_textArea.append(String.format("Password: %s\n", new String(_password.getPassword())));
				_textArea.append(String.format("Protocol: %s\n", _protocolList.getSelectedItem()));
			}
		}
	}
	
	private class ExitListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			int confirm = JOptionPane.showOptionDialog(SSLTesterFrame.this,
					"Are You Sure to Close this Application?",
					"Exit Confirmation", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, null, null);
			if (confirm == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		}
	}

	private class TextChangedListener implements DocumentListener {

		private List<JTextField> textFieldList = new ArrayList<JTextField>();
		private JButton button;
		
		public TextChangedListener(JButton buttonToEnable) {
			button = buttonToEnable;
		}
		
		public void addTextField(JTextField textField) {
			textFieldList.add(textField);
		}
		
		@Override
		public void insertUpdate(DocumentEvent e) {
			checkText();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			checkText();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			checkText();
		}
		
		private void checkText() {
			for (JTextField f : textFieldList) {
				if (f.getText().isEmpty()) {
					button.setEnabled(false);
				} else {
					button.setEnabled(true);
				}
			}
		}
	}
}
