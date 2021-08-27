package io.basc.framework.script;

import io.basc.framework.core.utils.StringUtils;
import io.basc.framework.env.Sys;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.FieldFeature;
import io.basc.framework.mapper.Fields;
import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.math.BigDecimalHolder;
import io.basc.framework.math.Calculator;
import io.basc.framework.math.Calculators;
import io.basc.framework.math.NumberHolder;
import io.basc.framework.util.Pair;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 实现单简单的数学计算(并不成熟，不推荐进行复杂计算)
 * 
 * @author shuchaowen
 *
 */
public final class MathScriptEngine extends AbstractScriptEngine<NumberHolder> {
	static final Function[] FUNCTIONS;

	static {
		List<Function> functions = new ArrayList<Function>(Sys.env.getServiceLoader(Function.class).toList());
		functions.add(new MaxFunction());
		functions.add(new MinFunction());
		functions.add(new MeaninglessFunction("{", "}"));
		functions.add(new MeaninglessFunction("[", "]"));
		functions.add(new MeaninglessFunction("(", ")"));
		functions.add(new AbsoluteValueFunction());
		FUNCTIONS = functions.toArray(new Function[0]);
	}

	private void resolve(Collection<Fragment> fragments, String script, Calculator lastOperator) {
		for (Function function : FUNCTIONS) {
			Pair<Integer, Integer> indexPair = StringUtils.indexOf(script, function.getPrefix(), function.getSuffix());
			if (indexPair == null) {
				continue;
			}

			int begin = indexPair.getKey();
			int end = indexPair.getValue();
			int prefixLength = function.getPrefix().length();
			int suffixLength = function.getSuffix().length();
			int scriptLength = script.length();
			String left = begin == 0 ? null : script.substring(0, begin);
			String center = script.substring(begin + prefixLength, end - suffixLength + 1);
			String right = end == (scriptLength - suffixLength) ? null
					: script.substring(end + suffixLength, scriptLength);
			if (left != null) {
				Calculator leftOperator = null;
				for (Calculator[] operators : Calculators.GROUPS) {
					for (Calculator operator : operators) {
						if (left.endsWith(operator.getOperator())) {
							leftOperator = operator;
							break;
						}
					}
				}

				if (leftOperator == null) {
					throw new ScriptException(script);
				}

				left = left.substring(0, left.length() - leftOperator.getOperator().length());
				resolve(fragments, left, leftOperator);
			}

			Calculator centerOperator = null;
			if (right != null) {
				Calculator rightOperator = null;
				for (Calculator[] operators : Calculators.GROUPS) {
					for (Calculator operator : operators) {
						if (right.startsWith(operator.getOperator())) {
							rightOperator = operator;
							break;
						}
					}
				}

				if (rightOperator == null) {
					throw new ScriptException(script);
				}

				right = right.substring(rightOperator.getOperator().length());
				centerOperator = rightOperator;
			}

			Fragment centerFragment = new ValueFragment(function.eval(this, center));
			if (centerOperator != null) {
				centerFragment.setOperator(centerOperator);
				fragments.add(centerFragment);
			} else {// 说明右边没有表达示了
				centerFragment.setOperator(lastOperator);
				fragments.add(centerFragment);
			}

			if (right != null) {
				resolve(fragments, right, lastOperator);
			}
			return;
		}

		for (Calculator[] operators : Calculators.GROUPS) {
			for (Calculator operator : operators) {
				int index = script.indexOf(operator.getOperator());
				if (index != -1) {
					String s = script.substring(0, index);
					resolve(fragments, s, operator);
					String right = script.substring(index + operator.getOperator().length());
					if (StringUtils.isNotEmpty(right)) {
						resolve(fragments, right, lastOperator);
					}
					return;
				}
			}
		}

		fragments.add(new ScriptFragment(script).setOperator(lastOperator));
	}

	private Fragment operator(Fragment left, Fragment right) {
		NumberHolder value = left.getOperator().calculate(left.getValue(), right.getValue());
		Fragment valueFragment = new ValueFragment(value);
		valueFragment.setOperator(right.getOperator());
		return valueFragment;
	}

	private int indexOf(Calculator operator, Collection<Fragment> fragments) {
		Iterator<Fragment> iterator = fragments.iterator();
		int index = 0;
		while (iterator.hasNext()) {
			Fragment fragment = iterator.next();
			if (iterator.hasNext() && fragment.getOperator().getOperator().equals(operator.getOperator())) {// 不是最后一个
				return index;
			}
			index++;
		}
		return -1;
	}

	private NumberHolder eval(Collection<Fragment> fragments) {
		if (fragments == null || fragments.isEmpty()) {
			return null;
		}

		for (Calculator[] operators : Calculators.GROUPS) {
			int indexToUse = -1;
			// 找到同个一个运算优先级中最左边的片段
			for (Calculator operator : operators) {
				int index = indexOf(operator, fragments);
				if (index != -1 && (indexToUse == -1 || index < indexToUse)) {
					indexToUse = index;
				}
			}

			if (indexToUse != -1) {
				LinkedList<Fragment> nextFragments = new LinkedList<Fragment>();
				int index = 0;
				Iterator<Fragment> iterator = fragments.iterator();
				while (iterator.hasNext()) {
					Fragment fragment = iterator.next();
					if (index == indexToUse) {
						Fragment value = operator(fragment, iterator.next());
						nextFragments.add(value);
					} else {
						nextFragments.add(fragment);
					}
					index++;
				}

				if (nextFragments.size() != fragments.size()) {
					return eval(nextFragments);
				}
			}
		}

		if (fragments.size() == 1) {
			return fragments.iterator().next().getValue();
		}

		// 不应该到这里
		throw new RuntimeException("Should never get here");
	}

	public NumberHolder eval(String script) {
		String scriptToUse = StringUtils.replace(script, " ", "");
		if (StringUtils.isEmpty(scriptToUse)) {
			return null;
		}

		if (StringUtils.isNumeric(scriptToUse)) {
			return new BigDecimalHolder(scriptToUse);
		}
		return super.eval(scriptToUse);
	}

	@Override
	protected NumberHolder evalInternal(String script) throws ScriptException {
		for (Function function : FUNCTIONS) {
			Pair<Integer, Integer> indexPair = StringUtils.indexOf(script, function.getPrefix(), function.getSuffix());
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
		resolve(fragments, script, null);
		if (!fragments.isEmpty()) {
			return eval(fragments);
		}
		return super.evalInternal(script);
	}

	/**
	 * 将对象的属性值做为参数来替换
	 * 
	 * @author shuchaowen
	 *
	 */
	public static final class ObjectFieldScriptResolver implements ScriptResolver<NumberHolder> {
		private Object instance;
		private Fields fields;

		public ObjectFieldScriptResolver(Object instance) {
			this.instance = instance;
			this.fields = instance == null ? null
					: MapperUtils.getFields(instance.getClass()).all().accept(FieldFeature.SUPPORT_GETTER);
		}

		public boolean isSupport(String script) {
			if (instance == null) {
				return false;
			}

			return getField(script) != null;
		}

		public Field getField(final String name) {
			return fields == null ? null : fields.find(name, null);
		}

		public NumberHolder eval(ScriptEngine<NumberHolder> engine, String script) throws ScriptException {
			Field field = getField(script);
			Object value = field.getGetter().get(instance);
			if (value == null) {
				throw new ScriptException(script);
			}

			if (value instanceof BigDecimal) {
				return new BigDecimalHolder((BigDecimal) value);
			} else if (value instanceof NumberHolder) {
				return (NumberHolder) value;
			} else {
				return new BigDecimalHolder(value.toString());
			}
		}
	}

	abstract class Fragment {
		private Calculator operator;

		public abstract NumberHolder getValue();

		public Calculator getOperator() {
			return operator;
		}

		public Fragment setOperator(Calculator operator) {
			this.operator = operator;
			return this;
		}
	}

	final class ScriptFragment extends Fragment {
		private final String script;

		public ScriptFragment(String script) {
			this.script = script;
		}

		public NumberHolder getValue() {
			return eval(script);
		}

		@Override
		public String toString() {
			return "(" + script + ")" + (getOperator() == null ? "" : getOperator().getOperator());
		}
	}

	final class ValueFragment extends Fragment {
		private final NumberHolder value;

		public ValueFragment(NumberHolder value) {
			this.value = value;
		}

		@Override
		public NumberHolder getValue() {
			return value;
		}

		@Override
		public String toString() {
			return "(" + (value == null ? null : value.toString()) + ")"
					+ (getOperator() == null ? "" : getOperator().getOperator());
		}
	}

	public static interface Function extends ScriptFunction<NumberHolder> {
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

		public NumberHolder eval(ScriptEngine<NumberHolder> engine, String script) throws ScriptException {
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

		public NumberHolder eval(ScriptEngine<NumberHolder> engine, String script) throws ScriptException {
			NumberHolder value = engine.eval(script);
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

		public NumberHolder eval(ScriptEngine<NumberHolder> engine, String script) throws ScriptException {
			int index = script.indexOf(",");
			if (index == -1) {
				throw new ScriptException(script);
			}

			String left = script.substring(0, index);
			String right = script.substring(index + 1);
			NumberHolder leftValue = engine.eval(left);
			NumberHolder rightValue = engine.eval(right);
			return leftValue.compareTo(rightValue) > 0 ? leftValue : rightValue;
		}

	}

	static final class MinFunction implements Function {

		public String getPrefix() {
			return "min(";
		}

		public String getSuffix() {
			return ")";
		}

		public NumberHolder eval(ScriptEngine<NumberHolder> engine, String script) throws ScriptException {
			int index = script.indexOf(",");
			if (index == -1) {
				throw new ScriptException(script);
			}

			String left = script.substring(0, index);
			String right = script.substring(index + 1);
			NumberHolder leftValue = engine.eval(left);
			NumberHolder rightValue = engine.eval(right);
			return leftValue.compareTo(rightValue) < 0 ? leftValue : rightValue;
		}

	}
}
