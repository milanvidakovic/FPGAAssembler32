package fpga.parser.instructions.mov;

import fpga.parser.FPGAParseException;
import fpga.parser.Parser;
import fpga.parser.instructions.Instruction;
import fpga.parser.objects.Function;

public class PixInstruction extends Instruction {

	public PixInstruction(String name) {
		super(name);
	}

	@Override
	public byte[] generate() {
		return new byte[] { (byte) 0x00, (byte)0x9b};
	}

	@Override
	public void prepare(Parser parser, Function f) throws FPGAParseException {
	}
}
