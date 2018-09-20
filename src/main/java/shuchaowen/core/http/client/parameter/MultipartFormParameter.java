package shuchaowen.core.http.client.parameter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import shuchaowen.core.http.client.parameter.file.File;
import shuchaowen.core.http.client.parameter.file.Param;

public class MultipartFormParameter implements Parameter {
	public static final String BOUNDARY = "----WebKitFormBoundaryKSD2ndz6G9RPNjx0";
	private static final String BOUNDARY_TAG = "--";
	private static final String BR = "\r\n";
	
	private String charsetName;
	private List<File> fileList = new ArrayList<File>();
	private List<Param> textList = new ArrayList<Param>();

	public MultipartFormParameter(String charsetName) {
		this.charsetName = charsetName;
	}

	public void setFileList(List<File> fileList) {
		this.fileList = fileList;
	}

	public void setTextList(List<Param> textList) {
		this.textList = textList;
	}

	public void addText(String key, String value) {
		textList.add(new Param(key, value));
	}

	public void addFile(File file) {
		fileList.add(file);
	}

	public void wrapper(OutputStream os) throws IOException {
		if(fileList != null){
			Iterator<File> iterator = fileList.iterator();
			while (iterator.hasNext()) {
				File file = iterator.next();
				StringBuilder boundary = new StringBuilder();
				boundary.append(BOUNDARY_TAG).append(BOUNDARY).append(BR);
				os.write(boundary.toString().getBytes(charsetName));
				StringBuilder sb = new StringBuilder();
				sb.append("Content-Disposition: form-data;");
				sb.append("name=\"").append(file.name()).append("\";");
				sb.append("filelength=\"").append(file.length()).append("\";");
				sb.append("filename=\"").append(file.fileName()).append("\"");
				sb.append(BR);
				sb.append("Content-Type:").append(file.contentType());
				sb.append(BR);
				sb.append(BR);
				os.write(sb.toString().getBytes(charsetName));
				InputStream is = (FileInputStream) file.inputStream();
				byte[] b = new byte[1024];
				int len;
				while ((len = is.read(b)) != -1) {
					os.write(b, 0, len);
				}
				is.close();
				os.write(BR.getBytes("UTF-8"));
			}
		}
		
		if(textList != null){
			Iterator<Param> iterator = textList.iterator();
			while(iterator.hasNext()){
				Param param = iterator.next();
				if(param == null){
					continue;
				}
				
				String[] vals = param.getValue();
				for(String val : vals){
					StringBuilder sb = new StringBuilder();
					sb.append(BOUNDARY_TAG).append(BOUNDARY).append(BR);
					sb.append("Content-Disposition: form-data;");
					sb.append("name=\"").append(param.getKey()).append("\";");
					sb.append(BR);
					sb.append(BR);
					sb.append(val);
					os.write(sb.toString().getBytes(charsetName));
					os.write(BR.getBytes(charsetName));
				}
			}
		}
		
		StringBuilder end = new StringBuilder();
		end.append(BR);
		end.append(BOUNDARY_TAG);
		end.append(BOUNDARY);
		end.append(BOUNDARY_TAG);
		end.append(BR);
		os.write(end.toString().getBytes(charsetName));
	}
}
