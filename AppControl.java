package ddthach.homework01;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;;

public class AppControl {	
	private File m_root;
	private File m_current_directory;
	private ArrayList<File> m_buffers;
	private ArrayList<File> m_selected_items;
	
	public AppControl() {
		m_root = null;
		m_current_directory = null;
		m_buffers = null;
		m_selected_items = null;
	}
	
	public File getRoot() {
		return m_root;
	}
	
	public void setRoot(File root) {
		m_root = root;
		m_current_directory = root;
	}
	
	public File getCurrentDirectory() {
		return m_current_directory;
	}
	
	public void setCurrentDirectory(File directory) {
		m_current_directory = directory;
	}
	
	public File getCurrentItem() {
		// null || no element || more than 1 element
		if (m_selected_items == null || m_selected_items.size() == 0 || m_selected_items.size() > 1)
			return null;
		
		return m_selected_items.get(0);
	}
	
	public ArrayList<File> getSelectedItems() {
		return m_selected_items;
	}
	
	public void setSelectedItems(ArrayList<File> items) {
		m_selected_items = items;
	}
	
	public ArrayList<File> getBuffers() {
		return m_buffers;
	}
	
	public void setBuffer(ArrayList<File> buffers) {
		m_buffers = buffers;
	}
	
	public boolean renameItem(String name) {		
		File item = this.getCurrentItem();
		
		if (item == null)
			return false;
		
		String newItemPath = item.getParent() + File.separator + name;
		
		File newItem = new File(newItemPath);
		
		if (newItem == null)
			return false;
		
		if (item.renameTo(newItem)) {
			m_selected_items = null;
			return true;
		}
		
		return false;
	}
	
	public static boolean deleteItem(File dir) {
		if (dir == null)
			return false;
		
		if (dir.isDirectory()) {
	         String[] children = dir.list();
	         for (int i = 0; i < children.length; i++) {
	            boolean success = AppControl.deleteItem(new File(dir, children[i]));
	            if (!success) {
	               return false;
	            }
	         }
	      }
	      return dir.delete();
	}
	
	public boolean createFile(String fileName) {
		if (fileName == null || fileName.compareTo("") == 0)
			return false;
		
		String newPath = m_current_directory.getPath() + File.separator + fileName;
		
		File file = new File(newPath);
		
		if (file == null)
			return false;
		
		try {
			return file.createNewFile();
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public boolean createDirectory(String dirName) {
		if (dirName == null || dirName.compareTo("") == 0)
			return false;
		
		String newPath = m_current_directory.getPath() + File.separator + dirName;
		
		File dir = new File(newPath);
		
		if (dir == null)
			return false;
		
		try {
			return dir.mkdirs();
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public void copyItems() {
		if (m_selected_items == null || m_selected_items.size() == 0)
			return;
		
		m_buffers = m_selected_items;
	}
	
	public static void pasteItems(ArrayList<File> src, File destDirectory) {
		if (src == null || src.size()==0 || destDirectory == null)
			return;
		
		for (int i = 0; i < src.size(); i++) {
			File item = src.get(i);
			
			if (item != null) {
				String destPath = destDirectory + File.separator + item.getName();
				File dest = new File(destPath);
				
				if (dest != null) {
					try {
						Files.copy(item.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
						
						if (item.isDirectory()) {
							File[] childs = item.listFiles();
							
							if (childs != null && childs.length > 0) {
								ArrayList<File> childsList = new ArrayList<>();
								for (File child : childs)
									childsList.add(child);
								
								AppControl.pasteItems(childsList, dest);
							}
						}
					}
					catch (Exception e) {
						// do nothing
					}
				}
			}
		}
	}
	
	public void compressItems(String fileName) {
		if (fileName == null || fileName.compareTo("") == 0)
			return;
		
		fileName = fileName + ".zip";
		
		String filePath = m_current_directory + File.separator + fileName;
		
		try {
			FileOutputStream fos = new FileOutputStream(filePath);
			
			if (fos == null)
				return;
			
			ZipOutputStream zos = new ZipOutputStream(fos);
			
			if (zos == null)
				return;
			
			for (int i = 0; i < m_selected_items.size(); i++) {
				File item = m_selected_items.get(i);
				
				if (item != null) {
					if (item.isDirectory())
						AppControl.addFodlerToZip(item, zos, "");
					else
						AppControl.addFileToZip(item, zos, "");
				}
			}
			
			zos.close();
			fos.close();
		}
		catch (Exception e) {
			// do nothing
		}		
	}
	
	public static void addFodlerToZip(File item, ZipOutputStream zos, String prefix) {
		if (item == null || zos == null)
			return;
		
		if (!item.isDirectory())
			return;
		
		File[] childs = item.listFiles();
		
		if (prefix.compareTo("") != 0)
			prefix = prefix + File.separator + item.getName();
		else
			prefix = item.getName();
		
		if (childs == null || childs.length == 0)
			return;
		
		for (int i = 0; i < childs.length; i++) {
			File child = childs[i];
			
			if (child != null) {
				if (child.isDirectory())
					AppControl.addFodlerToZip(child, zos, prefix);
				else
					AppControl.addFileToZip(child, zos, prefix);
			}
		}
	}
	
	public static void addFileToZip(File item, ZipOutputStream zos, String prefix) {
		if (item == null || zos == null)
			return;
		
		try {			
			FileInputStream fis = new FileInputStream(item);
			
			if (fis == null)
				return;
			
			String entryName;
			
			if (prefix.compareTo("") != 0)
				entryName = prefix + File.separator + item.getName();
			else
				entryName = item.getName();
			
			ZipEntry zipEntry = new ZipEntry(entryName);
			
			zos.putNextEntry(zipEntry);
			
			byte[] bytes = new byte[1024];
			int length = 0;
			
			while ((length = fis.read(bytes)) > 0) {
				zos.write(bytes, 0, length);
			}
			
			zos.closeEntry();
			fis.close();
		}
		catch (Exception e) {
			// do nothing
		}
	}
	
	public void decompressItems() {
		if (m_selected_items == null || m_selected_items.size() == 0)
			return;
		
		for (int i = 0; i < m_selected_items.size(); i++) {
			File item = m_selected_items.get(i);
			
			if (item != null)
				extractZip(item);
		}
	}
	
	public void extractZip(File file) {
		if (file == null)
			return;
		
		try {
			ZipInputStream zipInput = new ZipInputStream(new FileInputStream(file));
			
			ZipEntry zipEntry = zipInput.getNextEntry();
			
			while (zipEntry != null) {				
				String newPath = m_current_directory + File.separator + zipEntry.getName();
				
				File newItem = new File(newPath);
				
				// current zipEntry is a directory
				if (newPath.endsWith(File.separator)) {
					newItem.mkdirs();
					continue;
				}
				
				// current zipEntry is a file
				File parent = newItem.getParentFile();
				if (parent != null)
					parent.mkdirs();
				
				FileOutputStream fos = new FileOutputStream(newItem);
				byte[] bytes = new byte[1024];
				int len = 0;
				
				while ((len = zipInput.read(bytes)) > 0) {
					fos.write(bytes, 0, len);
				}
				
				fos.close();
				zipEntry = zipInput.getNextEntry();
			}
			
			zipInput.closeEntry();
			zipInput.close();
		}
		catch (Exception e) {
			// do nothing
		}		
	}
	
	public ArrayList<String> getTextData() {
		ArrayList<String> result = null;
		
		if (m_selected_items == null || m_selected_items.size() == 0 || m_selected_items.size() > 1)
			return result;
		
		File item = m_selected_items.get(0);
		if (!item.getName().endsWith(".txt"))
			return result;
		
		try {
			FileReader fileReader = new FileReader(item);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			result = new ArrayList<String>();
			
			while (true) {
				String line = bufferedReader.readLine();
				if (line == null)
					break;
				
				result.add(line);
			}
			
			bufferedReader.close();
			fileReader.close();
			
			return result;
		}
		catch (Exception e) {
			// do nothing
		}
		
		return result;
	}
	
	public void writeTextData(String data) {		
		if (m_selected_items == null || m_selected_items.size() == 0 || m_selected_items.size() > 1)
			return;
		
		File item = m_selected_items.get(0);
		if (!item.getName().endsWith(".txt"))
			return;
		
		try {
			FileWriter fileWriter = new FileWriter(item);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			
			bufferedWriter.flush();
			bufferedWriter.write(data);
			
			bufferedWriter.close();
			fileWriter.close();
		}
		catch (Exception e) {
			// do nothing
		}
	}
	
	public static String getFileName(File file) {
		String result = null;
		
		if (file == null || file.isDirectory())
			return result;
		
		String src = file.getName();
		
		int idx = -1;
		for (int i = src.length() - 1; i >= 0; i--) {
			if (src.charAt(i) == '.') {
				idx = i;
				break;
			}
		}
		
		if (idx == -1)
			return result;
		
		result = "";
		for (int i = 0; i < idx; i++)
			result += src.charAt(i);
		
		return result;
	}
	
	public void splitFile(int nFiles) {
		File file = this.getCurrentItem();
		
		if (file == null || file.isDirectory() || nFiles < 2)
			return;
		
		long fileSize = file.length();
		long splittedSize = fileSize / nFiles;
		
		if (splittedSize < 0)
			return;
		
		try {
			FileInputStream fis = new FileInputStream(file);
			
			for (int i = 0; i < nFiles; i++) {
				if (i == nFiles - 1) {
					long newSize = fileSize - (nFiles - 1) * splittedSize;
					this.writeSplittedFile(fis, file.getPath(), i, newSize);
				}
				else {
					this.writeSplittedFile(fis, file.getPath(), i, splittedSize);
				}
			}
			
			fis.close();
		}
		catch (Exception ex) {
			// do nothing
		}
	}
	
	public void writeSplittedFile(FileInputStream fis, String srcPath, int id, long splittedSize) {
		if (fis == null || srcPath == null || srcPath.compareTo("") == 0
				|| id < 0 || splittedSize < 0)
			return;
		
		String newPath = srcPath + "." + id;
		File splittedFile = new File(newPath);
		
		if (splittedFile == null)
			return;
		
		try {
			FileOutputStream fos = new FileOutputStream(splittedFile);
			
			int len = 0;
			int count = 0;
			byte[] bytes = new byte[1024];
			
			while (count <= splittedSize) {
				if ((len = fis.read(bytes)) > 0) {
					count += len;
					
					fos.write(bytes, 0, len);
				}
				else
					break;
			}
			
			fos.close();
		}
		catch (Exception ex) {
			// do nothing
		}
	}
	
	public void mergeFile() {
		if (m_selected_items == null || m_selected_items.size() < 2)
			return;
		
		// check all selected files have the same prefix name
		for (int i = 0; i < m_selected_items.size() - 1; i++) {
			String current = AppControl.getFileName(m_selected_items.get(i));
			String next = AppControl.getFileName(m_selected_items.get(i + 1));
			
			if (current == null || next == null)
				return;
			
			if (current.compareTo(next) != 0)
				return;
		}
		
		String destName = AppControl.getFileName(m_selected_items.get(0));
		String destPath = m_current_directory + File.separator + destName;
		
		File dest = new File(destPath);
		if (dest == null)
			return;
		
		try {
			FileOutputStream fos = new FileOutputStream(dest);
			
			for (File mergedFile : m_selected_items) {
				this.readMergedFile(fos, mergedFile);
			}
			
			fos.close();
		}
		catch (Exception ex) {
			// do nothing
		}
	}
	
	public void readMergedFile(FileOutputStream fos, File mergedFile) {
		if (fos == null || mergedFile == null)
			return;
		
		try {
			FileInputStream fis = new FileInputStream(mergedFile);
			
			int len = 0;
			byte[] bytes = new byte[1024];
			
			while ((len = fis.read(bytes)) > 0) {
				fos.write(bytes, 0, len);
			}
			
			fis.close();
		}
		catch (Exception ex) {
			// do nothing
		}
	}
}
