package ddthach.homework01;

import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.JList;
import java.awt.BorderLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;

import java.io.*;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.JSplitPane;
import javax.swing.JEditorPane;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JScrollPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FileManager {
	private AppControl m_appControl;

	private JFrame frame;
	
	// View
	private JMenuBar menuBar;
	private JMenu mnCopy;
	private JMenu mnPaste;
	private JMenu mnRename;
	private JMenu mnDelete;
	private JMenu mnZip;
	private JMenu mnUnzip;
	private JMenu mnSplit;
	private JMenu mnMerge;
	private JComboBox comboBox;
	private JList list;
	private JScrollPane scrollPane;
	
	// Model
	private DefaultComboBoxModel<String> m_comboBox_model;
	private DefaultListModel<String> m_list_model;
	
	private ArrayList<File> m_child_list;
	private JMenu mnNewFile;
	private JMenu mnNewDirectory;
	private JMenu mnEditFile;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FileManager window = new FileManager();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FileManager() {
		m_child_list = new ArrayList<>();
		
		// Init model
		m_comboBox_model = new DefaultComboBoxModel<>();
		m_list_model = new DefaultListModel<>();
		
		initialize();
		
		m_appControl = new AppControl();
		
		initUIData();
	}

	/**
	 * My functions
	 */
	public void initUIData() {		
		// 1. Init combobox
		File[] roots = File.listRoots();
		
		this.comboBox.setModel(m_comboBox_model);
		for (int i = 0; i < roots.length; i++)
			m_comboBox_model.addElement(roots[i].toString());
		
		// 2. Init default value for m_appControl
		m_appControl.setRoot(roots[0]);
		m_appControl.setCurrentDirectory(roots[0]);
		
		// 3. Init listview
		this.list.setModel(m_list_model);
		
		this.updateList(m_appControl.getCurrentDirectory());
	}
	
	public void updateList(File directory) {		
		if (directory == null)
			return;
		
		m_list_model.clear();
		m_list_model.addElement("..");
		
		m_child_list.clear();
		m_child_list.add(null);
		
		File[] childs = directory.listFiles();
		if (childs != null) {
			for (int i = 0; i < childs.length; i++) {
				m_child_list.add(childs[i]);
				m_list_model.addElement(childs[i].getName().toString());
			}
		}
	}
	
	public void onComboBoxItemChanged() {
		int idx = comboBox.getSelectedIndex();
		
		if (idx != -1) {
			File root = new File(m_comboBox_model.getElementAt(idx));
			
			if (root == null)
				return;
			
			m_appControl.setRoot(root);
			
			updateList(root);
		}
	}
	
	public void setSelectedItems() {
		int[] indices = list.getSelectedIndices();
		
		if (indices == null)
			return;
		
		ArrayList<File> items = new ArrayList<>();
		
		for (int i = 0; i < indices.length; i++) {
			File item = m_child_list.get(indices[i]);
			
			if (item != null)
				items.add(item);
		}
		
		m_appControl.setSelectedItems(items);
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception ex) {
			System.out.println(ex);
		}
		
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 600, 450);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		mnNewFile = new JMenu("New File");
		mnNewFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				DialogNewFile dialog = new DialogNewFile(m_appControl);
				dialog.setVisible(true);
				
				updateList(m_appControl.getCurrentDirectory());
			}
		});
		menuBar.add(mnNewFile);
		
		mnNewDirectory = new JMenu("New Directory");
		mnNewDirectory.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				DialogNewDirectory dialog = new DialogNewDirectory(m_appControl);
				dialog.setVisible(true);
				
				updateList(m_appControl.getCurrentDirectory());
			}
		});
		menuBar.add(mnNewDirectory);
		
		mnRename = new JMenu("Rename");
		mnRename.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setSelectedItems();
				
				ArrayList<File> items = m_appControl.getSelectedItems();
				if (items == null || items.size() == 0 || items.size() > 1)
					return;
				
				DialogRename dialog = new DialogRename(m_appControl);
				dialog.setVisible(true);
				
				updateList(m_appControl.getCurrentDirectory());
			}
		});
		menuBar.add(mnRename);
		
		mnDelete = new JMenu("Delete");
		mnDelete.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setSelectedItems();
				
				DialogDelete dialog = new DialogDelete(m_appControl);
				dialog.setVisible(true);
				
				updateList(m_appControl.getCurrentDirectory());
			}
		});
		menuBar.add(mnDelete);
		
		mnCopy = new JMenu("Copy");
		mnCopy.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setSelectedItems();
				m_appControl.copyItems();
			}
		});
		menuBar.add(mnCopy);
		
		mnPaste = new JMenu("Paste");
		mnPaste.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				AppControl.pasteItems(m_appControl.getBuffers(), m_appControl.getCurrentDirectory());
				updateList(m_appControl.getCurrentDirectory());
			}
		});
		menuBar.add(mnPaste);
		
		mnEditFile = new JMenu("Edit File");
		mnEditFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setSelectedItems();
				
				ArrayList<File> items = m_appControl.getSelectedItems();
				if (items == null || items.size() == 0 || items.size() > 1)
					return;
				
				File item = items.get(0);
				if (!item.getName().endsWith(".txt"))
					return;
				
				DialogEditFile dialog = new DialogEditFile(m_appControl);
				dialog.setVisible(true);
			}
		});
		menuBar.add(mnEditFile);
		
		mnZip = new JMenu("Compress");
		mnZip.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setSelectedItems();
				
				ArrayList<File> items = m_appControl.getSelectedItems();
				if (items == null || items.size() == 0)
					return;
				
				DialogCompress dialog = new DialogCompress(m_appControl);
				dialog.setVisible(true);
				
				updateList(m_appControl.getCurrentDirectory());
			}
		});
		menuBar.add(mnZip);
		
		mnUnzip = new JMenu("Decompress");
		mnUnzip.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setSelectedItems();
				
				m_appControl.decompressItems();
				
				updateList(m_appControl.getCurrentDirectory());
			}
		});
		menuBar.add(mnUnzip);
		
		mnSplit = new JMenu("Split");
		mnSplit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setSelectedItems();
				
				ArrayList<File> items = m_appControl.getSelectedItems();
				if (items == null || items.size() == 0 || items.size() > 1)
					return;
				
				DialogSplitFile dialog = new DialogSplitFile(m_appControl);
				dialog.setVisible(true);
				
				updateList(m_appControl.getCurrentDirectory());
			}
		});
		menuBar.add(mnSplit);
		
		mnMerge = new JMenu("Merge");
		mnMerge.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setSelectedItems();
				
				m_appControl.mergeFile();
				
				updateList(m_appControl.getCurrentDirectory());
			}
		});
		menuBar.add(mnMerge);
		
		comboBox = new JComboBox();
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				onComboBoxItemChanged();
			}
		});
		frame.getContentPane().add(comboBox, BorderLayout.NORTH);
		
		scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		list = new JList();
		scrollPane.setViewportView(list);
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int idx = list.locationToIndex(e.getPoint());
				int clickCount = e.getClickCount();
				File item = m_child_list.get(idx);
				
				// double click
				if (clickCount >= 2) {
					// item == null: back to parent
					if (item == null) {
						File parent = m_appControl.getCurrentDirectory().getParentFile();
						
						if (parent != null) {
							m_appControl.setCurrentDirectory(parent);
							updateList(parent);
						}
					}
					// item is directory
					else if (item.isDirectory()) {
						m_appControl.setCurrentDirectory(item);
						updateList(item);
					}
					// item is file
					else {
						
					}
				}
			}
		});
	}

}
