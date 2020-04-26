package scw.dss;

import java.io.InputStream;

import scw.net.message.InputMessage;

public interface Data extends InputMessage {
	/**
	 * 获取数据内容
	 */
	public InputStream getBody();

	/**
	 * 最后一次修改时间
	 * @return
	 */
	long lastModified();
}