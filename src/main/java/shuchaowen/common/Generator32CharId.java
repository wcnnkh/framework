package shuchaowen.common;

import java.io.Serializable;
import java.nio.CharBuffer;

import shuchaowen.common.exception.ShuChaoWenRuntimeException;
import shuchaowen.common.utils.StringUtils;

/**
 * 生成一个定长的32位id
 * @author shuchaowen
 *
 */
public class Generator32CharId implements Serializable{
	private static final long serialVersionUID = 1L;
	private final long uid;
	private final long cts;
	private final String id;
	
	/**
	 * 生成一个
	 * @param uid
	 */
	public Generator32CharId(long uid){
		this.uid = uid;
		this.cts = System.currentTimeMillis();
		CharBuffer buffer = CharBuffer.allocate(32);
		buffer.put(StringUtils.complemented(Long.toString(cts, 32), '0', 13));
		buffer.put(StringUtils.complemented(Long.toString(uid, 32), '0', 13));
		buffer.put(StringUtils.getRandomStr(6));
		this.id = buffer.toString();
	}
	
	/**
	 * 通过ID解析
	 * @param id
	 */
	public Generator32CharId(String id){
		if(id.length() != 32){
			throw new ShuChaoWenRuntimeException("length not is 32 [" + id + "]");
		}
		this.cts = Long.parseLong(id.substring(0, 12));
		this.uid = Long.parseLong(id.substring(13, 26));
		this.id = id;
	}

	/**
	 * @return the uid
	 */
	public long getUid() {
		return uid;
	}

	/**
	 * @return the cts
	 */
	public long getCts() {
		return cts;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
}
