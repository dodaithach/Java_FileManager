package ddthach.homework01;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

public class DialogDelete extends JDialog {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public DialogDelete(AppControl appControl) {
		setResizable(false);
		setTitle("Delete");
		setModal(true);
		setBounds(100, 100, 450, 110);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JLabel lblDoYouWant = new JLabel("Do you want to delete this file/directory?");
			contentPanel.add(lblDoYouWant);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Yes");
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						ArrayList<File> items = appControl.getSelectedItems();
						
						if (items == null || items.size() == 0) {
							return;
						}
						
						boolean result = true;
						
						for (int i = 0; i < items.size(); i++) {
							File item = items.get(i);
							result &= appControl.deleteItem(item);
						}
						
						if (result) {
							appControl.setSelectedItems(null);
						}
						
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
