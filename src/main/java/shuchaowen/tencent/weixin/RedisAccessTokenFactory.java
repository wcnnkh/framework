package shuchaowen.tencent.weixin;

import java.io.IOException;
import java.nio.charset.Charset;

import shuchaowen.common.utils.IOUtils;
import shuchaowen.redis.Redis;
import shuchaowen.redis.RedisLock;
import shuchaowen.tencent.weixin.bean.AccessToken;

public final class RedisAccessTokenFactory extends AbstractAccessTokenFactory{
	private final Redis redis;
	private final byte[] key;
	private final String lockKey;
	
	public RedisAccessTokenFactory(Redis redis, String charsetName, String appid, String appsecret) {
		this(redis, Charset.forName(charsetName), appid, appsecret);
	}
	
	public RedisAccessTokenFactory(Redis redis, Charset charset, String appid, String appsecret) {
		super(appid, appsecret);
		this.redis = redis;
		this.key = (this.getClass().getName() + "#" + getAppid()).getBytes(charset);
		this.lockKey = this.getClass().getName() + "#lock#" + getAppid();
	}
	
	@Override
	protected AccessToken getAccessTokenByCache() {
		byte[] data = redis.get(key);
		if(data == null){
			return null;
		}
		
		try {
			return IOUtils.byteToJavaObject(data);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected AccessToken refreshToken() {
		if(!isExpires()){
			return getAccessTokenByCache();
		}
		
		RedisLock lock = new RedisLock(redis, lockKey);
		if(lock.lock()){
			try {
				if(isExpires()){
					AccessToken accessToken = new AccessToken(getAppid(), getAppsecret());
					if(accessToken != null){
						redis.setex(key, accessToken.getExpires_in(), IOUtils.javaObjectToByte(accessToken));
						return accessToken;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				lock.unLock();
			}
		}
		
		//没有拿到锁
		try {
			Thread.sleep(50L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return refreshToken();
	}
}
