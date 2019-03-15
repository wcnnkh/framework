package scw.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;
import java.util.List;
import java.util.Map;

import scw.common.ByteArray;

public interface Request {
	void connect() throws IOException;

	void setConnectTimeout(int timeout);

	int getConnectTimeout();

	void setReadTimeout(int timeout);

	int getReadTimeout();

	URL getURL();

	int getContentLength();

	long getContentLengthLong();

	String getContentType();

	String getContentEncoding();

	long getExpiration();

	long getDate();

	long getLastModified();

	String getHeaderField(String name);

	Map<String, List<String>> getHeaderFields();

	int getHeaderFieldInt(String name, int Default);

	long getHeaderFieldLong(String name, long Default);

	long getHeaderFieldDate(String name, long Default);

	String getHeaderFieldKey(int n);

	String getHeaderField(int n);

	Object getContent() throws IOException;

	@SuppressWarnings("rawtypes")
	Object getContent(Class[] classes) throws IOException;

	Permission getPermission() throws IOException;

	InputStream getInputStream() throws IOException;

	OutputStream getOutputStream() throws IOException;

	void setDoInput(boolean doinput);

	boolean getDoInput();

	void setDoOutput(boolean dooutput);

	boolean getDoOutput();

	void setAllowUserInteraction(boolean allowuserinteraction);

	boolean getAllowUserInteraction();

	void setUseCaches(boolean usecaches);

	boolean getUseCaches();

	void setIfModifiedSince(long ifmodifiedsince);

	long getIfModifiedSince();

	boolean getDefaultUseCaches();

	void setDefaultUseCaches(boolean defaultusecaches);

	void setRequestProperty(String key, String value);

	void addRequestProperty(String key, String value);

	String getRequestProperty(String key);

	Map<String, List<String>> getRequestProperties();

	ByteArray getResponseByteArray() throws IOException;

	void setRequestEntity(RequestEntity entity) throws IOException;

	URLConnection getURLConnection();
}
