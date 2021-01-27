package scw.data.file;

import java.io.File;

import scw.data.AbstractExpireCacheFactory;
import scw.data.ExpiredCache;
import scw.env.SystemEnvironment;
import scw.io.NoTypeSpecifiedSerializer;
import scw.io.SerializerUtils;

public class FileExpiredCacheFactory extends AbstractExpireCacheFactory{
	protected final NoTypeSpecifiedSerializer serializer;
	protected final String cacheDirectory;

	protected FileExpiredCacheFactory() {
		this.serializer = SerializerUtils.DEFAULT_SERIALIZER;
		this.cacheDirectory = SystemEnvironment.getInstance().getTempDirectoryPath() + File.separator + getClass().getName();
	}

	public FileExpiredCacheFactory(String cacheDirectory) {
		this(SerializerUtils.DEFAULT_SERIALIZER, cacheDirectory);
	}

	public FileExpiredCacheFactory(NoTypeSpecifiedSerializer serializer, String cacheDirectory) {
		this.serializer = serializer;
		this.cacheDirectory = cacheDirectory;
	}

	@Override
	protected ExpiredCache createExpiredCache(int exp) {
		return new FileCache(exp, serializer, cacheDirectory + File.separator + exp);
	}
}
