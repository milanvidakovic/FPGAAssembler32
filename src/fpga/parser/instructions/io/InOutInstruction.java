package fpga.parser.instructions.io;

import org.apache.commons.lang3.ArrayUtils;

import fpga.parser.FPGAParseException;
import fpga.parser.Parser;
import fpga.parser.instructions.Instruction;
import fpga.parser.objects.Function;

public class InOutInstruction extends Instruction {

	public InOutInstruction(String name) {
		super(name);
	}

	@Override
	public byte[] generate() {
		if (this.name.equals("in")) {
			// in reg, [port]
			return ArrayUtils.addAll(new byte[] { (byte) this.destReg, (byte) 0x30 },
					super.toShortBytes(this.operand));
		} else {
			// out [port], reg
			return ArrayUtils.addAll(new byte[] { (byte) (this.srcReg<<4), (byte) 0x40 },
					super.toShortBytes(this.operand));		
		}
	}

	@Override
	public void prepare(Parser parser, Function f) throws FPGAParseException {

		if (super.name.equals("in")) {
			// in reg, [port]
			if (!super.parseRegMemNumber(Instruction.DEST_REG)) {
				if (!super.parseRegMemLabel(parser, Instruction.DEST_REG, f)) {
					throw new FPGAParseException("Unknown " + super.name.toUpperCase() + " argument in " + f.fileName + ", line "
							+ this.lineCount + ": " + this.source);
				}
			}
		} else {
			// out [port], reg
			if (!super.parseMemNumberReg(Instruction.SRC_REG)) {
				if (!super.parseMemLabelReg(parser, Instruction.SRC_REG, f)) {
					throw new FPGAParseException("Unknown " + super.name.toUpperCase() + " argument in " + f.fileName + ", line "
							+ this.lineCount + ": " + this.source);
				}
			}
		}

	}

}
