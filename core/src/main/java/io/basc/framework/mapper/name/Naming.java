package io.basc.framework.mapper.name;

import java.util.stream.Collectors;

import io.basc.framework.codec.Codec;
import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.util.Elements;

/**
 * 命名法
 * 
 * @author wcnnkh
 * @see HumpReplacementNaming
 * @see CamelCase
 *
 */
public interface Naming extends Codec<String, String> {
	/**
	 * 根据规则编码名称
	 */
	@Override
	String encode(String source) throws EncodeException;

	/**
	 * 根据规则解码名称
	 */
	@Override
	String decode(String source) throws DecodeException;

	String getDelimiter();

	/**
	 * 根据规则拼接名称
	 * 
	 * @see #encode(String)
	 * @param elements
	 * @return
	 */
	default String join(Elements<String> elements) {
		String delimiter = getDelimiter();
		return elements.map((e) -> encode(e)).collect(Collectors.joining(delimiter == null ? "" : delimiter));
	}
}
