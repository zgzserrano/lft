public class NumberTok extends Token {
	public int number;
	
	public NumberTok(int tag, int num) {
		super(tag);
		number = num;
	}

	public String toString() {
		return "<" + tag + ", " + number + ">";
	}

}