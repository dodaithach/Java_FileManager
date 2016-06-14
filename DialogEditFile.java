package ddthach.homework01;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.JEditorPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DialogEditFile extends JDialog {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public DialogEditFile(AppControl appControl) {
		setTitle("Edit File");
		setResizable(false);
		setModal(true);
		setBounds(100, 100, 500, 400);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane();
		JEditorPane editorPane = new JEditorPane();
		JPanel buttonPane = new JPanel();		
		
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
		{
			contentPanel.add(scrollPane);
			{
				scrollPane.setViewportView(editorPane);
			}
		}
		{
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Save");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String data = editorPane.getText();
						
						if (data == null)
							return;
						
						appControl.writeTextData(data);
						
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		
		ArrayList<String> src = appControl.getTextData();
		if (src != null) {
			String data = "";
			
			for (int i = 0; i < src.size(); i++) {
				if (i == src.size() - 1)
					data = data + src.get(i);
				else
					data = data + src.get(i) + "\n";
			}
			
			editorPane.setText(data);
		}
	}

}
