package scw.compensat.policy;

import scw.core.Assert;
import scw.io.JavaSerializer;
import scw.io.NoTypeSpecifiedSerializer;

/**
 * 基于存储的实现
 * @author shuchaowen
 *
 */
public abstract class StorageCompenstPolicy extends AbstractCompenstPolicy{
	//分钟
	private int compenstBeforeMinute = 5;
	private NoTypeSpecifiedSerializer serializer = JavaSerializer.INSTANCE;

	public int getCompenstBeforeMinute() {
		return compenstBeforeMinute;
	}

	public void setCompenstBeforeMinute(int compenstBeforeMinute) {
		Assert.requiredArgument(compenstBeforeMinute > 0, "compenstBeforeMinute");
		this.compenstBeforeMinute = compenstBeforeMinute;
	}

	public NoTypeSpecifiedSerializer getSerializer() {
		return serializer;
	}

	public void setSerializer(NoTypeSpecifiedSerializer serializer) {
		this.serializer = serializer;
	}
}
