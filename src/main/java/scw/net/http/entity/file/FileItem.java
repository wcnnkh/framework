package scw.net.http.entity.file;

import java.io.File;
import java.io.IOException;

import scw.common.utils.FileUtils;

public class FileItem{
	private File file;
	private String name;
	private String text;
	private String contentType;
	private boolean isFile;

	public FileItem(String name, String contentType) {
		this.name = name;
		this.contentType = contentType;
	}

	/**
	 * 写放指定文件
	 * @param toFile
	 * @throws IOException
	 */
	public void write(File toFile) throws IOException{
		FileUtils.copyFileUsingFileChannels(file, toFile);
	}
	
	/**
	 * 删除tempFile
	 */
	public void delete(){ 
		file.delete();
	}


	public String getName() {
		return name;
	}


	public String getText() {
		return text;
	}


	public String getContentType() {
		return contentType;
	}


	public boolean isFile() {
		return isFile;
	}
	
	public String getFileName(){
		return file.getName();
	}
	
	public void setFile(){
		//TODO
	}
	
	public void setText(){
		//TODO
	}
}
