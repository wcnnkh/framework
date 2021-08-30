package io.basc.framework.oauth2;

import java.io.Serializable;

import io.basc.framework.util.StringUtils;

/**
 * 令牌类型
 * @author shuchaowen
 *
 */
public class TokenType implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final TokenType BEARER = new TokenType("bearer");
	public static final TokenType MAC = new TokenType("mac");

	private final String name;

	public TokenType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof TokenType) {
			return StringUtils.equals(name, ((TokenType) obj).name);
		}
		return false;
	}

	@Override
	public String toString() {
		return name;
	}
}
