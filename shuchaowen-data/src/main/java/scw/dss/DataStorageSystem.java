package scw.dss;

import java.util.concurrent.TimeUnit;

/**
 * 数据存储系统
 * @author shuchaowen
 *
 */
public interface DataStorageSystem {
	/**
	 * 获取数据
	 * @param key
	 * @return
	 * @throws DataStorageSystemException
	 */
	Data get(String key) throws DataStorageSystemException;

	/**
	 * 保存数据
	 * 
	 * @param key
	 *            数据的key
	 * @param data
	 *            数据
	 * @param cover
	 *            如果数据存在是否覆盖
	 * @return 是否保存成功(例如：如果cover为false, 此时数据已经存在那么返回false)
	 * @throws DataStorageSystemException
	 */
	boolean set(String key, Data data, boolean cover)
			throws DataStorageSystemException;

	/**
	 * 为数据设置过期时间
	 * 
	 * @param key
	 * @param expire
	 *            多久后过期
	 * @param timeUnit
	 *            过期时间单位
	 * @return 设置成功返回treu 设置失败返回false
	 * @throws DataStorageSystemException
	 */
	boolean setExpire(String key, long expire, TimeUnit expireTimeUnit)
			throws DataStorageSystemException;

	/**
	 * 获取数据的过期时间
	 * 
	 * @param key
	 * @return 单位:毫秒
	 */
	long getExpire(String key);

	/**
	 * 数据是否存在
	 * 
	 * @param key
	 * @return
	 * @throws DataStorageSystemException
	 */
	boolean isExist(String key) throws DataStorageSystemException;
}
