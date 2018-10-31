package shuchaowen.memcached;

import shuchaowen.core.exception.ShuChaoWenRuntimeException;

public class MemcachedQueue {
	private static final String READ_KEY = "_read";
	private static final String WRITE_KEY = "_write";
	private static final String WRITE_INDEX_KEY = "_write_index";
	private static final String READ_LOCK_KEY = "_read_lock";

	private Memcached memcached;
	private String keyPrefix;

	public MemcachedQueue(String keyPrefix, Memcached memcached) {
		this.keyPrefix = keyPrefix;
		this.memcached = memcached;
		memcached.add(keyPrefix + READ_KEY, 0 + "");
		memcached.add(keyPrefix + WRITE_INDEX_KEY, 0 + "");
	}

	private boolean checkCanRead() {
		long readIndex = Integer.parseInt((String) memcached.get(keyPrefix + READ_KEY));
		Long writeIndex = memcached.get(keyPrefix + WRITE_KEY);
		if(writeIndex == null){
			return false;
		}
		
		if (writeIndex < 0) {
			if (readIndex >= 0) {
				return true;
			}
		}
		return writeIndex > readIndex;
	}
	
	

	/**
	 * 为了提高性能这里不对因一直没有取出数据导致队列满的的问题进行校验
	 * 队列的最大长度是Long的最大值
	 * @param data
	 * @return
	 */
	public void write(byte[] data) {
		if(data == null){
			throw new NullPointerException("RedisQueue not write null");
		}
		
		long writeIndex = memcached.incr(keyPrefix + WRITE_INDEX_KEY, 1);
		boolean b = memcached.add(keyPrefix + writeIndex, data);
		if(b){
			memcached.set(keyPrefix + WRITE_KEY, writeIndex);
		}
	}

	public byte[] lockRead() {
		MemcachedLock memcachedLock = new MemcachedLock(memcached, keyPrefix + READ_LOCK_KEY);
		if (memcachedLock.lock()) {
			try {
				if (checkCanRead()) {
					long readIndex = memcached.incr(keyPrefix + READ_KEY, 1);
					byte[] data = memcached.get(keyPrefix + readIndex);
					memcached.delete(keyPrefix + readIndex);// 如果此时数据发送失败可能会出现重复获取的问题
					return data;
				}
			} catch (Exception e) {
				throw new ShuChaoWenRuntimeException(e);
			} finally {
				memcachedLock.unLock();
			}
		}
		return null;
	}

	public byte[] lockReadWait(int sleepTime) throws InterruptedException {
		byte[] data = null;
		while (!Thread.interrupted() && data == null) {
			Thread.sleep(sleepTime);
			data = lockRead();
		}
		return data;
	}

	public Memcached getMemcached() {
		return memcached;
	}
}
