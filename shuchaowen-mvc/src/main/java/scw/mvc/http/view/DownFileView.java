package scw.mvc.http.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

import scw.io.IOUtils;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.mvc.http.HttpView;

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
	public void render(HttpChannel channel, HttpRequest httpRequest, HttpResponse httpResponse) throws Throwable {
		if (encoding != null) {
			httpResponse.setCharacterEncoding(encoding);
		}

		httpResponse.setContentType(Files.probeContentType(file.toPath()));
		setResponseFileDisposition(file.getName(), httpResponse);

		FileInputStream fis = null;
		OutputStream os = null;
		try {
			os = httpResponse.getBody();
			fis = new FileInputStream(file);
			IOUtils.write(fis, os, 1024);
		} finally {
			IOUtils.close(fis, os);
		}
	}
	
	public static void setResponseFileDisposition(String fileName, HttpResponse httpResponse) throws UnsupportedEncodingException{
		fileName = new String(fileName.getBytes(), "iso-8859-1");
		httpResponse.getHeaders().set("Content-Disposition", "attachment;filename=" + fileName);
	}
}
