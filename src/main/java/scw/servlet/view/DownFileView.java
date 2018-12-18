package scw.servlet.view;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.View;

public class DownFileView implements View{
	private String encoding;
	private int buffSize = 2048;
	private File file;
	
	public DownFileView(File file) {
		this.file = file;
	}
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setBuffSize(int buffSize) {
		this.buffSize = buffSize;
	}

	public void render(Request request, Response response) throws IOException{
		if(encoding != null){
			response.setCharacterEncoding(encoding);
		}
		
		response.setContentType(Files.probeContentType(file.toPath()));
		response.setHeader("Content-Disposition", "attachment;filename=" + new String(file.getName().getBytes(), "IOS-8859-1"));
		response.setBufferSize(buffSize);
		
		char[] c = new char[buffSize];
		FileReader fileReader = null;
		int len = 0;
		try {
			fileReader = new FileReader(file);
			while((len = fileReader.read(c)) != -1){
				response.getWriter().write(c, 0, len);
			}
		} catch (IOException e) {
			throw new IOException(e);
		}finally {
			if(fileReader != null){
				fileReader.close();
			}
		}
	}
}
