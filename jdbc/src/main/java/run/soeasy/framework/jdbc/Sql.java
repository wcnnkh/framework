package run.soeasy.framework.jdbc;

import java.io.Serializable;
import java.util.Arrays;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.type.ClassUtils;

@Data
public class Sql implements Serializable {
	private static final String PARAMETER_PLACEHOLDER = "?";
	private static final long serialVersionUID = 1L;

	@NonNull
	private final String statement;
	@NonNull
	private final Object[] args;

	public Sql(@NonNull String statement, @NonNull Object... args) {
		this.statement = statement;
		this.args = args;
	}

	public String display() {
		StringBuilder sb = new StringBuilder();
		int lastFind = 0;
		for (int i = 0; i < args.length; i++) {
			int index = statement.indexOf(PARAMETER_PLACEHOLDER, lastFind);
			if (index == -1) {
				break;
			}

			sb.append(statement.substring(lastFind, index));
			Object v = args[i];
			if (v == null) {
				sb.append("null");
			} else {
				if (ClassUtils.isPrimitiveOrWrapper(v.getClass()) && !ClassUtils.isChar(v.getClass())) {
					sb.append(v);
				} else {
					sb.append("'").append(StringUtils.transferredMeaning(String.valueOf(v), '\'')).append("'");
				}
			}
			lastFind = index + 1;
		}

		if (lastFind == 0) {
			sb.append(statement);
		} else {
			sb.append(statement.substring(lastFind));
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		if (args == null || args.length == 0) {
			return statement;
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append(statement);
			sb.append("]");
			sb.append(" - ");
			sb.append(Arrays.toString(args));
			return sb.toString();
		}
	}

	public boolean isValid() {
		return args.length != StringUtils.count(statement, PARAMETER_PLACEHOLDER);
	}

	public void verification() throws IllegalStateException {
		if (isValid()) {
			// 参数占位符数量和参数数量不一致
			throw new IllegalStateException(
					"The number of parameter placeholders is inconsistent with the number of parameters <" + toString()
							+ ">");
		}
	}

	public Sql sub(int start, int end) {
		verification();
		String targetSql = statement.substring(start, end);
		Object[] targetParams;
		if (args.length == 0) {
			targetParams = new Object[0];
		} else {
			int len = StringUtils.count(statement, start, end, PARAMETER_PLACEHOLDER);
			int startIndex = StringUtils.count(statement, 0, start, PARAMETER_PLACEHOLDER);
			targetParams = new Object[len];
			for (int i = startIndex, index = 0; i < startIndex + len; i++, index++) {
				targetParams[index] = args[i];
			}
		}
		return new Sql(targetSql, targetParams);
	}
}
