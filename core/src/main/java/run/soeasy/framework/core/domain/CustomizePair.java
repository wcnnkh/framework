package run.soeasy.framework.core.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomizePair<L, R> implements Pair<L, R>, Serializable {
	private static final long serialVersionUID = 1L;
	private L left;
	private R right;
}
