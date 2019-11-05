package scw.data.file;

import java.io.File;

import scw.core.Constants;
import scw.core.Converter;
import scw.core.utils.SystemPropertyUtils;
import scw.data.Cache;
import scw.io.SerializerUtils;
import scw.io.serializer.NoTypeSpecifiedSerializer;

public class AutoRefreshFileExpireCacheFactory extends FileExpireCacheFactory {
	private final Converter<String, Object> converter;

	public AutoRefreshFileExpireCacheFactory(Converter<String, Object> converter) {
		this(SystemPropertyUtils.getTempDirectoryPath() + File.separator + "auto_refresh_file_cache", converter);
	}

	public AutoRefreshFileExpireCacheFactory(String cacheDirectory, Converter<String, Object> converter) {
		this(SerializerUtils.DEFAULT_SERIALIZER, Constants.DEFAULT_CHARSET_NAME, cacheDirectory, converter);
	}

	public AutoRefreshFileExpireCacheFactory(NoTypeSpecifiedSerializer serializer, String charsetName, String cacheDirectory,
			Converter<String, Object> converter) {
		super(serializer, charsetName, cacheDirectory);
		this.converter = converter;
	}

	@Override
	protected Cache createCache(int exp) {
		return new AutoRefreshFileCache(exp, serializer, charsetName, cacheDirectory + File.separator + exp, converter);
	}
}
