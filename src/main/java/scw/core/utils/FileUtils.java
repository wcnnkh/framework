package scw.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import scw.core.Base64;
import scw.core.Constants;

public final class FileUtils {
	private FileUtils() {
	};

	public static void toFile(String pathName, InputStream is) {
		OutputStream os = null;
		try {
			File file = new File(pathName);
			if (file.exists()) {
				file.delete();
			}

			os = new FileOutputStream(file);
			IOUtils.write(os, is, 10 * 1024 * 1024, -1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public static String pathToName(String path) {
		int index = path.indexOf("/");
		if (index != -1) {
			index++;
			return path.substring(index);
		}
		return path;
	}

	public static void downUrlFile(String url, String pathName) {
		try {
			URL connUrl = new URL(url);
			URLConnection conn = connUrl.openConnection();
			InputStream is = conn.getInputStream();
			toFile(pathName, is);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static boolean unZip(String zipPath, String toPath) {
		try {
			File zipF = new File(zipPath);
			if (!zipF.exists()) {
				return false;
			}

			File to = new File(toPath);
			if (!to.exists()) {
				to.mkdirs();
			}

			toPath = to.getPath() + File.separator;
			ZipFile zipFile = new ZipFile(zipPath);
			Enumeration<? extends ZipEntry> ens = zipFile.entries();
			ZipEntry zipEntry = null;
			while (ens.hasMoreElements()) {
				zipEntry = ens.nextElement();
				String dirName = zipEntry.getName();
				if (zipEntry.isDirectory()) {
					// dirName = dirName.substring(0, dirName.length() - 1);
					File f = new File(toPath + dirName);
					f.mkdirs();
				} else {
					String strFilePath = toPath + dirName;
					File f = new File(strFilePath);
					/*
					 * if(!f.exists()){ String[] arrFolderName =
					 * dirName.split("/"); StringBuilder sb = new
					 * StringBuilder(); sb.append(toPath + File.separator);
					 * for(int i=0; i<(arrFolderName.length - 1); i++){
					 * sb.append(arrFolderName[i]); sb.append(File.separator); }
					 * 
					 * File tempDir = new File(sb.toString()); tempDir.mkdir();
					 * }
					 */

					f.createNewFile();

					InputStream is = zipFile.getInputStream(zipEntry);
					FileOutputStream fos = new FileOutputStream(f);
					int len;
					byte[] by = new byte[1024];
					while ((len = is.read(by)) != -1) {
						fos.write(by, 0, len);
					}
					fos.flush();
					fos.close();
					is.close();
				}
			}
			zipFile.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 将文件分割符换成与当前操作系统一致
	 * 
	 * @param path
	 * @return
	 */
	public static String replaceSeparator(String path) {
		if (path == null) {
			return path;
		}

		if (File.separator.equals("/")) {
			return path.replaceAll("\\\\", "/");
		} else {
			return path.replaceAll("/", "\\\\");
		}
	}

	/**
	 * 查找文件
	 * 
	 * @param fileName
	 *            要查找的文件名 不包含目录
	 * @param rootPath
	 *            父目录
	 * @param recursion
	 *            是否递归
	 * @return
	 */
	public static String searchFileName(String fileName, String rootPath, boolean recursion) {
		File file = new File(rootPath);
		for (File f : file.listFiles()) {
			if (recursion) {
				if (f.isFile() && f.getName().equals(fileName)) {
					return f.getPath();
				}
			} else {
				if (f.isFile()) {
					if (f.getName().equals(fileName)) {
						return f.getPath();
					}
				} else {
					return searchFileName(fileName, f.getPath(), recursion);
				}
			}
		}
		return null;
	}

	public static List<String> getFileList(String rootPath) {
		rootPath = replaceSeparator(rootPath);
		List<String> list = new ArrayList<String>();

		File rootFile = new File(rootPath);
		if (rootFile.exists()) {
			File[] files = rootFile.listFiles();
			for (File file : files) {
				if (file.isFile()) {
					list.add(file.getPath());
				} else {
					list.addAll(getFileList(file.getPath()));
				}
			}
		}
		return list;
	}

	/**
	 * 在目录下递归搜索指定目录
	 * 
	 * @param rootPath
	 * @param directoryName
	 * @return
	 */
	public static File searchDirectory(String rootPath, String directoryName) {
		File rootFile = new File(rootPath);
		if (rootFile.exists()) {
			File[] files = rootFile.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					if (file.getName().equals(directoryName)) {
						return file;
					}

					File f = searchDirectory(file.getPath(), directoryName);
					if (f != null) {
						return f;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 在目录下递归搜索指定文件
	 * 
	 * @param rootPath
	 * @param searchFileName
	 * @return
	 */
	public static File searchFile(String rootPath, String searchFileName) {
		File rootFile = new File(rootPath);
		if (rootFile.exists()) {
			File[] files = rootFile.listFiles();
			for (File file : files) {
				if (file.isFile()) {
					if (file.getName().equals(searchFileName)) {
						return file;
					} else {
						continue;
					}
				} else {
					return searchFile(file.getPath(), searchFileName);
				}
			}
		}
		return null;
	}

	public static String toBase64(String filePath, StringBuilder data) {
		FileInputStream fis = null;
		byte[] b = new byte[1024];
		int len = 0;
		if (data == null) {
			data = new StringBuilder();
		}

		try {
			fis = new FileInputStream(filePath);
			while ((len = fis.read(b)) != -1) {
				data.append(Base64.encode(Arrays.copyOf(b, len)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		return data.toString();
	}

	public static String getFileSuffix(String filePath) {
		int sufIndex = filePath.lastIndexOf(".");
		if (sufIndex != -1) {
			return filePath.substring(sufIndex + 1);
		}
		return null;
	}

	public static boolean copyFile(String oldFile, String newFile) {
		Path oldP = Paths.get(oldFile);
		Path newP = Paths.get(newFile);
		if (oldP.toFile().exists()) {
			try {
				Files.copy(oldP, newP, StandardCopyOption.REPLACE_EXISTING);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static void copyFileUsingFileChannels(File source, File dest) throws IOException {
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(source);
			fos = new FileOutputStream(dest);
			inputChannel = fis.getChannel();
			outputChannel = fos.getChannel();
			outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
		} finally {
			fis.close();
			fos.close();
			inputChannel.close();
			outputChannel.close();
		}
	}

	public static boolean moveFile(String oldFile, String newFile) {
		Path oldP = Paths.get(oldFile);
		Path newP = Paths.get(newFile);
		if (oldP.toFile().exists()) {
			try {
				Files.move(oldP, newP, StandardCopyOption.REPLACE_EXISTING);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static String readerFileContent(File file, String charsetName) {
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fileInputStream, Charset.forName(charsetName));
			return IOUtils.read(isr, 256, 0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static <T> T readObject(File file) throws IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			return Constants.DEFAULT_SERIALIZER.deserialize(fis);
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
	}

	public static void writeObject(File file, Object obj) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			Constants.DEFAULT_SERIALIZER.serialize(fos, obj);
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}

	public static List<String> getFileContentLineList(File file, String charsetName) {
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, charsetName);
			br = new BufferedReader(isr);
			return IOUtils.readLineList(br, -1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeReader(br, isr);
			IOUtils.closeInputStream(fis);
		}
	}

	public static void writeFileContent(String filePath, String content, String charsetName) {
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		writeFileContent(file, content, charsetName);
	}

	public static void writeFileContent(File file, String content, String charsetName) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(content.getBytes(charsetName));
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeOutputStream(fos);
		}
	}
}
