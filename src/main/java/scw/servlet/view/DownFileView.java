package scw.servlet.view;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.http.HttpServletResponse;

import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.ServletUtils;
import scw.servlet.View;

public class DownFileView implements View {
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

	public void render(Request request, Response response) throws Exception {
		if (!ServletUtils.isHttpServlet(request, response)) {
			return;
		}

		if (encoding != null) {
			response.setCharacterEncoding(encoding);
		}

		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		httpServletResponse.setContentType(Files.probeContentType(file.toPath()));
		httpServletResponse.setHeader("Content-Disposition",
				"attachment;filename=" + new String(file.getName().getBytes(), "IOS-8859-1"));
		httpServletResponse.setBufferSize(buffSize);

		char[] c = new char[buffSize];
		FileReader fileReader = null;
		int len = 0;
		try {
			fileReader = new FileReader(file);
			while ((len = fileReader.read(c)) != -1) {
				response.getWriter().write(c, 0, len);
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
