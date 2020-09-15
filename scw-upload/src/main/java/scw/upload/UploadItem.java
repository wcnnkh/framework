package scw.upload;

import java.io.IOException;
import java.io.InputStream;

import scw.http.HttpHeaders;

public interface UploadItem {
	String getName();

	HttpHeaders getHeaders();

	InputStream getBody() throws IOException;

	long size();
}