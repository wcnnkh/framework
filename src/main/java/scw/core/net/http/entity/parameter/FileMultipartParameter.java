package scw.core.net.http.entity.parameter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import scw.core.net.http.entity.file.File;

public class FileMultipartParameter extends AbstractMultipartParameter{
	private final File file;
	
	public FileMultipartParameter(Charset charset, String boundary, File file) {
		super(charset, boundary);
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public void write(OutputStream out) throws IOException {
		StringBuilder boundary = new StringBuilder();
		boundary.append(BOUNDARY_TAG).append(getBoundary()).append(BR);
		out.write(boundary.toString().getBytes(getCharset()));
		StringBuilder sb = new StringBuilder();
		sb.append("Content-Disposition: form-data;");
		sb.append("name=\"").append(file.name()).append("\";");
		sb.append("filelength=\"").append(file.length()).append("\";");
		sb.append("filename=\"").append(file.fileName()).append("\"");
		sb.append(BR);
		sb.append("Content-Type:").append(file.contentType());
		sb.append(BR);
		sb.append(BR);
		out.write(sb.toString().getBytes(getCharset()));
		InputStream is = (FileInputStream) file.inputStream();
		try {
			byte[] b = new byte[1024];
			int len;
			while ((len = is.read(b)) != -1) {
				out.write(b, 0, len);
			}
		} finally {
			is.close();
		}
		out.write(BR.getBytes("UTF-8"));
	}
}
