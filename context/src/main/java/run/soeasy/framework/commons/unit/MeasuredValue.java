package run.soeasy.framework.commons.unit;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 有单位的值
 * 
 * @author soeasy.run
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeasuredValue implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 值
	 */
	private BigDecimal value;
	/**
	 * 单位
	 */
	private String unit;
}
