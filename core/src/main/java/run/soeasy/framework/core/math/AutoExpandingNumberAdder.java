package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;

import run.soeasy.framework.core.domain.Value;

/**
 * 自动类型扩充的数值累加器，继承自{@link NumberAdder}，基于已有{@link IntValue}、{@link LongValue}、{@link BigIntegerValue}实现
 * <p>核心能力：累加操作中自动检测当前类型溢出，无缝升级到更大范围的类型（优先级：int → long → BigInteger），
 * 兼顾小数值场景的运算效率与大数值场景的范围兼容性，无需手动处理类型转换。
 * 
 * <p>设计思路：
 * 1. 组合模式：内部持有当前活跃的{@link NumberAdder}实例（初始为最小合适类型），所有操作委托给该实例；
 * 2. 溢出触发：依赖{@link IntValue}、{@link LongValue}自身的溢出检测（如{@link Math#addExact}），捕获{@link ArithmeticException}后触发升级；
 * 3. 类型优化：初始化和升级时均选择“能容纳当前值的最小类型”，避免不必要的高精度类型开销；
 * 4. 可逆重置：支持恢复到初始值和初始类型，而非当前类型，适配循环计数等场景。
 * 
 * <p>适用场景：
 * - 数值范围不确定的累加场景（如统计计数、流量汇总，可能从int量级增长到long/BigInteger量级）；
 * - 需兼顾性能与扩展性的场景（小数值用高效基本类型，大数值自动扩容）；
 * - 希望简化类型处理逻辑，无需手动判断和切换类型的业务。
 */
public class AutoExpandingNumberAdder extends NumberAdder {
	private static final long serialVersionUID = 1L;
	
	/** 当前活跃的数值累加器实例，承载实际的数值存储与运算（动态切换类型） */
	private NumberAdder current;
	/** 初始数值，用于{@link #reset()}时恢复到初始状态（含初始类型） */
	private final Number initialValue;

	/**
	 * 私有构造器：通过初始数值和初始累加器实例初始化，禁止外部直接创建
	 * <p>需通过工厂方法{@link #of(int)}、{@link #of(long)}、{@link #of(BigInteger)}创建实例，确保初始类型最优。
	 * 
	 * @param initialValue 初始数值（用于后续reset恢复）
	 * @param initialAdder 初始类型的{@link NumberAdder}实例（如int值对应{@link IntValue}）
	 */
	private AutoExpandingNumberAdder(Number initialValue, NumberAdder initialAdder) {
		this.initialValue = initialValue;
		this.current = initialAdder;
	}

	/**
	 * 工厂方法：从int初始值创建自动扩充累加器
	 * <p>初始类型固定为{@link IntValue}，因int值必然在自身范围内。
	 * 
	 * @param initialValue 初始int值（范围：{@link Integer#MIN_VALUE} ~ {@link Integer#MAX_VALUE}）
	 * @return 自动扩充累加器实例（初始类型：{@link IntValue}）
	 */
	public static AutoExpandingNumberAdder of(int initialValue) {
		return new AutoExpandingNumberAdder(initialValue, new IntValue(initialValue));
	}

	/**
	 * 工厂方法：从long初始值创建自动扩充累加器
	 * <p>初始类型优化：若long值在int范围内（{@link Integer#MIN_VALUE} ~ {@link Integer#MAX_VALUE}），
	 * 优先使用{@link IntValue}以提升运算效率；否则使用{@link LongValue}。
	 * 
	 * @param initialValue 初始long值（范围：{@link Long#MIN_VALUE} ~ {@link Long#MAX_VALUE}）
	 * @return 自动扩充累加器实例（初始类型：{@link IntValue} 或 {@link LongValue}）
	 */
	public static AutoExpandingNumberAdder of(long initialValue) {
		NumberAdder initialAdder = (initialValue >= Integer.MIN_VALUE && initialValue <= Integer.MAX_VALUE)
				? new IntValue((int) initialValue)
				: new LongValue(initialValue);
		return new AutoExpandingNumberAdder(initialValue, initialAdder);
	}

	/**
	 * 工厂方法：从BigInteger初始值创建自动扩充累加器
	 * <p>初始类型自动降级：尝试将BigInteger值适配到最小能容纳的类型（优先{@link IntValue}，其次{@link LongValue}，最后{@link BigIntegerValue}），
	 * 避免直接使用高精度类型带来的性能损耗。
	 * 
	 * @param initialValue 初始BigInteger值（无范围限制）
	 * @return 自动扩充累加器实例（初始类型：{@link IntValue} / {@link LongValue} / {@link BigIntegerValue}）
	 * @throws ArithmeticException 无（降级失败时自动 fallback 到更大类型，不会抛出异常）
	 */
	public static AutoExpandingNumberAdder of(BigInteger initialValue) {
		NumberAdder initialAdder;
		try {
			// 尝试降级为int：通过intValueExact()确保值在int范围内，超范围则抛异常
			initialAdder = new IntValue(initialValue.intValueExact());
		} catch (ArithmeticException e1) {
			try {
				// 尝试降级为long：同理，超范围抛异常
				initialAdder = new LongValue(initialValue.longValueExact());
			} catch (ArithmeticException e2) {
				// 无法降级，使用BigIntegerValue
				initialAdder = new BigIntegerValue(initialValue);
			}
		}
		return new AutoExpandingNumberAdder(initialValue, initialAdder);
	}

	/**
	 * 核心累加方法：向当前值添加long类型数值，自动检测溢出并升级类型
	 * <p>执行流程：
	 * 1. 委托当前{@link NumberAdder}实例尝试累加（如当前为{@link IntValue}则调用其{@link IntValue#add(long)}）；
	 * 2. 若累加触发{@link ArithmeticException}（即当前类型溢出），调用{@link #upgradeType(long)}升级类型并完成累加；
	 * 3. 若当前为{@link BigIntegerValue}，因无溢出风险，直接委托执行。
	 * 
	 * @param value 要累加的long类型数值（可正可负，负数值可能触发类型降级，但当前实现暂不主动降级，仅升级）
	 * @throws ArithmeticException 仅在极端异常场景（如BigInteger运算错误）抛出，正常溢出会自动升级，不抛异常
	 */
	@Override
	public void add(long value) {
		try {
			// 尝试在当前类型中累加：利用底层类型的溢出检测（如IntValue用Math.addExact）
			current.add(value);
		} catch (ArithmeticException e) {
			// 溢出触发：执行类型升级并完成累加
			upgradeType(value);
		}
	}

	/**
	 * 类型升级核心逻辑：计算溢出后的准确值，切换到更大范围的类型
	 * <p>确保累加结果无精度丢失，通过BigInteger作为中间载体完成值传递。
	 * 
	 * @param value 导致溢出的累加值（long类型）
	 */
	private void upgradeType(long value) {
		// 1. 将当前值转为BigInteger：确保值的完整性，不受当前类型限制
		BigInteger currentValue = current.getAsBigInteger();
		// 2. 计算累加后的值：BigInteger无溢出风险，结果准确
		BigInteger newValue = currentValue.add(BigInteger.valueOf(value));
		// 3. 切换到合适的新类型：调用createAdderForValue选择最小能容纳newValue的类型
		current = createAdderForValue(newValue);
	}

	/**
	 * 根据BigInteger值创建“最小合适类型”的{@link NumberAdder}实例
	 * <p>类型选择优先级：int → long → BigInteger，避免过度使用高精度类型，平衡效率与范围。
	 * 
	 * @param value 待适配的BigInteger值
	 * @return 适配后的{@link NumberAdder}实例（{@link IntValue} / {@link LongValue} / {@link BigIntegerValue}）
	 */
	private NumberAdder createAdderForValue(BigInteger value) {
		try {
			// 尝试适配int：value.intValueExact()超范围则抛异常
			return new IntValue(value.intValueExact());
		} catch (ArithmeticException e1) {
			try {
				// 尝试适配long：同理，超范围抛异常
				return new LongValue(value.longValueExact());
			} catch (ArithmeticException e2) {
				// 仅当值超long范围时，使用BigIntegerValue
				return new BigIntegerValue(value);
			}
		}
	}

	/**
	 * 重置累加器到初始状态
	 * <p>恢复内容：
	 * - 数值：回到构造时的初始值；
	 * - 类型：回到构造时的初始类型（而非当前类型），例如初始为{@link IntValue}，升级后reset仍恢复为{@link IntValue}。
	 */
	@Override
	public void reset() {
		if (initialValue instanceof Integer) {
			current = new IntValue(initialValue.intValue());
		} else if (initialValue instanceof Long) {
			current = new LongValue(initialValue.longValue());
		} else if (initialValue instanceof BigInteger) {
			current = new BigIntegerValue((BigInteger) initialValue);
		}
	}

	/**
	 * 委托当前累加器获取int类型值
	 * <p>注意：若当前类型为{@link LongValue}或{@link BigIntegerValue}，且值超int范围，会发生精度丢失（遵循{@link Number#intValue()}规则）。
	 * 
	 * @return 当前值的int表示（可能丢失精度）
	 */
	@Override
	public int getAsInt() {
		return current.getAsInt();
	}

	/**
	 * 委托当前累加器获取long类型值
	 * <p>注意：若当前类型为{@link BigIntegerValue}且值超long范围，会发生精度丢失（遵循{@link Number#longValue()}规则）。
	 * 
	 * @return 当前值的long表示（可能丢失精度）
	 */
	@Override
	public long getAsLong() {
		return current.getAsLong();
	}

	/**
	 * 委托当前累加器获取BigInteger类型值
	 * <p>无精度丢失风险，所有类型均能完整转为BigInteger。
	 * 
	 * @return 当前值的BigInteger表示（完整无丢失）
	 */
	@Override
	public BigInteger getAsBigInteger() {
		return current.getAsBigInteger();
	}

	/**
	 * 委托当前累加器获取字符串表示
	 * <p>格式与当前类型一致，例如{@link IntValue}返回十进制int字符串，{@link BigIntegerValue}返回十进制大整数字符串。
	 * 
	 * @return 当前值的字符串表示
	 */
	@Override
	public String getAsString() {
		return current.getAsString();
	}

	/**
	 * 标记当前累加器为数值类型
	 * <p>固定返回true，因内部持有的{@link IntValue}、{@link LongValue}、{@link BigIntegerValue}均为数值类型。
	 * 
	 * @return true（始终为数值类型）
	 */
	@Override
	public boolean isNumber() {
		return current.isNumber();
	}

	/**
	 * 委托当前累加器与目标{@link Value}比较大小
	 * <p>比较逻辑与当前类型一致，例如当前为{@link LongValue}时，按long值大小比较。
	 * 
	 * @param o 目标比较对象（需为数值类型{@link Value}，否则可能抛出异常或返回默认比较结果）
	 * @return 比较结果：-1（当前值小）、0（相等）、1（当前值大）
	 * @throws ClassCastException 若目标对象非数值类型，可能由底层累加器抛出
	 */
	@Override
	public int compareTo(Value o) {
		return current.compareTo(o);
	}

	/**
	 * 获取当前活跃的累加器类型名称（用于调试、日志输出）
	 * 
	 * @return 当前类型的简单类名（如"IntValue"、"LongValue"、"BigIntegerValue"）
	 */
	public String getCurrentType() {
		return current.getClass().getSimpleName();
	}

	/**
	 * 累加器的字符串表示（含当前类型和值，便于调试）
	 * 
	 * @return 格式化字符串，格式：AutoExpandingNumberAdder[type=xxx, value=xxx]
	 */
	@Override
	public String toString() {
		return String.format("AutoExpandingNumberAdder[type=%s, value=%s]", getCurrentType(), getAsString());
	}

	/**
	 * 哈希值：委托当前累加器计算，确保相同值、相同类型的实例哈希一致
	 * 
	 * @return 当前累加器的哈希值
	 */
	@Override
	public int hashCode() {
		return current.hashCode();
	}

	/**
	 * 委托当前累加器获取BigDecimal类型值
	 * <p>无精度丢失风险，所有数值类型均能完整转为BigDecimal。
	 * 
	 * @return 当前值的BigDecimal表示（完整无丢失）
	 */
	@Override
	public BigDecimal getAsBigDecimal() {
		return current.getAsBigDecimal();
	}
}