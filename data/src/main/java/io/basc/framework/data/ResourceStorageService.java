package io.basc.framework.data;

import io.basc.framework.http.HttpRequestEntity;
import io.basc.framework.io.Resource;
import io.basc.framework.lang.Nullable;
import io.basc.framework.net.message.InputMessage;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.List;

/**
 * 资源存储服务
 * 
 * @author shuchaowen
 *
 */
public interface ResourceStorageService {
	/**
	 * 获取
	 * 
	 * @param key
	 * @return
	 * @throws DataException
	 */
	Resource get(String key) throws DataException, IOException;

	/**
	 * 上传
	 * 
	 * @param key
	 * @param input
	 * @return
	 * @throws DataException
	 * @throws IOException
	 */
	boolean put(String key, InputMessage input) throws DataException, IOException;

	/**
	 * 删除
	 * 
	 * @param key
	 * @return
	 * @throws DataException
	 */
	boolean delete(String key) throws DataException;

	/**
	 * 根据uri删除
	 * 
	 * @param uri
	 * @return
	 * @throws DataException
	 */
	boolean delete(URI uri) throws DataException;

	/**
	 * 列出指定规则的资源
	 * 
	 * @param prefix
	 * @param marker
	 * @param limit
	 * @return
	 * @throws DataException
	 */
	List<Resource> list(@Nullable String prefix, @Nullable String marker, int limit)
			throws DataException, IOException;

	/**
	 * 生成上传策略<br/>
	 * 另外，此方法和put方法的区别最大的区别是当存储服务使用的是第三方实现时<br/>
	 * put方法是使用服务器带宽进行上传,而此方法使用的是第三方服务的带宽
	 * 
	 * @param key
	 * @param expiration 到期时间点
	 * @return
	 * @throws DataException
	 */
	UploadPolicy generatePolicy(String key, Date expiration) throws DataException;

	static final class UploadPolicy {
		private final HttpRequestEntity<?> policy;
		private final String url;

		public UploadPolicy(String url, HttpRequestEntity<?> policy) {
			this.url = url;
			this.policy = policy;
		}

		public HttpRequestEntity<?> getPolicy() {
			return policy;
		}

		public String getUrl() {
			return url;
		}
	}
}
