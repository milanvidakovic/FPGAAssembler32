package fpga.parser.instructions.irq;

import org.apache.commons.lang3.ArrayUtils;

import fpga.parser.FPGAParseException;
import fpga.parser.Parser;
import fpga.parser.instructions.Instruction;
import fpga.parser.objects.Function;

public class IntInstruction extends Instruction{

	public IntInstruction(String name) {
		super(name);
	}

	@Override
	public byte[] generate() {
		// int addr
		return ArrayUtils.addAll(new byte[] { 0x00, (byte)0xfb },
				super.toShortBytes(this.operand));

	}

	@Override
	public void prepare(Parser parser, Function f) throws FPGAParseException {
		// int addr
		if (!super.parseNumber()) {
			throw new FPGAParseException("Unknown " + super.name.toUpperCase() + " argument in " + f.fileName + ", line "
					+ this.lineCount + ": " + this.source);
		}
	}

}
