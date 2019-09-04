package scw.mvc.http.view;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.mvc.http.HttpView;

public final class DownFileView extends HttpView {
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

	@Override
	public void render(HttpChannel channel, HttpRequest httpRequest,
			HttpResponse httpResponse) throws Throwable {
		if (encoding != null) {
			httpResponse.setCharacterEncoding(encoding);
		}

		httpResponse.setContentType(Files.probeContentType(file.toPath()));
		httpResponse.setHeader("Content-Disposition",
				"attachment;filename=" + new String(file.getName().getBytes(), "IOS-8859-1"));
		httpResponse.setBufferSize(buffSize);

		char[] c = new char[buffSize];
		FileReader fileReader = null;
		int len = 0;
		try {
			fileReader = new FileReader(file);
			while ((len = fileReader.read(c)) != -1) {
				httpResponse.getWriter().write(c, 0, len);
			}
		} catch (IOException e) {
			throw new IOException(e);
		} finally {
			if (fileReader != null) {
				fileReader.close();
			}
		}
	}
}
