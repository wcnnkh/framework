package io.basc.framework.text;

import java.text.FieldPosition;
import java.text.Format.Field;

import io.basc.framework.util.ParentDiscover;

public class FormatPosition extends FieldPosition implements ParentDiscover<FormatPosition> {
	private FieldPosition sourcePosition;

	public FormatPosition(int field) {
		super(field);
	}

	public FormatPosition(Field attribute, int fieldID) {
		super(attribute, fieldID);
	}

	public FormatPosition(Field attribute) {
		super(attribute);
	}

	public FieldPosition getSourcePosition() {
		return sourcePosition;
	}

	public void setSourcePosition(FieldPosition sourcePosition) {
		this.sourcePosition = sourcePosition;
	}

	@Override
	public FormatPosition getParent() {
		if (sourcePosition == null) {
			return null;
		}

		if (sourcePosition instanceof FormatPosition) {
			return (FormatPosition) sourcePosition;
		}
		return null;
	}
}
