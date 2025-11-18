package run.soeasy.framework.math;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import lombok.NonNull;
import run.soeasy.framework.core.NumberUtils;
import run.soeasy.framework.core.match.Operator;
import run.soeasy.framework.core.match.Operators;

/**
 * 通用运算计算器：支持算术、比较、逻辑（二元+单目）运算，可扩展其他运算类型
 * <p>
 * 核心特性： 1. 线程安全（ConcurrentHashMap 存储运算逻辑）； 2. 严格非空校验（lombok @NonNull）； 3.
 * 内置所有指定运算符运算逻辑： - 算术运算（+、-、*、/、%）：BigDecimal 高精度，支持异常处理； -
 * 比较运算（==、!=、&gt;、&gt;=、&lt;、&lt;=）：返回 1（true）/0（false）； -
 * 逻辑运算（&amp;&amp;、||、!）：二元与/或，单目非，按&quot;非0为true&quot;规则； 4. 支持自定义覆盖（手动注册可覆盖内置运算）； 5.
 * 预留扩展空间，可新增三元等其他运算类型。
 * </p>
 *
 * @author soeasy.run
 */
public class Calculator {
	private static volatile Calculator instance;

	public static Calculator getInstance() {
		if (instance == null) {
			synchronized (Calculator.class) {
				if (instance == null) {
					instance = new Calculator();
				}
			}
		}
		return instance;
	}

	// 二元运算映射（算术、比较、逻辑与/或）
	private final Map<Operator, BinaryOperator<Number>> binaryOperationMap = new ConcurrentHashMap<>();
	// 单目运算映射（逻辑非 !）
	private final Map<Operator, UnaryOperator<Number>> unaryOperationMap = new ConcurrentHashMap<>();

	public Calculator() {
		binaryOperationMap.put(Operators.MULTIPLY, new MultiplyOperation());
		binaryOperationMap.put(Operators.DIVIDE, new DivideOperation());
		binaryOperationMap.put(Operators.MOD, new ModOperation());
		binaryOperationMap.put(Operators.PLUS, new AddOperation());
		binaryOperationMap.put(Operators.MINUS, new SubtractOperation());
	}

	public Calculator(@NonNull Calculator calculator) {
		this.binaryOperationMap.putAll(calculator.binaryOperationMap);
		this.unaryOperationMap.putAll(calculator.unaryOperationMap);
	}

	public Calculator copy() {
		return new Calculator(this);
	}

	// ====================== 二元运算：注册与执行 ======================
	/**
	 * 注册二元运算符及其运算逻辑
	 *
	 * @param operator       二元运算符（不可为 &quot;null&quot;）
	 * @param binaryOperator 二元运算逻辑（不可为 &quot;null&quot;，精度/返回值由实现者负责）
	 */
	public void registerBinary(@NonNull Operator operator, @NonNull BinaryOperator<Number> binaryOperator) {
		binaryOperationMap.put(operator, binaryOperator);
	}

	/**
	 * 执行二元运算（算术、比较、逻辑与/或）
	 *
	 * @param left     左操作数（不可为 &quot;null&quot;，Number 子类均可）
	 * @param right    右操作数（不可为 &quot;null&quot;，Number 子类均可）
	 * @param operator 目标运算符（需提前注册）
	 * @return 运算结果：算术运算返回 BigDecimal，比较/逻辑运算返回 Integer（1=true/0=false）
	 * @throws IllegalStateException 运算符未注册
	 * @throws ArithmeticException   算术运算异常（如除数为0）
	 */
	public Number calculateBinary(@NonNull Number left, @NonNull Number right, @NonNull Operator operator) {
		BinaryOperator<Number> logic = getBinaryOperator(operator);
		if (logic == null) {
			throw new IllegalStateException(
					String.format("二元运算符 [%s] 未注册，请先调用 registerBinary 方法注册", operator.getSymbol()));
		}
		return logic.apply(left, right);
	}

	public BinaryOperator<Number> getBinaryOperator(@NonNull Operator operator) {
		return binaryOperationMap.get(operator);
	}

	// ====================== 单目运算：注册与执行（逻辑非 !） ======================
	/**
	 * 注册单目运算符及其运算逻辑
	 *
	 * @param operator      单目运算符（不可为 &quot;null&quot;）
	 * @param unaryOperator 单目运算逻辑（不可为 &quot;null&quot;）
	 */
	public void registerUnary(@NonNull Operator operator, @NonNull UnaryOperator<Number> unaryOperator) {
		unaryOperationMap.put(operator, unaryOperator);
	}

	public UnaryOperator<Number> getUnaryOperator(@NonNull Operator operator) {
		return unaryOperationMap.get(operator);
	}

	/**
	 * 执行单目运算（当前仅支持逻辑非 !）
	 *
	 * @param operand  操作数（不可为 &quot;null&quot;，Number 子类均可）
	 * @param operator 目标运算符（需提前注册）
	 * @return 运算结果：Integer（1=true/0=false）
	 * @throws IllegalStateException 运算符未注册
	 */
	public Number calculateUnary(@NonNull Number operand, @NonNull Operator operator) {
		UnaryOperator<Number> logic = unaryOperationMap.get(operator);
		if (logic == null) {
			throw new IllegalStateException(
					String.format("单目运算符 [%s] 未注册，请先调用 registerUnary 方法注册", operator.getSymbol()));
		}
		return logic.apply(operand);
	}

	/**
	 * 通用分商取余运算：支持所有Number类型（含自定义Number子类），返回商（索引0）和余数（索引1）。
	 * <p>
	 * 核心依赖（必须遵守，否则结果不一致）：
	 * 1. 语义契约：底层除法（Operators.DIVIDE）需遵循Java原生「向零取整」规则，取模（Operators.MOD）需遵循本框架 {@link ModOperation} 规则（余数符号与被除数一致）；
	 * 2. 精度契约：底层运算（DIVIDE/MOD/MINUS）均为无限精度，无精度损失；
	 * 3. 类型契约：不同类型混合运算时（如int+long），底层 {@link #calculateBinary} 需做类型提升（如int→long），确保运算结果类型一致。
	 * </p>
	 * <p>
	 * 结果特性：
	 * - 数学关系：商 × 除数 + 余数 = 被除数（严格成立）；
	 * - 类型适配：结果类型与输入类型一致（或类型提升后的类型），浮点类型无精度损失则返回原生类型，否则返回BigDecimal；
	 * - 特殊场景：
	 *   - 被除数为0 → 商=0、余数=0；
	 *   - 除数为1 → 商=被除数、余数=0；
	 *   - 被除数=除数 → 商=1、余数=0。
	 * </p>
	 *
	 * @param left 被除数（非空，任意Number类型）
	 * @param right 除数（非空，任意Number类型，不能为0）
	 * @return Number[]：索引0为商，索引1为余数
	 * @throws ArithmeticException 当除数为0时抛出
	 * @throws UnsupportedOperationException 当底层运算不支持输入类型时抛出
	 * @see ModOperation 取模运算实现（定义余数规则）
	 */
	public Number[] divideAndRemainder(@NonNull Number left, @NonNull Number right) {
	    // 入口校验：除数为0（统一异常，比底层运算抛出更直观）
	    if (NumberUtils.isZero(right)) { // 可复用 NumberUtils.isZero 或自行实现
	        throw new ArithmeticException(String.format("分商取余运算：除数不能为0 [被除数类型=%s, 除数类型=%s, 被除数=%s, 除数=%s]",
	                left.getClass().getSimpleName(), right.getClass().getSimpleName(), left, right));
	    }

	    Number[] result = new Number[2];
	    // 余数：依赖 ModOperation 的规则（余数符号与被除数一致）
	    result[1] = calculateBinary(left, right, Operators.MOD);
	    // 商：(被除数 - 余数) ÷ 除数（依赖除法向零取整规则）
	    Number subtractResult = calculateBinary(left, result[1], Operators.MINUS);
	    result[0] = calculateBinary(subtractResult, right, Operators.DIVIDE);
	    return result;
	}
}