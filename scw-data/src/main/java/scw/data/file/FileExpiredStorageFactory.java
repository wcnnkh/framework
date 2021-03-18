package scw.data.file;

import java.io.File;

import scw.data.AbstractExpiredStorageFactory;
import scw.data.ExpiredStorage;
import scw.env.SystemEnvironment;
import scw.io.NoTypeSpecifiedSerializer;
import scw.io.SerializerUtils;

public class FileExpiredStorageFactory extends AbstractExpiredStorageFactory{
	protected final NoTypeSpecifiedSerializer serializer;
	protected final String cacheDirectory;

	protected FileExpiredStorageFactory() {
		this.serializer = SerializerUtils.DEFAULT_SERIALIZER;
		this.cacheDirectory = SystemEnvironment.getInstance().getTempDirectoryPath() + File.separator + getClass().getName();
	}

	public FileExpiredStorageFactory(String cacheDirectory) {
		this(SerializerUtils.DEFAULT_SERIALIZER, cacheDirectory);
	}

	public FileExpiredStorageFactory(NoTypeSpecifiedSerializer serializer, String cacheDirectory) {
		this.serializer = serializer;
		this.cacheDirectory = cacheDirectory;
	}

	@Override
	protected ExpiredStorage createExpiredCache(int exp) {
		return new FileCache(exp, serializer, cacheDirectory + File.separator + exp);
	}
}
