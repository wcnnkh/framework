package shuchaowen.core.util;

import shuchaowen.core.cache.Memcached;

public class MemcachedQueue {
	private static final String READ_KEY = "_read";
	private static final String WRITE_KEY = "_write";
	private static final String READ_LOCK_KEY = "_read_lock";
	
	private Memcached memcached;
	private String keyPrefix;
	
	public MemcachedQueue(String keyPrefix, Memcached memcached){
		this.keyPrefix = keyPrefix;
		this.memcached = memcached;
		memcached.add(keyPrefix + READ_KEY, 0 + "");
		memcached.add(keyPrefix + WRITE_KEY, 0 + "");
	}
	
	private boolean checkCanRead(){
		long readIndex = Integer.parseInt((String)memcached.get(keyPrefix + READ_KEY));
		long writeIndex = Integer.parseInt((String)memcached.get(keyPrefix + WRITE_KEY));
		if(writeIndex < 0){
			if(readIndex >= 0){
				return true;
			}
		}
		return writeIndex > readIndex;
	}

	/**
	 * 为了提高性能这里不对因一直没有取出数据导致的问题进行校验
	 * @param data
	 * @return
	 */
	public boolean write(byte[] data){
		if(data == null){
			return false;
		}
		
		return memcached.add(keyPrefix + memcached.incr(keyPrefix + WRITE_KEY, 1), data);
	}
	
	public byte[] lockRead(){
		MemcachedLock memcachedLock = new MemcachedLock(memcached, keyPrefix + READ_LOCK_KEY);
		if(memcachedLock.lock()){
			try {
				if(checkCanRead()){
					long readIndex = memcached.incr(keyPrefix + READ_KEY, 1);
					byte[] data = memcached.get(keyPrefix + readIndex);
					memcached.delete(keyPrefix + readIndex);//如果此时数据发送失败可能会出现重复获取的问题
					return data;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				memcachedLock.unLock();
			}
		}
		return null;
	}
	
	public Memcached getMemcached(){
		return memcached;
	}
}
