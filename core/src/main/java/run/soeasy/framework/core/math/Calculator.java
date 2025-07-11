package run.soeasy.framework.core.math;

/**
 * 运算器/计算器
 * 
 * @author soeasy.run
 *
 */
public interface Calculator extends MathFunction<NumberValue, NumberValue> {

	/**
	 * 计算符
	 * 
	 * @return
	 */
	String getOperator();
}
