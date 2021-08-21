package fpga.parser.objects;

import java.util.ArrayList;
import java.util.List;

import fpga.parser.instructions.Instruction;

public class AssemblerObject {
	
	public static final int BYTE  	 = 10;
	public static final int SHORT 	 = 20;
	public static final int INT   	 = 40;
	public static final int FUNCTION = 99;
	
	public String name;
	public int type;
	
	public int address;

	public List<AssemblerObject> referencedFrom = new ArrayList<AssemblerObject>();

	/** Instruction to which this operand is assigned to. */
	public Instruction assignedTo;


	public String dataLabelName;
	public AssemblerObject dataLabel;
	public String stringContent;
	
	public int lineCount;
	public  String fileName;
	
	public AssemblerObject() {
	}
	
	public AssemblerObject(String name) {
		this();
		this.name = name;
	}
	
	public String getReferencedFrom() {
		StringBuilder refs = new StringBuilder();
		for (AssemblerObject i : this.referencedFrom) {
			refs.append (i.name + "->" + i.address + " (" + String.format("%08X", i.address) +"), ");
		}
		return refs.toString();
	}

	@Override
	public String toString() {
		return "AssemblerObject [name=" + name + ", type=" + type + ", address=" + address + ", dataLabelName="
				+ dataLabelName + ", dataLabel=" + dataLabel + ", stringContent=" + stringContent + ", lineCount="
				+ lineCount + ", fileName=" + fileName + "]";
	}

	
}
