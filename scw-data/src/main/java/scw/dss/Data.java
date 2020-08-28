package scw.dss;

import java.io.IOException;
import java.io.InputStream;

public interface Data {
	/**
	 * 数据的key
	 * @return
	 */
	String getKey();
	
	/**
	 * 获取数据内容
	 * @return
	 * @throws IOException
	 */
	InputStream getBody() throws IOException;
	
	/**
	 * 获取内容长度
	 * @return
	 */
	long getContentLength();

	/**
	 * 最后一次修改时间
	 * @return
	 */
	long lastModified() throws IOException;
}