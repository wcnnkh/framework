package shuchaowen.web.util;

public abstract class Token {
	/**
	 * 是否已经过期
	 * @return
	 */
	protected abstract boolean isExpire();
	
	/**
	 * 获取临时的token
	 * @return
	 */
	protected abstract String getTempToken();
	
	/**
	 * 获取一个新的token
	 * @return
	 */
	protected abstract String getNewToken();
	
	/**
	 * 获取token
	 * @return
	 */
	public final String getToken(){
		String token = getTempToken();
		if(token == null || isExpire()){
			synchronized (this) {
				token = getTempToken();
				if(token == null || isExpire()){
					token = getTempToken();
				}
			}
		}
		return token;
	}
}
