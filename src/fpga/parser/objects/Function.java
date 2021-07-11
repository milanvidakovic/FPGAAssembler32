package fpga.parser.objects;

import java.util.ArrayList;
import java.util.List;

import fpga.parser.instructions.Instruction;

public class Function extends AssemblerObject {
	
	public List<Instruction> instructions;

	public Function(AssemblerObject current, String fileName) {
		this.instructions = new ArrayList<Instruction>();
		this.name = current.name;
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return "Function [name=" + name + ", instructions=" + instructions + ", referenced from: " + super.getReferencedFrom() + "]\n";
	}
	
}
