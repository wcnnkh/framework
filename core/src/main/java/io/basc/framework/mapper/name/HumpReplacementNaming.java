package io.basc.framework.mapper.name;

import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.EncodeException;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 驼峰分割命名
 * 
 * @author wcnnkh
 *
 */
@Data
@AllArgsConstructor
public class HumpReplacementNaming implements Naming {
	/**
	 * 下划线命名法
	 */
	public static final HumpReplacementNaming UNDER_SCORE_CASE = new HumpReplacementNaming('_');

	private final char replacement;

	@Override
	public String getDelimiter() {
		return replacement + "";
	}

	@Override
	public String decode(String source) throws DecodeException {
		int len = source.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = source.charAt(i);
			if (c == replacement) {
				i++;
				sb.append(Character.toUpperCase(source.charAt(i)));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	@Override
	public String encode(String source) throws EncodeException {
		int len = source.length();
		StringBuilder sb = new StringBuilder(len * 2);
		for (int i = 0; i < len; i++) {
			char c = source.charAt(i);
			if (Character.isUpperCase(c)) {// 如果是大写的
				if (i != 0) {
					sb.append(replacement);
				}
				sb.append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
}
