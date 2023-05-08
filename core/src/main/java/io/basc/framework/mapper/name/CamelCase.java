package io.basc.framework.mapper.name;

import io.basc.framework.codec.Codec;
import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.codec.support.CaseCodec;
import io.basc.framework.util.Assert;

/**
 * 驼峰命名法
 * 
 * @author wcnnkh
 *
 */
public class CamelCase implements Naming {
	/**
	 * 驼峰命名法(又叫小驼峰)
	 */
	public static final CamelCase CAMEL_CASE = new CamelCase(CaseCodec.FIRST_CASE_CODEC);

	/**
	 * 大驼峰命名法(又叫帕斯卡命名法)
	 */
	public static final CamelCase UPPER_CAMEL_CASE = new CamelCase(CaseCodec.FIRST_CASE_CODEC.reverse());

	private final Codec<String, String> humpNameCodec;

	public CamelCase(Codec<String, String> humpNameCodec) {
		Assert.requiredArgument(humpNameCodec != null, "humpNameCodec");
		this.humpNameCodec = humpNameCodec;
	}

	@Override
	public String encode(String source) throws EncodeException {
		return humpNameCodec.encode(source);
	}

	@Override
	public String decode(String source) throws DecodeException {
		return humpNameCodec.decode(source);
	}

	@Override
	public String getDelimiter() {
		return null;
	}
}
