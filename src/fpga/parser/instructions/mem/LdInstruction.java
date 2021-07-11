package fpga.parser.instructions.mem;

import org.apache.commons.lang3.ArrayUtils;

import fpga.parser.FPGAParseException;
import fpga.parser.Parser;
import fpga.parser.instructions.Instruction;
import fpga.parser.objects.Function;

public class LdInstruction extends Instruction {

	public static final int REG_MEMREG = 1;
	public static final int REG_MEM_NUM = 2;
	public static final int REG_MEM_LABEL = 3;
	public static final int REG_MEMREG_NUM = 4;

	int type;

	public LdInstruction(String name) {
		super(name);
	}

	@Override
	public byte[] generate() {
		if (this.name.endsWith(".b")) {
			switch (this.type) {
			case REG_MEMREG:
				// ld.b reg, [reg]
				return new byte[] { (byte) ((this.srcReg<<4) + this.destReg), 0x33 };
			case REG_MEM_NUM:
			case REG_MEM_LABEL:
				// ld.b reg, [num]
				return ArrayUtils.addAll(new byte[] { (byte) ((this.srcReg<<4) + this.destReg), 0x43 },
						super.toIntBytes(this.operand));
			case REG_MEMREG_NUM:
				// ld.b reg, [reg + (num)]
				return ArrayUtils.addAll(new byte[] { (byte) ((this.srcReg<<4) + this.destReg), 0x53 },
						super.toIntBytes(this.operand));
			}
		} else if (this.name.endsWith(".s")) {
			switch (this.type) {
			case REG_MEMREG:
				// ld.s reg, [reg]
				return new byte[] { (byte) ((this.srcReg << 4) + this.destReg), 0x03 };
			case REG_MEM_NUM:
			case REG_MEM_LABEL:
				// ld.s reg, [num]
				return ArrayUtils.addAll(new byte[] { (byte) ((this.srcReg << 4) + this.destReg), 0x13 },
						super.toIntBytes(this.operand));
			case REG_MEMREG_NUM:
				// ld.s reg, [reg + (num)]
				return ArrayUtils.addAll(new byte[] { (byte) ((this.srcReg << 4) + this.destReg), 0x23 },
						super.toIntBytes(this.operand));
			}
		} else {
			switch (this.type) {
			case REG_MEMREG:
				// ld.w reg, [reg]
				return new byte[] { (byte) ((this.srcReg <<4) + this.destReg), 0x0c };
			case REG_MEM_NUM:
			case REG_MEM_LABEL:
				// ld.w reg, [num]
				return ArrayUtils.addAll(new byte[] { (byte) ((this.srcReg << 4) + this.destReg), 0x1c },
						super.toIntBytes(this.operand));
			case REG_MEMREG_NUM:
				// ld.w reg, [reg + (num)]
				return ArrayUtils.addAll(new byte[] { (byte) ((this.srcReg << 4) + this.destReg), 0x2c },
						super.toIntBytes(this.operand));
			}
		}

		return null;
	}

	@Override
	public void prepare(Parser parser, Function f) throws FPGAParseException {
		
		if (super.parseRegMemReg(Instruction.DEST_REG) && super.parseRegMemReg(Instruction.SRC_REG)) {
			this.type = REG_MEMREG;
		} else {
			if (!super.parseRegMemNumber(Instruction.DEST_REG)) {
				if (!super.parseRegMemLabel(parser, Instruction.DEST_REG, f)) {
					if (!super.parseRegMemRegNumber(Instruction.DEST_REG)) {
						if (!super.parseRegMemRegLabel(parser, Instruction.DEST_REG)) {
							throw new FPGAParseException("Unknown LOAD argument in " + f.fileName + ", line " + this.lineCount + ": " + this.source);
						} else {
							super.parseRegMemRegLabel(parser, Instruction.SRC_REG);
							this.type = REG_MEMREG_NUM;
						}
					} else {
						super.parseRegMemRegNumber(Instruction.SRC_REG);
						this.type = REG_MEMREG_NUM;
					}
				} else {
					this.type = REG_MEM_LABEL;
				}
			} else {
				this.type = REG_MEM_NUM;
			}
		}

	}

}
