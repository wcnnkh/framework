package scw.messageing;

public class TextFragmentMessage extends TextMessage implements FragmentMessage<String>{
	private static final long serialVersionUID = 1L;
	private boolean last;
	
	public TextFragmentMessage(String payload, boolean last) {
		super(payload);
		this.last = last;
	}

	public boolean isLast() {
		return last;
	}

}
