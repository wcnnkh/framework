package shuchaowen.tencent.weixin;

import java.io.IOException;
import java.nio.charset.Charset;

import shuchaowen.core.util.IOUtils;
import shuchaowen.redis.Redis;
import shuchaowen.redis.RedisLock;
import shuchaowen.tencent.weixin.bean.JsApiTicket;

public final class RedisJsApiTicketFactory extends AbstractJsApiTicketFactory{
	private final Redis redis;
	private final byte[] key;
	private final String lockKey;
	
	public RedisJsApiTicketFactory(Redis redis, String charsetName, String key, AccessTokenFactory accessTokenFactory){
		this(redis, Charset.forName(charsetName), key, accessTokenFactory);
	}
	
	public RedisJsApiTicketFactory(Redis redis, Charset charset, String key, AccessTokenFactory accessTokenFactory) {
		super(accessTokenFactory);
		this.redis = redis;
		this.key = (this.getClass().getName() + "#" + key).getBytes(charset);
		this.lockKey = this.getClass().getName() + "#lock#" + key;
	}
	
	@Override
	protected JsApiTicket getJsApiTicketByCache() {
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
	protected JsApiTicket refreshJsApiTicket() {
		if(!isExpires()){
			return getJsApiTicketByCache();
		}
		
		RedisLock lock = new RedisLock(redis, lockKey);
		if(lock.lock()){
			try {
				if(isExpires()){
					JsApiTicket jsApiTicket = new JsApiTicket(getAccessTokenFactory().getAccessToken());
					if(jsApiTicket != null){
						redis.setex(key, jsApiTicket.getExpires_in(), IOUtils.javaObjectToByte(jsApiTicket));
						return jsApiTicket;
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
		return refreshJsApiTicket();
	}
}
