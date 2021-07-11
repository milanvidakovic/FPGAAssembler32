package fpga.parser.instructions.arithm;

import org.apache.commons.lang3.ArrayUtils;

import fpga.parser.FPGAParseException;
import fpga.parser.Parser;
import fpga.parser.instructions.Instruction;
import fpga.parser.objects.Function;

public class ArithmInstruction extends Instruction {

	boolean isRegReg;

	public ArithmInstruction(String name) {
		super(name);
	}

	@Override
	public byte[] generate() throws FPGAParseException {
		switch (super.name) {
		case "add.w":
			if (this.isRegReg) {
				return new byte[] { (byte) ((this.srcReg << 4) + this.destReg), 0x04 };
			} else {
				return ArrayUtils.addAll(new byte[] { (byte) ((this.srcReg << 4) + this.destReg), 0x0d },
						super.toIntBytes(this.operand));
			}
		case "sub.w":
			if (this.isRegReg) {
				return new byte[] { (byte) ((this.srcReg << 4) + this.destReg), (byte) 0x84 };
			} else {
				return ArrayUtils.addAll(new byte[] { (byte) ((this.srcReg << 4) + this.destReg), 0x1d },
						super.toIntBytes(this.operand));
			}
		case "and.w":
			if (this.isRegReg) {
				return new byte[] { (byte) ((this.srcReg << 4) + this.destReg), 0x05 };
			} else {
				return ArrayUtils.addAll(new byte[] { (byte) ((this.srcReg << 4) + this.destReg), 0x2d },
						super.toIntBytes(this.operand));
			}
		case "or.w":
			if (this.isRegReg) {
				return new byte[] { (byte) ((this.srcReg << 4) + this.destReg), (byte) 0x85 };
			} else {
				return ArrayUtils.addAll(new byte[] { (byte) ((this.srcReg << 4) + this.destReg), 0x3d },
						super.toIntBytes(this.operand));
			}
		case "xor.w":
			if (this.isRegReg) {
				return new byte[] { (byte) ((this.srcReg << 4) + this.destReg), (byte) 0x06 };
			} else {
				return ArrayUtils.addAll(new byte[] { (byte) ((this.srcReg << 4) + this.destReg), 0x4d },
						super.toIntBytes(this.operand));
			}
		case "neg.w":
			return new byte[] { (byte) (this.srcReg << 4), (byte) 0x86 };
		case "shl.w":
			if (this.isRegReg) {
				return new byte[] { (byte) ((this.srcReg << 4) + this.destReg), (byte) 0x07 };
			} else {
				return ArrayUtils.addAll(new byte[] { (byte) ((this.srcReg << 4) + this.destReg), 0x5d },
						super.toIntBytes(this.operand));
			}
		case "shr.w":
			if (this.isRegReg) {
				return new byte[] { (byte) ((this.srcReg << 4) + this.destReg), (byte) 0x87 };
			} else {
				return ArrayUtils.addAll(new byte[] { (byte) ((this.srcReg << 4) + this.destReg), 0x6d },
						super.toIntBytes(this.operand));
			}
		case "mul.w":
			if (this.isRegReg) {
				return new byte[] { (byte) ((this.srcReg << 4) + this.destReg), (byte) 0x08 };
			} else {
				return ArrayUtils.addAll(new byte[] { (byte) ((this.srcReg << 4) + this.destReg), 0x7d },
						super.toIntBytes(this.operand));
			}
		case "div.w":
			if (this.isRegReg) {
				return new byte[] { (byte) ((this.srcReg << 4) + this.destReg), (byte) 0x88 };
			} else {
				return ArrayUtils.addAll(new byte[] { (byte) ((this.srcReg << 4) + this.destReg), (byte) 0x8d },
						super.toIntBytes(this.operand));
			}
		case "inc.w":
			return new byte[] { (byte) (this.destReg), (byte) 0x09 };
		case "dec.w":
			return new byte[] { (byte) (this.destReg), (byte) 0x89 };
		case "cmp.w":
			if (this.isRegReg) {
				return new byte[] { (byte) ((this.srcReg << 4) + this.destReg), (byte) 0x0a };
			} else {
				return ArrayUtils.addAll(new byte[] { (byte) ((this.srcReg << 4) + this.destReg), (byte) 0x9d },
						super.toIntBytes(this.operand));
			}
		case "inv.w":
			return new byte[] { (byte) (this.destReg), (byte) 0x8a };
		default:
			throw new FPGAParseException("Instruction " + super.name + " is not generating code.");
		}
	}

	@Override
	public void prepare(Parser parser, Function f) throws FPGAParseException {
		switch (super.name) {
		case "neg.w":
			if (!super.parseReg(Instruction.SRC_REG)) {
				throw new FPGAParseException("Unknown " + super.name.toUpperCase() + " argument in " + f.fileName
						+ ", line " + this.lineCount + ": " + this.source);
			}
		case "inv.w":
		case "inc.w":
		case "dec.w":
			if (!super.parseReg(Instruction.DEST_REG)) {
				throw new FPGAParseException("Unknown " + super.name.toUpperCase() + " argument in " + f.fileName
						+ ", line " + this.lineCount + ": " + this.source);
			}
			break;
		default:
			if (!super.parseRegReg(Instruction.SRC_REG) || !super.parseRegReg(Instruction.DEST_REG)) {
				if (!super.parseRegNumber(Instruction.DEST_REG)) {
					if (!super.parseRegLabel(parser, Instruction.DEST_REG, f)) {
						throw new FPGAParseException("Unknown " + super.name.toUpperCase() + " argument in "
								+ f.fileName + ", line " + this.lineCount + ": " + this.source);
					}
				} else {
					this.isRegReg = false;
				}
			} else {
				this.isRegReg = true;
			}
		}
	}

}
