package run.soeasy.framework.core.convert;

import lombok.NonNull;

class DirectlyConverter extends IdentityConverter {
	static final DirectlyConverter INSTANCE = new DirectlyConverter();

	@Override
	public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		if (super.canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
			return true;
		}

		if (targetTypeDescriptor.getType() == Object.class) {
			return true;
		}
		return false;
	}

}
