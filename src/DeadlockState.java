public class DeadlockState {
	private int hashCode;
	private String sequence;
	private char[] seq;
	private boolean is3x3;


	public DeadlockState(char i11, char i12, char i13, char i21, char i22, char i23, char i31, char i32, char i33) {
		char[] seq = { i11, i12, i13, i21, i22, i23, i31, i32, i33 };
		sequence = String.valueOf(seq);
		sequence = sequence.replaceAll("D", " ");
		sequence = sequence.replaceAll("\\.", " ");
		this.seq = sequence.toCharArray();
		hashCode = -1;
		is3x3 = true;
	}
	
	public DeadlockState(char i11, char i12, char i21, char i22) {
		char[] seq = { i11, i12, i21, i22 };
		sequence = String.valueOf(seq);
		sequence = sequence.replaceAll("D", " ");
		sequence = sequence.replaceAll("\\.", " ");
		this.seq = sequence.toCharArray();
		hashCode = -1;
		is3x3 = false;
	}

	public boolean is3x3() {
		return is3x3;
	}
	public String getSequence() {
		return sequence;
	}
	
	public void setCharAt(int y, int x, char newChar) {
		int index = y*3+x;
		if(!is3x3)
			index = y*2+x;
		seq[index] = newChar;
		sequence = String.valueOf(seq);
	}

	public String toString() {
		 return sequence;
	}

	@Override
	public boolean equals(Object o) {
		return ((DeadlockState) o).getSequence().equals(getSequence());
	}

	@Override
	public int hashCode() {
		if(hashCode == -1)
			hashCode = sequence.hashCode();
		return hashCode;
	}
}