package io.basc.framework.mapper;

import java.util.stream.Collectors;

import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.util.Elements;

/**
 * 下划线命名法
 * 
 * @author wcnnkh
 *
 */
public class UnderScoreCaseNameMapping implements Naming {
	private static final char REPLACEMENT = '_';

	@Override
	public String join(Elements<String> elements) {
		return elements.map((e) -> encode(e)).collect(Collectors.joining(REPLACEMENT + ""));
	}

	@Override
	public String encode(String source) throws EncodeException {
		int len = source.length();
		StringBuilder sb = new StringBuilder(len * 2);
		for (int i = 0; i < len; i++) {
			char c = source.charAt(i);
			if (Character.isUpperCase(c)) {// 如果是大写的
				if (i != 0) {
					sb.append(REPLACEMENT);
				}
				sb.append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	@Override
	public String decode(String source) throws DecodeException {
		int len = source.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = source.charAt(i);
			if (c == REPLACEMENT) {
				i++;
				sb.append(Character.toUpperCase(source.charAt(i)));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

}
