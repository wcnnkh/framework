package io.basc.framework.consistency.policy;

import io.basc.framework.io.JavaSerializer;
import io.basc.framework.io.Serializer;
import io.basc.framework.util.Assert;

/**
 * 基于存储的实现
 * @author wcnnkh
 *
 */
public abstract class StorageCompensatePolicy extends AbstractCompensatePolicy{
	//分钟
	private int compenstBeforeMinute = 5;
	private Serializer serializer = JavaSerializer.INSTANCE;

	public int getCompenstBeforeMinute() {
		return compenstBeforeMinute;
	}

	public void setCompenstBeforeMinute(int compenstBeforeMinute) {
		Assert.requiredArgument(compenstBeforeMinute > 0, "compenstBeforeMinute");
		this.compenstBeforeMinute = compenstBeforeMinute;
	}

	public Serializer getSerializer() {
		return serializer;
	}

	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}
}
