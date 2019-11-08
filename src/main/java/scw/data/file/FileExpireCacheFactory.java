package scw.data.file;

import java.io.File;

import scw.core.Constants;
import scw.core.utils.SystemPropertyUtils;
import scw.data.AbstractExpireCacheFactory;
import scw.data.Cache;
import scw.io.serializer.NoTypeSpecifiedSerializer;
import scw.io.serializer.SerializerUtils;

public class FileExpireCacheFactory extends AbstractExpireCacheFactory{
	protected final NoTypeSpecifiedSerializer serializer;
	protected final String charsetName;
	protected final String cacheDirectory;

	protected FileExpireCacheFactory() {
		this.serializer = SerializerUtils.DEFAULT_SERIALIZER;
		this.charsetName = Constants.DEFAULT_CHARSET_NAME;
		this.cacheDirectory = SystemPropertyUtils.getTempDirectoryPath() + File.separator + getClass().getName();
	}

	public FileExpireCacheFactory(String cacheDirectory) {
		this(SerializerUtils.DEFAULT_SERIALIZER, Constants.DEFAULT_CHARSET_NAME, cacheDirectory);
	}

	public FileExpireCacheFactory(NoTypeSpecifiedSerializer serializer, String charsetName, String cacheDirectory) {
		this.serializer = serializer;
		this.charsetName = charsetName;
		this.cacheDirectory = cacheDirectory;
	}

	protected Cache createCache(int exp) {
		return new FileCache(exp, serializer, charsetName, cacheDirectory + File.separator + exp);
	}
}
