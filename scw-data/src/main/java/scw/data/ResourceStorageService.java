package scw.data;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.List;

import scw.http.HttpRequestEntity;
import scw.io.Resource;
import scw.lang.Nullable;
import scw.net.message.InputMessage;

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
	 * @throws StorageException
	 */
	Resource get(String key) throws StorageException, IOException;

	/**
	 * 上传
	 * 
	 * @param key
	 * @param input
	 * @return
	 * @throws StorageException
	 * @throws IOException
	 */
	boolean put(String key, InputMessage input) throws StorageException, IOException;

	/**
	 * 删除
	 * 
	 * @param key
	 * @return
	 * @throws StorageException
	 */
	boolean delete(String key) throws StorageException;

	/**
	 * 根据uri删除
	 * 
	 * @param uri
	 * @return
	 * @throws StorageException
	 */
	boolean delete(URI uri) throws StorageException;

	/**
	 * 列出指定规则的资源
	 * 
	 * @param prefix
	 * @param marker
	 * @param limit
	 * @return
	 * @throws StorageException
	 */
	List<Resource> list(@Nullable String prefix, @Nullable String marker, int limit)
			throws StorageException, IOException;

	/**
	 * 生成上传策略<br/>
	 * 另外，此方法和put方法的区别最大的区别是当存储服务使用的是第三方实现时<br/>
	 * put方法是使用服务器带宽进行上传,而此方法使用的是第三方服务的带宽
	 * 
	 * @param key
	 * @param expiration 到期时间点
	 * @return
	 * @throws StorageException
	 */
	UploadPolicy generatePolicy(String key, Date expiration) throws StorageException;

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
