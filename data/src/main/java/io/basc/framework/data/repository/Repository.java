package io.basc.framework.data.repository;

import java.io.Serializable;

import io.basc.framework.util.Named;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Repository implements Serializable, Named {
	private static final long serialVersionUID = 1L;
	/**
	 * 名称
	 */
	@NonNull
	private final String name;
}
