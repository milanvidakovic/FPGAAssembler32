package fpga.parser.instructions;

import fpga.parser.FPGAParseException;
import fpga.parser.Parser;
import fpga.parser.objects.Function;

public class DummyInstruction extends Instruction{

	public DummyInstruction(String name) {
		super(name);
	}

	@Override
	public byte[] generate() {
		return new byte[] {0, 0}; 
	}

	@Override
	public void prepare(Parser parser, Function f) throws FPGAParseException {
		
	}

}
