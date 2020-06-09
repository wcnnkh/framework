package scw.math;

import java.math.BigDecimal;

/**
 * 数字扩展
 * @author shuchaowen
 *
 */
public interface NumberExtend {
	/**
	 * 加法
	 * 
	 * @param numberExtend
	 * @return
	 */
	NumberExtend add(NumberExtend numberExtend);

	/**
	 * 减法
	 * 
	 * @param numberExtend
	 * @return
	 */
	NumberExtend subtract(NumberExtend numberExtend);

	/**
	 * 乘法
	 * 
	 * @param numberExtend
	 * @return
	 */
	NumberExtend multiply(NumberExtend numberExtend);

	/**
	 * 除法
	 * 
	 * @param numberExtend
	 * @return
	 */
	NumberExtend divide(NumberExtend numberExtend);
	
	/**
	 * 余数
	 * @param numberExtend
	 * @return
	 */
	NumberExtend remainder(NumberExtend numberExtend);
	
	/**
	 * 指数运算
	 * @param numberExtend
	 * @return
	 */
	NumberExtend pow(NumberExtend numberExtend);

	BigDecimal toBigDecimal();
}
