package io.basc.framework.data.generator.distributed;

import io.basc.framework.data.generator.IdGenerator;
import io.basc.framework.util.XUtils;

public class UUIDGenerator implements IdGenerator<String> {

	@Override
	public String next() {
		return XUtils.getUUID();
	}
}
