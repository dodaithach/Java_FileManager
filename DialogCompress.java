package ddthach.homework01;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DialogCompress extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;

	/**
	 * Create the dialog.
	 */
	public DialogCompress(AppControl appControl) {
		setTitle("Compress");
		setResizable(false);
		setModal(true);
		setBounds(100, 100, 330, 135);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblName = new JLabel("Name");
		lblName.setBounds(10, 22, 27, 14);
		contentPanel.add(lblName);
		
		textField = new JTextField();
		textField.setBounds(47, 19, 267, 20);
		contentPanel.add(textField);
		textField.setColumns(10);
		
		JLabel lblNameContainsFile = new JLabel("Note: Name contains file name only without extension");
		lblNameContainsFile.setBounds(10, 49, 267, 14);
		contentPanel.add(lblNameContainsFile);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						String fileName = textField.getText();
						
						if (fileName == null || fileName.compareTo("") == 0) {
							return;
						}
						
						appControl.compressItems(fileName);
						
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}
}
