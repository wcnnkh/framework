package scw.data.file;

import scw.beans.annotation.AutoImpl;
import scw.data.AutoRefreshCache;
import scw.data.CacheFactory;

@AutoImpl({ DefaultFileCacheFactory.class })
public interface FileCacheFactory extends CacheFactory<AutoRefreshCache> {
}
