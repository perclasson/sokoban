public class DeadlockState {
	private int hashCode;
	private String sequence;

	public DeadlockState(char i11, char i12, char i13, char i21, char i22, char i23, char i31, char i32, char i33) {
		char[] seq = { i11, i12, i13, i21, i22, i23, i31, i32, i33 };
		sequence = new String(seq);
		sequence = sequence.replaceAll("D", " ");
		sequence = sequence.replaceAll("\\.", " ");
		hashCode = sequence.hashCode();
	}

	public String getSequence() {
		return sequence;
	}

	public String toString() {
		// return "\n"+sequence.substring(0,3)+"\n"+sequence.substring(3,
		// 6)+"\n"+sequence.substring(6, 9);
		return sequence;
	}

	@Override
	public boolean equals(Object o) {
		return ((DeadlockState) o).getSequence().equals(getSequence());
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
}