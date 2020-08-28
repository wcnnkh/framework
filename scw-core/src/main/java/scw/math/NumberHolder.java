package scw.math;

import java.math.BigDecimal;

/**
 * 数字持有者
 * 
 * @author shuchaowen
 *
 */
public interface NumberHolder extends Comparable<NumberHolder>{
	/**
	 * 加法
	 * 
	 * @param numberHolder
	 * @return
	 */
	NumberHolder add(NumberHolder numberHolder);

	/**
	 * 减法
	 * 
	 * @param numberHolder
	 * @return
	 */
	NumberHolder subtract(NumberHolder numberHolder);

	/**
	 * 乘法
	 * 
	 * @param numberExtend
	 * @return
	 */
	NumberHolder multiply(NumberHolder numberHolder);

	/**
	 * 除法
	 * 
	 * @param numberHolder
	 * @return
	 */
	NumberHolder divide(NumberHolder numberHolder);

	/**
	 * 余数
	 * 
	 * @param numberHolder
	 * @return
	 */
	NumberHolder remainder(NumberHolder numberHolder);

	/**
	 * 指数运算
	 * 
	 * @param numberHolder
	 * @return
	 */
	NumberHolder pow(NumberHolder numberHolder);
	
	/**
	 * 绝对值
	 * @return
	 */
	NumberHolder abs();

	BigDecimal toBigDecimal();
}
