package fpga.parser.instructions.mem;

import org.apache.commons.lang3.ArrayUtils;

import fpga.parser.FPGAParseException;
import fpga.parser.Parser;
import fpga.parser.instructions.Instruction;
import fpga.parser.objects.Function;

public class StInstruction extends Instruction {
	
	
	private static final int MEM_REG_REG 		= 1;
	private static final int MEM_NUM_REG 		= 2;
	private static final int MEM_LABEL_REG 		= 3;
	private static final int MEM_REG_NUM_REG 	= 4;
	
	int type;
	
	public StInstruction(String name) {
		super(name);
	}

	@Override
	public byte[] generate() {
		
		if (this.name.endsWith(".b")) {
			switch (this.type) {
			case MEM_REG_REG:
				// st.b [reg], reg
				return new byte[] { (byte) ((this.srcReg<<4) + this.destReg), (byte)0xb3 };
			case MEM_NUM_REG:
			case MEM_LABEL_REG:
				// st.b [num], reg
				return ArrayUtils.addAll(new byte[] { (byte) ((this.srcReg<<4) + this.destReg), (byte)0xc3 },
						super.toIntBytes(this.operand));
			case MEM_REG_NUM_REG:
				// st.b [reg + (num)], reg
				return ArrayUtils.addAll(new byte[] { (byte) ((this.srcReg<<4) + this.destReg), (byte)0xd3 },
						super.toIntBytes(this.operand));
			}
		} else if (this.name.endsWith(".s")) {
			switch (this.type) {
			case MEM_REG_REG:
				// st.s [reg], reg
				return new byte[] { (byte) ((this.srcReg<<4) + this.destReg), (byte)0x83 };
			case MEM_NUM_REG:
			case MEM_LABEL_REG:
				// st.s [num], reg
				return ArrayUtils.addAll(new byte[] { (byte) ((this.srcReg<<4) + this.destReg), (byte)0x93 },
						super.toIntBytes(this.operand));
			case MEM_REG_NUM_REG:
				// st.s [reg + (num)], reg
				return ArrayUtils.addAll(new byte[] { (byte) ((this.srcReg<<4) + this.destReg), (byte)0xa3 },
						super.toIntBytes(this.operand));
			}
		} else {
			switch (this.type) {
			case MEM_REG_REG:
				// st.w [reg], reg
				return new byte[] { (byte) ((this.srcReg<<4) + this.destReg), (byte)0x8c };
			case MEM_NUM_REG:
			case MEM_LABEL_REG:
				// st.w [num], reg
				return ArrayUtils.addAll(new byte[] { (byte) ((this.srcReg<<4) + this.destReg), (byte)0x9c },
						super.toIntBytes(this.operand));
			case MEM_REG_NUM_REG:
				// st.w [reg + (num)], reg
				return ArrayUtils.addAll(new byte[] { (byte) ((this.srcReg<<4) + this.destReg), (byte)0xac },
						super.toIntBytes(this.operand));
			}
		}
		
		return null;
	}

	@Override
	public void prepare(Parser parser, Function f) throws FPGAParseException {
		if (super.parseMemRegReg(Instruction.DEST_REG) && super.parseMemRegReg(Instruction.SRC_REG)) {
			this.type = MEM_REG_REG;
		} else {
			if (!super.parseMemNumberReg(Instruction.SRC_REG)) {
				if (!super.parseMemLabelReg(parser, Instruction.SRC_REG, f)) {
					if (!super.parseMemRegNumberReg(Instruction.SRC_REG)) {
						throw new FPGAParseException("Unknown STORE argument in " + f.fileName + ", line " + this.lineCount + ": " + this.source);
					} else {
						super.parseMemRegNumberReg(Instruction.DEST_REG);
						this.type = MEM_REG_NUM_REG;
					}
				} else {
					this.type = MEM_LABEL_REG;
				}
			} else {
				this.type = MEM_NUM_REG;
			}
		}
	}

}
