package scw.net.http.server.mvc.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

import scw.io.IOUtils;
import scw.net.http.MediaType;
import scw.net.http.server.ServerHttpResponse;
import scw.net.http.server.mvc.HttpChannel;

public final class DownFileView implements View {
	private String encoding;
	private File file;

	public DownFileView(File file) {
		this.file = file;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void render(HttpChannel httpChannel) throws IOException {
		MediaType mediaType = MediaType.parseMediaType(Files.probeContentType(file.toPath()));
		if(mediaType.getCharset() == null && encoding != null){
			mediaType = new MediaType(mediaType, encoding);
		}
		httpChannel.getResponse().setContentType(mediaType);
		setResponseFileDisposition(file.getName(), httpChannel.getResponse());

		FileInputStream fis = null;
		OutputStream os = null;
		try {
			os = httpChannel.getResponse().getBody();
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
