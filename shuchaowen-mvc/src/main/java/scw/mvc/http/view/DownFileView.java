package scw.mvc.http.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

import scw.io.IOUtils;
import scw.mvc.Channel;
import scw.mvc.http.HttpView;
import scw.net.http.MediaType;
import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.ServerHttpResponse;

public final class DownFileView extends HttpView {
	private String encoding;
	private File file;

	public DownFileView(File file) {
		this.file = file;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public void render(Channel channel, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) throws Throwable {
		MediaType mediaType = MediaType.parseMediaType(Files.probeContentType(file.toPath()));
		if(mediaType.getCharset() == null && encoding != null){
			mediaType = new MediaType(mediaType, encoding);
		}
		serverHttpResponse.setContentType(mediaType);
		setResponseFileDisposition(file.getName(), serverHttpResponse);

		FileInputStream fis = null;
		OutputStream os = null;
		try {
			os = serverHttpResponse.getBody();
			fis = new FileInputStream(file);
			IOUtils.write(fis, os, 1024);
		} finally {
			IOUtils.close(fis, os);
		}
	}
	
	public static void setResponseFileDisposition(String fileName, ServerHttpResponse serverHttpResponse) throws UnsupportedEncodingException{
		fileName = new String(fileName.getBytes(), "iso-8859-1");
		serverHttpResponse.getHeaders().set("Content-Disposition", "attachment;filename=" + fileName);
	}
}
