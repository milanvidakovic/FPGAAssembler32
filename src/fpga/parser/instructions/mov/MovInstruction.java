package fpga.parser.instructions.mov;

import org.apache.commons.lang3.ArrayUtils;

import fpga.parser.FPGAParseException;
import fpga.parser.Parser;
import fpga.parser.instructions.Instruction;
import fpga.parser.objects.Function;

public class MovInstruction extends Instruction {

	boolean regToReg;

	public MovInstruction(String name) {
		super(name);
	}

	@Override
	public byte[] generate() {
		if (this.name.equals("swap")) {
			// swap reg, reg
			return new byte[] { (byte) ((this.srcReg << 4) + this.destReg), (byte) 0xa0 };
		} else if (this.name.endsWith(".w")) {
			// mov.w reg, reg
			if (this.regToReg) {
				int high = (this.srcReg << 4) + this.destReg;
				return new byte[] { (byte) (high), 0x10 };
			} else {
				// mov.w reg, num
				return ArrayUtils.addAll(new byte[] { (byte) this.destReg, (byte) 0xb0 },
						super.toIntBytes(this.operand));
			}
		} else if (this.name.endsWith(".s")) {
			// mov.s reg, num
			return ArrayUtils.addAll(new byte[] { (byte) this.destReg, 0x20 }, super.toShortBytes(this.operand));
		} else {
			// mov.b reg, num
			return ArrayUtils.addAll(new byte[] { (byte) this.destReg, (byte) 0xd0 }, super.toShortBytes(this.operand));
		}
	}

	@Override
	public void prepare(Parser parser, Function f) throws FPGAParseException {

		if (super.parseRegReg(Instruction.SRC_REG) && super.parseRegReg(Instruction.DEST_REG)) {
			this.regToReg = true;
		} else {
			this.regToReg = false;
			if (!super.parseRegNumber(Instruction.DEST_REG)) {
				if (!super.parseRegLabel(parser, Instruction.DEST_REG, f)) {
					throw new FPGAParseException("Unknown MOV argument in " + f.fileName + ", line "
							+ this.lineCount + ": " + this.source);
				}
			}
		}
	}

}
