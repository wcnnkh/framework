package scw.data.file;

import java.io.File;

import scw.core.Constants;
import scw.core.Converter;
import scw.core.GlobalPropertyFactory;
import scw.data.ExpiredCache;
import scw.data.ExpiredCacheFactory;
import scw.io.NoTypeSpecifiedSerializer;
import scw.io.SerializerUtils;

public class AutoRefreshFileExpiredCacheFactory extends FileExpiredCacheFactory {
	private final Converter<String, ?> converter;

	protected AutoRefreshFileExpiredCacheFactory(Converter<String, ?> converter) {
		super();
		this.converter = converter;
	}
	
	public AutoRefreshFileExpiredCacheFactory(String cacheDirectory, Converter<String, ?> converter) {
		this(SerializerUtils.DEFAULT_SERIALIZER, Constants.DEFAULT_CHARSET_NAME, cacheDirectory, converter);
	}

	public AutoRefreshFileExpiredCacheFactory(NoTypeSpecifiedSerializer serializer, String charsetName, String cacheDirectory,
			Converter<String, ?> converter) {
		super(serializer, charsetName, cacheDirectory);
		this.converter = converter;
	}

	@Override
	protected ExpiredCache createExpiredCache(int exp) {
		return new AutoRefreshFileCache(exp, serializer, charsetName, cacheDirectory + File.separator + exp, converter);
	}
	
	public static ExpiredCacheFactory create(String cacheDirectorySuffix, Converter<String, ?> converter){
		return new AutoRefreshFileExpiredCacheFactory(GlobalPropertyFactory.getInstance().getTempDirectoryPath() + File.separator + cacheDirectorySuffix, converter);
	}
}
