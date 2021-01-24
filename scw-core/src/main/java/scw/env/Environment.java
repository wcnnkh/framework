package scw.env;

import java.nio.charset.Charset;

public interface Environment extends BasicEnvironment,
		EnvironmentResourceLoader {
	public static final String WORK_PATH_PROPERTY = "work.path";
	public static final String CHARSET_PROPERTY = "charset.name";
	
	String getCharsetName();

	Charset getCharset();
}