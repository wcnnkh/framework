package scw.message;

public class TextFragmentMessage extends TextMessage implements FragmentMessage<String>{
	private static final long serialVersionUID = 1L;
	private boolean last;
	
	TextFragmentMessage(String payload, boolean last) {
		super(payload);
		this.last = last;
	}

	public boolean isLast() {
		return last;
	}

}
