package fpga.parser.instructions;

import fpga.parser.FPGAParseException;
import fpga.parser.Parser;
import fpga.parser.objects.Function;

public class Label extends Instruction {
	
	public Label(String name) {
		super(name);
	}

	@Override
	public String toString() {
		if (assignedTo != null && referencedFrom != null)
			return "Label [name=" + name + ", assignedTo=" + assignedTo.name + ", address=" + assignedTo.address + "(" + String.format("%08x", assignedTo.address) + ")"+ ", referencedFrom= " + getReferencedFrom().toString() + "]";
		else {
			if (referencedFrom != null)
				return "Label [name=" + name + ", assignedTo= null, address=null, (null)"+ ", referencedFrom= " + getReferencedFrom().toString() + "]";
			else
				return "Label [name=" + name + ", assignedTo= null, address=null, (null)"+ ", referencedFrom=null]";
		}
	}


	@Override
	public byte[] generate() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void prepare(Parser parser, Function f) throws FPGAParseException {
		// TODO Auto-generated method stub
		
	}
	
	
}
