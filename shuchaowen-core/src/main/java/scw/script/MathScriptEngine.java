package scw.script;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedList;

import scw.core.utils.StringUtils;
import scw.mapper.Field;
import scw.mapper.FieldFilter;
import scw.mapper.FilterFeature;
import scw.mapper.MapperUtils;

public class MathScriptEngine implements ScriptEngine<BigDecimal> {
	private final LinkedList<Function> functions = new LinkedList<Function>();
	private final LinkedList<Operator> operators = new LinkedList<Operator>();
	private final LinkedList<ScriptResolver<BigDecimal>> scriptResolvers = new LinkedList<ScriptResolver<BigDecimal>>();

	public MathScriptEngine() {
		scriptResolvers.add(new NumberScriptResolver());

		functions.add(new MeaninglessFunction("{", "}"));
		functions.add(new MeaninglessFunction("[", "]"));
		functions.add(new MeaninglessFunction("(", ")"));
		functions.add(new AbsoluteValueFunction());

		operators.add(new MultiplicationOperator());
		operators.add(new DivisionOperator());
		operators.add(new AdditionOperator());
		operators.add(new SubtractionOperator());
		operators.add(new RemainderOperator());
	}

	public LinkedList<Function> getFunctions() {
		return functions;
	}

	public LinkedList<Operator> getOperators() {
		return operators;
	}

	public LinkedList<ScriptResolver<BigDecimal>> getScriptResolvers() {
		return scriptResolvers;
	}

	public BigDecimal eval(String script) {
		String scriptToUse = StringUtils.replace(script, " ", "");
		if (StringUtils.isEmpty(scriptToUse)) {
			return null;
		}
		
		for (ScriptResolver<BigDecimal> resolver : getScriptResolvers()) {
			if (resolver.isSupport(scriptToUse)) {
				return resolver.eval(this, scriptToUse);
			}
		}
		
		int scriptLength = scriptToUse.length();
		for (Function function : getFunctions()) {
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
					String center = scriptToUse.substring(begin + prefixLength, end - suffixLength);
					String right = scriptToUse.substring(end + suffixLength, scriptLength);
					char leftOperatorChar = left.charAt(begin - 1);
					char rightOperatorChar = right.charAt(0);
					
					BigDecimal leftValue = eval(left.substring(begin - 1));
					return getOperator(scriptToUse, leftOperatorChar).operation(leftValue,
							getOperator(scriptToUse, rightOperatorChar).operation(function.call(eval(center)),
									eval(right.substring(1))));
				}
			}
		}

		for (Operator operator : getOperators()) {
			for (int i = 0; i < scriptLength; i++) {
				char operatorChar = scriptToUse.charAt(i);
				if (operator.isSupport(operatorChar)) {
					return operator.operation(eval(scriptToUse.substring(0, i)), eval(scriptToUse.substring(i + 1)));
				}
			}
		}
		
		throw new ScriptException(scriptToUse);
	}

	private Operator getOperator(String script, char operatorChar) throws ScriptException {
		for (Operator operator : getOperators()) {
			if (operator.isSupport(operatorChar)) {
				return operator;
			}
		}
		throw new ScriptException("operator:" + operatorChar + ", script=" + script);
	}

	/**
	 * 将对象的属性值做为参数来替换
	 * @author shuchaowen
	 *
	 */
	public static final class ObjectFieldScriptResolver implements ScriptResolver<BigDecimal> {
		private Object instance;

		public ObjectFieldScriptResolver(Object instance) {
			this.instance = instance;
		}

		public boolean isSupport(String script) {
			if (instance == null) {
				return false;
			}

			return getField(script) != null;
		}

		public Field getField(final String name) {
			return MapperUtils.getMapper().getField(instance.getClass(), new FieldFilter() {

				public boolean accept(Field field) {
					return field.getGetter().getName().equals(name);
				}
			}, FilterFeature.SUPPORT_GETTER);
		}

		public BigDecimal eval(ScriptEngine<BigDecimal> engine, String script) throws ScriptException {
			Field field = getField(script);
			Object value = field.getGetter().get(instance);
			if (value == null) {
				throw new ScriptException(script);
			}

			if (value instanceof BigInteger) {
				return new BigDecimal((BigInteger) value);
			} else if (value instanceof BigDecimal) {
				return (BigDecimal) value;
			} else {
				return new BigDecimal(value.toString());
			}
		}
	}

	static final class NumberScriptResolver implements ScriptResolver<BigDecimal> {

		public boolean isSupport(String script) {
			return StringUtils.isNumeric(script);
		}

		public BigDecimal eval(ScriptEngine<BigDecimal> engine, String script) throws ScriptException {
			System.out.println("resolver:" + script);
			return new BigDecimal(script);
		}
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

		BigDecimal call(BigDecimal value);
	}

	static class MeaninglessFunction implements Function {
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

		public BigDecimal call(BigDecimal value) {
			return value;
		}
	}

	static final class AbsoluteValueFunction implements Function {

		public String getPrefix() {
			return "|";
		}

		public String getSuffix() {
			return "|";
		}

		public BigDecimal call(BigDecimal value) {
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

		BigDecimal operation(BigDecimal left, BigDecimal right);
	}
	
	/**
	 * 乘法
	 * 
	 * @author shuchaowen
	 *
	 */
	static class MultiplicationOperator implements Operator {

		public boolean isSupport(char operator) {
			return operator == '*';
		}

		public BigDecimal operation(BigDecimal left, BigDecimal right) {
			return left.multiply(right);
		}
	}

	/**
	 * 除法
	 * 
	 * @author shuchaowen
	 *
	 */
	static class DivisionOperator implements Operator {

		public boolean isSupport(char operator) {
			return operator == '/';
		}

		public BigDecimal operation(BigDecimal left, BigDecimal right) {
			return left.divide(right);
		}
	}

	/**
	 * 加法
	 * 
	 * @author shuchaowen
	 *
	 */
	static class AdditionOperator implements Operator {

		public boolean isSupport(char operator) {
			return operator == '+';
		}

		public BigDecimal operation(BigDecimal left, BigDecimal right) {
			return left.add(right);
		}
	}

	/**
	 * 减法
	 * 
	 * @author shuchaowen
	 *
	 */
	static class SubtractionOperator implements Operator {

		public boolean isSupport(char operator) {
			return operator == '-';
		}

		public BigDecimal operation(BigDecimal left, BigDecimal right) {
			return left.subtract(right);
		}
	}

	/**
	 * 取余
	 * 
	 * @author shuchaowen
	 *
	 */
	static class RemainderOperator implements Operator {

		public boolean isSupport(char operator) {
			return operator == '%';
		}

		public BigDecimal operation(BigDecimal left, BigDecimal right) {
			return left.remainder(right);
		}
	}
}
