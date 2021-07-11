package fpga.parser.instructions.mov;

import fpga.parser.FPGAParseException;
import fpga.parser.Parser;
import fpga.parser.instructions.Instruction;
import fpga.parser.objects.Function;

public class BlitInstruction extends Instruction {

	public BlitInstruction(String name) {
		super(name);
	}

	@Override
	public byte[] generate() {
		return new byte[] { (byte) 0x00, (byte)0x8b};
	}

	@Override
	public void prepare(Parser parser, Function f) throws FPGAParseException {
	}
}
