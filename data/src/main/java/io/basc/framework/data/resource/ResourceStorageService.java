package io.basc.framework.data.resource;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.List;

import io.basc.framework.data.DataException;
import io.basc.framework.lang.Nullable;
import io.basc.framework.net.InputMessage;
import io.basc.framework.util.io.Resource;

/**
 * 资源存储服务
 * 
 * @author wcnnkh
 *
 */
public interface ResourceStorageService {
	Resource get(String key) throws DataException, IOException;

	boolean put(String key, InputMessage input) throws DataException, IOException;

	boolean delete(String key) throws DataException;

	boolean delete(URI uri) throws DataException;

	List<Resource> list(@Nullable String prefix, @Nullable String marker, int limit) throws DataException, IOException;

	/**
	 * 生成上传策略 另外，此方法和put方法的区别最大的区别是当存储服务使用的是第三方实现时
	 * put方法是使用服务器带宽进行上传,而此方法使用的是第三方服务的带宽
	 * 
	 * @param key        key
	 * @param expiration 到期时间点
	 * @return 返回上传策略
	 * @throws DataException 获取策略失败
	 */
	ResourceUploadPolicy generatePolicy(String key, Date expiration) throws DataException;
}
