package scw.dss;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 数据存储系统
 * 
 * @author shuchaowen
 *
 */
public interface DataStorageSystem {
	/**
	 * 获取数据
	 * 
	 * @param key
	 * @return
	 * @throws DataStorageSystemException
	 */
	Data get(String key) throws DataStorageSystemException, IOException;

	/**
	 * 保存数据
	 * 
	 * @param key
	 *            数据的key
	 * @param input 输入数据
	 * @throws DataStorageSystemException
	 */
	boolean put(String key, InputStream input) throws DataStorageSystemException, IOException;

	/**
	 * 数据是否存在
	 * 
	 * @param key
	 * @return
	 * @throws DataStorageSystemException
	 */
	boolean isExist(String key) throws DataStorageSystemException, IOException;

	/**
	 * 获取数据列表
	 * @param keyPrefix key的前缀
	 * @param marker 指定key之后的数据
	 * @param limit 一次最多返回多少数据
	 * @return
	 */
	List<Data> getList(String keyPrefix, String marker, int limit) throws DataStorageSystemException, IOException;
	
	/**
	 * 删除数据
	 * @param key
	 * @return
	 * @throws DataStorageSystemException
	 */
	boolean delete(String key) throws DataStorageSystemException, IOException;
}
