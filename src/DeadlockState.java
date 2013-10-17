public class DeadlockState {
		private char i11, i12, i13, i21, i22, i23, i31, i32, i33;
		private int hashCode;
		private String sequence;
		public DeadlockState(char i11, char i12, char i13, char i21, char i22, char i23, char i31, char i32, char i33) {
			this.i11 = i11;
			this.i12 = i12;
			this.i13 = i13;
			this.i21 = i21;
			this.i22 = i22;
			this.i23 = i23;
			this.i31 = i31;
			this.i32 = i32;
			this.i33 = i33;
			hashCode = -1;
		}
		
		public DeadlockState(String sequence) {
			this.sequence = sequence;
			hashCode = -1;
		}
		
		@Override
		public int hashCode() {
			if(hashCode != -1) {
				return hashCode;
			}
			else {
				if(sequence ==null ) {
					char[] sequence = {i11,i12,i13,i21,i22,i23,i31,i32,i33};
					hashCode = String.copyValueOf(sequence).hashCode();
				} else {
					hashCode = sequence.hashCode();
				}
				return hashCode;
			}
		}
	}