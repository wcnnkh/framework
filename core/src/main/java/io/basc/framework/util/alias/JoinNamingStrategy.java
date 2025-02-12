package io.basc.framework.util.alias;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * TODO 使用指定字符串拼接名称
 * 
 * @author soeasy.run
 *
 */
@RequiredArgsConstructor
public class JoinNamingStrategy implements NamingStrategy {
	@NonNull
	private final String delim;

	@Override
	public boolean test(String name) {
		return true;
	}

	@Override
	public boolean startsWith(String name, String prefix) {
		return name.startsWith(prefix + delim);
	}

	@Override
	public String display(String name, String prefix) {
		return name.substring(prefix.length() + delim.length());
	}

	@Override
	public String join(String left, String right) {
		return left + delim + right;
	}

}
