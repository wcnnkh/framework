package io.basc.framework.util.codec.support;

import io.basc.framework.util.codec.Codec;
import io.basc.framework.util.codec.DecodeException;
import io.basc.framework.util.codec.EncodeException;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 大小写转换
 * 
 * @author wcnnkh
 *
 */
@Data
@AllArgsConstructor
public class CaseCodec implements Codec<String, String> {
	/**
	 * 首字母大小写转换
	 */
	public static final CaseCodec FIRST_CASE_CODEC = new CaseCodec(0, 1);

	private final int formIndex;
	private final int endIndex;

	@Override
	public String encode(String source) throws EncodeException {
		char[] chars = source.toCharArray();
		for (int i = formIndex; i < endIndex; i++) {
			chars[i] = Character.toLowerCase(chars[i]);
		}
		return new String(chars);
	}

	@Override
	public String decode(String source) throws DecodeException {
		char[] chars = source.toCharArray();
		for (int i = formIndex; i < endIndex; i++) {
			chars[i] = Character.toUpperCase(chars[i]);
		}
		return new String(chars);
	}

}
