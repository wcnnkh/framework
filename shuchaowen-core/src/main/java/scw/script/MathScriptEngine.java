package scw.script;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import scw.compatible.CompatibleUtils;
import scw.compatible.ServiceLoader;
import scw.core.instance.InstanceUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.mapper.Field;
import scw.mapper.FieldFilter;
import scw.mapper.FilterFeature;
import scw.mapper.MapperUtils;
import scw.util.KeyValuePair;

/**
 * 实现单简单的数学计算
 * 
 * @author shuchaowen
 *
 */
public class MathScriptEngine extends AbstractScriptEngine<BigDecimal> {
	static final Function[] FUNCTIONS;
	static final Operator[] OPERATORS = new Operator[] { new PowOperator(), new MultiplicationOperator(),
			new DivisionOperator(), new RemainderOperator(), new AdditionOperator(), new SubtractionOperator() };

	static {
		List<Function> functions = new ArrayList<MathScriptEngine.Function>();
		ServiceLoader<Function> serviceLoader = CompatibleUtils.getSpi().load(Function.class,
				ClassUtils.getDefaultClassLoader());
		for (Function function : serviceLoader) {
			functions.add(function);
		}

		functions.addAll(InstanceUtils.getSystemConfigurationList(Function.class));
		functions.add(new MaxFunction());
		functions.add(new MinFunction());
		functions.add(new MeaninglessFunction("{", "}"));
		functions.add(new MeaninglessFunction("[", "]"));
		functions.add(new MeaninglessFunction("(", ")"));
		functions.add(new AbsoluteValueFunction());
		FUNCTIONS = functions.toArray(new Function[0]);
	}

	private void resolve(Collection<Fragment> fragments, String script) {
		int begin = -1;
		Function functionToUse = null;
		for (Function function : FUNCTIONS) {
			int index = script.indexOf(function.getPrefix());
			if (index == -1) {
				continue;
			}

			if (index == 0) {
				functionToUse = function;
				break;
			}

			if (begin == -1 || index < begin) {
				begin = index;
				functionToUse = function;
			}
		}

		if (functionToUse != null) {
			KeyValuePair<Integer, Integer> indexPair = StringUtils.indexOf(script, functionToUse.getPrefix(),
					functionToUse.getSuffix());
			if (indexPair != null) {
				resolveFunction(fragments, functionToUse, indexPair, script);
				return;
			}
		}
		resolveOperator(fragments, script);
	}

	private void resolveFunction(Collection<Fragment> fragments, Function function,
			KeyValuePair<Integer, Integer> indexPair, String script) {
		int begin = indexPair.getKey();
		int end = indexPair.getValue();
		int prefixLength = function.getPrefix().length();
		int suffixLength = function.getSuffix().length();
		int scriptLength = script.length();
		String left = begin == 0 ? null : script.substring(0, begin);
		String center = script.substring(begin + prefixLength, end - suffixLength + 1);
		String right = end == (scriptLength - suffixLength) ? null : script.substring(end + suffixLength, scriptLength);
		if (left != null) {
			char operator = left.charAt(left.length() - 1);
			left = left.substring(0, left.length() - 1);
			fragments.add(new ScriptFragment(left).setOperator(getOperator(left, operator)));
		}

		Fragment centerFragment = new ValueFragment(function.eval(this, center));
		if (right == null) {
			fragments.add(centerFragment);
			return;
		}

		char operator = right.charAt(0);
		right = right.substring(1);
		centerFragment.setOperator(getOperator(right, operator));
		fragments.add(centerFragment);

		LinkedList<Fragment> rigthFragments = new LinkedList<MathScriptEngine.Fragment>();
		resolve(rigthFragments, right);
		if (rigthFragments.isEmpty()) {
			fragments.add(new ScriptFragment(right));
		} else {
			fragments.addAll(rigthFragments);
		}
	}

	private Fragment operator(Fragment left, Fragment right) {
		BigDecimal value = left.getOperator().operation(left.getValue(), right.getValue());
		Fragment valueFragment = new ValueFragment(value);
		valueFragment.setOperator(right.getOperator());
		return valueFragment;
	}

	private BigDecimal eval(Collection<Fragment> fragments) {
		if (fragments == null || fragments.isEmpty()) {
			return null;
		}

		for (Operator operator : OPERATORS) {
			LinkedList<Fragment> nextFragments = new LinkedList<Fragment>();
			Iterator<Fragment> iterator = fragments.iterator();
			while (iterator.hasNext()) {
				Fragment fragment = iterator.next();
				// 不处理最后一个
				if (iterator.hasNext() && fragment.getOperator() != null
						&& fragment.getOperator().getOperator() == operator.getOperator()) {
					Fragment value = operator(fragment, iterator.next());
					nextFragments.add(value);
				} else {
					nextFragments.add(fragment);
				}
			}

			if (nextFragments.size() != fragments.size()) {
				return eval(nextFragments);
			}
		}

		if (fragments.size() == 1) {
			return fragments.iterator().next().getValue();
		}

		// 不应该到这里
		throw new RuntimeException("Should never get here");
	}

	private void resolveOperator(Collection<Fragment> fragments, String script) {
		int lastIndex = 0;
		for (int i = 0, len = script.length(); i < len; i++) {
			char c = script.charAt(i);
			for (Operator operator : OPERATORS) {
				if (c == operator.getOperator()) {
					String s = script.substring(lastIndex, i);
					fragments.add(new ScriptFragment(s).setOperator(operator));
					lastIndex = i + 1;
					break;
				}
			}
		}
		fragments.add(new ScriptFragment(script.substring(lastIndex)));
	}

	public BigDecimal eval(String script) {
		String scriptToUse = StringUtils.replace(script, " ", "");
		if (StringUtils.isEmpty(scriptToUse)) {
			return null;
		}

		if (StringUtils.isNumeric(scriptToUse)) {
			return new BigDecimal(scriptToUse);
		}
		return super.eval(scriptToUse);
	}

	@Override
	protected BigDecimal evalInternal(String script) throws ScriptException {
		for (Function function : FUNCTIONS) {
			KeyValuePair<Integer, Integer> indexPair = StringUtils.indexOf(script, function.getPrefix(),
					function.getSuffix());
			if (indexPair == null) {
				continue;
			}

			if (indexPair.getKey() == 0 && indexPair.getValue() == script.length() - 1) {
				String scriptToUse = script.substring(function.getPrefix().length(),
						script.length() - function.getSuffix().length());
				return function.eval(this, scriptToUse);
			}
		}

		LinkedList<Fragment> fragments = new LinkedList<Fragment>();
		resolve(fragments, script);
		if (!fragments.isEmpty()) {
			return eval(fragments);
		}
		return super.evalInternal(script);
	}

	private Operator getOperator(String script, char operatorChar) throws ScriptException {
		for (Operator operator : OPERATORS) {
			if (operator.getOperator() == operatorChar) {
				return operator;
			}
		}
		throw new ScriptException("operator:" + operatorChar + ", script=" + script);
	}

	/**
	 * 将对象的属性值做为参数来替换
	 * 
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

	abstract class Fragment {
		private Operator operator;

		public abstract BigDecimal getValue();

		public Operator getOperator() {
			return operator;
		}

		public Fragment setOperator(Operator operator) {
			this.operator = operator;
			return this;
		}
	}

	final class ScriptFragment extends Fragment {
		private final String script;

		public ScriptFragment(String script) {
			this.script = script;
		}

		public BigDecimal getValue() {
			return eval(script);
		}

		@Override
		public String toString() {
			return script;
		}
	}

	final class ValueFragment extends Fragment {
		private final BigDecimal value;

		public ValueFragment(BigDecimal value) {
			this.value = value;
		}

		@Override
		public BigDecimal getValue() {
			return value;
		}

		@Override
		public String toString() {
			return value == null ? null : value.toString();
		}
	}

	public static interface Function extends ScriptFunction<BigDecimal> {
	}

	static final class MeaninglessFunction implements Function {
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

		public BigDecimal eval(ScriptEngine<BigDecimal> engine, String script) throws ScriptException {
			return engine.eval(script);
		}
	}

	static final class AbsoluteValueFunction implements Function {

		public String getPrefix() {
			return "|";
		}

		public String getSuffix() {
			return "|";
		}

		public BigDecimal eval(ScriptEngine<BigDecimal> engine, String script) throws ScriptException {
			BigDecimal value = engine.eval(script);
			return value.abs();
		}
	}

	static final class MaxFunction implements Function {

		public String getPrefix() {
			return "max(";
		}

		public String getSuffix() {
			return ")";
		}

		public BigDecimal eval(ScriptEngine<BigDecimal> engine, String script) throws ScriptException {
			int index = script.indexOf(",");
			if (index == -1) {
				throw new ScriptException(script);
			}

			String left = script.substring(0, index);
			String right = script.substring(index + 1);
			BigDecimal leftValue = engine.eval(left);
			BigDecimal rightValue = engine.eval(right);
			return leftValue.max(rightValue);
		}

	}

	static final class MinFunction implements Function {

		public String getPrefix() {
			return "min(";
		}

		public String getSuffix() {
			return ")";
		}

		public BigDecimal eval(ScriptEngine<BigDecimal> engine, String script) throws ScriptException {
			int index = script.indexOf(",");
			if (index == -1) {
				throw new ScriptException(script);
			}

			String left = script.substring(0, index);
			String right = script.substring(index + 1);
			BigDecimal leftValue = engine.eval(left);
			BigDecimal rightValue = engine.eval(right);
			return leftValue.min(rightValue);
		}

	}

	/**
	 * 运算符
	 * 
	 * @author shuchaowen
	 *
	 */
	static interface Operator {
		char getOperator();

		BigDecimal operation(BigDecimal left, BigDecimal right);
	}

	/**
	 * 乘法
	 * 
	 * @author shuchaowen
	 *
	 */
	static final class MultiplicationOperator implements Operator {

		public char getOperator() {
			return '*';
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
	static final class DivisionOperator implements Operator {

		public char getOperator() {
			return '/';
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
	static final class AdditionOperator implements Operator {

		public char getOperator() {
			return '+';
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
	static final class SubtractionOperator implements Operator {

		public char getOperator() {
			return '-';
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
	static final class RemainderOperator implements Operator {

		public char getOperator() {
			return '%';
		}

		public BigDecimal operation(BigDecimal left, BigDecimal right) {
			return left.remainder(right);
		}
	}

	/**
	 * 指数运算
	 * 
	 * @author shuchaowen
	 *
	 */
	static final class PowOperator implements Operator {

		public char getOperator() {
			return '^';
		}

		public BigDecimal operation(BigDecimal left, BigDecimal right) {
			return new BigDecimal(Math.pow(left.doubleValue(), right.doubleValue()));
		}
	}
}
