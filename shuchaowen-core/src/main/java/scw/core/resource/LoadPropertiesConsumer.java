package scw.core.resource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.Properties;

import scw.core.Consumer;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.StringUtils;
import scw.io.IOUtils;
import scw.util.FormatUtils;

public class LoadPropertiesConsumer implements Consumer<InputStream> {
	private Properties properties;
	private String name;
	private String charsetName;

	public LoadPropertiesConsumer(Properties properties, String name, String charsetName) {
		this.properties = properties;
		this.name = name;
		this.charsetName = charsetName;
	}

	public void consume(InputStream inputStream) throws Exception {
		if (name.endsWith(".xml")) {
			properties.loadFromXML(inputStream);
		} else {
			if (StringUtils.isEmpty(charsetName)) {
				properties.load(inputStream);
			} else {
				Method method = ReflectionUtils.getMethod(Properties.class, "load", Reader.class);
				if (method == null) {
					FormatUtils.warn(LoadPropertiesConsumer.class, "jdk1.6及以上的版本才支持指定字符集: {}" + name);
					properties.load(inputStream);
				} else {
					InputStreamReader isr = null;
					try {
						isr = new InputStreamReader(inputStream, charsetName);
						method.invoke(properties, isr);
					} finally {
						IOUtils.close(isr);
					}
				}
			}
		}
	}

}
