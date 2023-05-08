package io.basc.framework.mapper.name;

import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.util.Assert;

public class NamespaceNaming implements Naming {
	private final String delimiter;

	public NamespaceNaming(String delimiter) {
		Assert.requiredArgument(delimiter != null, "delimiter");
		this.delimiter = delimiter;
	}

	@Override
	public String encode(String source) throws EncodeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String decode(String source) throws DecodeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDelimiter() {
		return delimiter;
	}

}
