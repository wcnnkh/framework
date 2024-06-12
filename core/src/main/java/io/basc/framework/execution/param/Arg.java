package io.basc.framework.execution.param;

import java.io.Serializable;

import io.basc.framework.convert.lang.Value;
import lombok.Getter;

@Getter
public class Arg implements Parameter, Serializable {
	private static final long serialVersionUID = 1L;
	private int positionIndex;
	private String name;
	private Value value;

	public Arg(int positionIndex, Value value) {
		this.positionIndex = positionIndex;
	}

	public Arg(String name, Value value) {
		this.name = name;
		this.value = value;
	}
}
