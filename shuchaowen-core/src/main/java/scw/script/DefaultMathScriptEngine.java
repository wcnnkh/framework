package scw.script;

import java.math.BigInteger;
import java.util.LinkedList;

import scw.core.utils.StringUtils;
import scw.util.RegexUtils;

public class MathScriptEngine extends LinkedList<ScriptResolver<BigInteger>> implements ScriptEngine<BigInteger> {
	private static final long serialVersionUID = 1L;
	private final LinkedList<Function> functionList = new LinkedList<MathScriptEngine.Function>();
	private final LinkedList<Operator> operatorList = new LinkedList<MathScriptEngine.Operator>();

	public MathScriptEngine() {
		functionList.add(new MeaninglessFunction("{", "}"));
		functionList.add(new MeaninglessFunction("[", "]"));
		functionList.add(new MeaninglessFunction("(", ")"));
		functionList.add(new AbsoluteValueFunction());

		operatorList.add(new MultiplicationOperator());
		operatorList.add(new DivisionOperator());
		operatorList.add(new AdditionOperator());
		operatorList.add(new SubtractionOperator());
		operatorList.add(new RemainderOperator());
	}

	public final LinkedList<Function> getFunctionList() {
		return functionList;
	}

	public final LinkedList<Operator> getOperatorList() {
		return operatorList;
	}

	public BigInteger eval(String script) {
		String scriptToUse = StringUtils.replace(script, " ", "");
		if (StringUtils.isEmpty(scriptToUse)) {
			return null;
		}

		int scriptLength = scriptToUse.length();
		for (Function function : functionList) {
			int begin = scriptToUse.indexOf(function.getPrefix());
			if (begin == -1) {
				continue;
			}

			int end = scriptToUse.lastIndexOf(function.getSuffix());
			if (end == -1 || begin >= end) {
				throw new ScriptException(scriptToUse);
			}

			int prefixLength = function.getPrefix().length();
			int suffixLength = function.getSuffix().length();
			if (begin == 0) {
				if (end == scriptLength - suffixLength) {
					return function.call(eval(scriptToUse.substring(prefixLength, scriptLength - suffixLength)));
				} else {
					String center = scriptToUse.substring(prefixLength, end);
					String right = scriptToUse.substring(end + suffixLength, scriptLength);
					char operatorChar = right.charAt(0);
					return getOperator(scriptToUse, operatorChar).operation(function.call(eval(center)),
							eval(right.substring(1)));
				}
			} else {
				if (end == scriptLength - suffixLength) {
					String left = scriptToUse.substring(prefixLength, begin);
					String center = scriptToUse.substring(begin + suffixLength);
					char operatorChar = left.charAt(end);
					return getOperator(scriptToUse, operatorChar).operation(eval(left.substring(0, begin - 1)),
							function.call(eval(center)));
				} else {
					String left = scriptToUse.substring(0, begin);
					String center = scriptToUse.substring(begin - prefixLength, end);
					String right = scriptToUse.substring(end + suffixLength, scriptLength);
					char leftOperatorChar = left.charAt(begin);
					char rightOperatorChar = right.charAt(0);
					BigInteger leftValue = eval(left.substring(begin - 1));
					return getOperator(scriptToUse, leftOperatorChar).operation(leftValue,
							getOperator(scriptToUse, rightOperatorChar).operation(function.call(eval(center)),
									eval(right.substring(1))));
				}
			}
		}

		for (Operator operator : operatorList) {
			for (int i = 0; i < scriptLength; i++) {
				char operatorChar = scriptToUse.charAt(i);
				if (operator.isSupport(operatorChar)) {
					return operator.operation(eval(scriptToUse.substring(0, i)), eval(scriptToUse.substring(i + 1)));
				}
			}
		}

		if (RegexUtils.isNumber(scriptToUse)) {
			return parseBigInteger(scriptToUse);
		}

		for (ScriptResolver<BigInteger> resolver : this) {
			if (resolver.isSupport(scriptToUse)) {
				return resolver.eval(this, scriptToUse);
			}
		}

		throw new ScriptException(scriptToUse);
	}

	protected BigInteger parseBigInteger(String str) {
		return new BigInteger(str);
	}

	private Operator getOperator(String script, char operatorChar) throws ScriptException {
		for (Operator operator : operatorList) {
			if (operator.isSupport(operatorChar)) {
				return operator;
			}
		}
		throw new ScriptException("operator:" + operatorChar + ", script=" + script);
	}

	/**
	 * 函数计算
	 * 
	 * @author shuchaowen
	 *
	 */
	public static interface Function {
		String getPrefix();

		String getSuffix();

		BigInteger call(BigInteger value);
	}

	private static final class MeaninglessFunction implements Function {
		private String prefix;
		private String suffix;

		public MeaninglessFunction(String prefix, String suffix) {
			this.prefix = prefix;
			this.suffix = suffix;
		}

		public String getPrefix() {
			return prefix;
		}

		public String getSuffix() {
			return suffix;
		}

		public BigInteger call(BigInteger value) {
			return value;
		}
	}

	public static final class AbsoluteValueFunction implements Function {

		public String getPrefix() {
			return "|";
		}

		public String getSuffix() {
			return "|";
		}

		public BigInteger call(BigInteger value) {
			return value.abs();
		}

	}

	/**
	 * 运算符
	 * 
	 * @author shuchaowen
	 *
	 */
	public static interface Operator {
		boolean isSupport(char operator);

		BigInteger operation(BigInteger left, BigInteger right);
	}

	/**
	 * 乘法
	 * 
	 * @author shuchaowen
	 *
	 */
	private static final class MultiplicationOperator implements Operator {

		public boolean isSupport(char operator) {
			return operator == '*';
		}

		public BigInteger operation(BigInteger left, BigInteger right) {
			return left.multiply(right);
		}
	}

	/**
	 * 除法
	 * 
	 * @author shuchaowen
	 *
	 */
	private static final class DivisionOperator implements Operator {

		public boolean isSupport(char operator) {
			return operator == '/';
		}

		public BigInteger operation(BigInteger left, BigInteger right) {
			return left.divide(right);
		}
	}

	/**
	 * 加法
	 * 
	 * @author shuchaowen
	 *
	 */
	private static final class AdditionOperator implements Operator {

		public boolean isSupport(char operator) {
			return operator == '+';
		}

		public BigInteger operation(BigInteger left, BigInteger right) {
			return left.add(right);
		}
	}

	/**
	 * 减法
	 * 
	 * @author shuchaowen
	 *
	 */
	private static final class SubtractionOperator implements Operator {

		public boolean isSupport(char operator) {
			return operator == '-';
		}

		public BigInteger operation(BigInteger left, BigInteger right) {
			return left.subtract(right);
		}
	}

	/**
	 * 取余
	 * 
	 * @author shuchaowen
	 *
	 */
	private static final class RemainderOperator implements Operator {

		public boolean isSupport(char operator) {
			return operator == '%';
		}

		public BigInteger operation(BigInteger left, BigInteger right) {
			return left.remainder(right);
		}
	}

	public static void main(String[] args) {
		MathScriptEngine mathScriptEngine = new MathScriptEngine();
		BigInteger value = mathScriptEngine.eval("3/2");
		System.out.println(value);
	}
}
