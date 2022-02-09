package fpga.parser;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.text.StringEscapeUtils;

import fpga.parser.instructions.Instruction;
import fpga.parser.instructions.Instructions;
import fpga.parser.instructions.Label;
import fpga.parser.objects.AssemblerObject;
import fpga.parser.objects.Constant;
import fpga.parser.objects.Function;
import fpga.parser.objects.Variable;

// TODO: array[5] = 4; is translated into st.w [array + 20], r1 <== array + 20 needs to be parsed
public class Parser {
	public static final int IDLE = 0;
	public static final int START = 10;
	public static final int STRING = 11;
	public static final int FUNCTION = 20;

	public int lineCount;
	public String fileName;

	public Map<String, Variable> globalVars;
	public Map<String, Variable> strings;
	public Map<String, Function> functions;
	public Map<String, Label> labels;
	public Map<String, Constant> constants;

	public AssemblerObject current;
	Variable currentString;

	int state;

	public Instructions instructions = new Instructions();
	public String mainFileName;

	public Parser(String mainFileName) {
		this.mainFileName = mainFileName;
		this.globalVars = new HashMap<String, Variable>();
		this.functions = new HashMap<String, Function>();
		this.strings = new HashMap<String, Variable>();
		this.labels = new HashMap<String, Label>();
		this.constants = new HashMap<String, Constant>();

		this.state = IDLE;
		this.current = null;
	}

	// TODO: static functions!
	// TODO: #include <file.c>!
	public void parse(String line, int lineCount, String fileName, boolean verbose)
			throws FPGAParseException, CloneNotSupportedException {
		this.lineCount = lineCount;
		this.fileName = fileName;
		String firstToken = getToken(line, 0);
		if (firstToken.equals(".global") || line.equals("")) {
			if (this.current != null) {
				if (this.current instanceof Variable) {
//					System.out.println(this.current.name + ", " + fileName);
					((Variable) this.current).type = getType((Variable) this.current);
					if (this.globalVars.get(this.current.name) == null)
						this.globalVars.put(this.current.name, ((Variable) this.current));
					else {
						if (verbose)
							System.err.println("WARNING: Global variable " + this.current.name
									+ " already exists. Filename: " + fileName + " at line: " + line);
					}
				} else {
//					System.out.println(this.current.name + ", " + fileName);
					try {
						if (this.functions.get(this.current.name) == null || this.current.name.equals("main")) {
							this.functions.put(this.current.name, ((Function) this.current));
						} else {
							if (verbose)
								System.err.println("WARNING: Function " + this.current.name
										+ " already exists. Filename: " + fileName + " at line: " + line);
						}
					} catch (Exception ex) {
						if (verbose)
							System.err.println("WARNING: Function " + this.current.name
									+ " does not exist (probably internal GCC function). Filename: " + fileName + " at line: " + line);
//						System.err.println(ex.getMessage());
//						System.out.println(this.current.name + ", " + fileName);
					}
				}
			}
			if (line.equals(""))
				return;
			String name = getToken(line, 1);
			if (name == null)
				throw new FPGAParseException("Missing global variable name. Line: " + lineCount + ": " + line);
			AssemblerObject o = new AssemblerObject(name);
			this.current = o;
			this.state = START;
			return;
		}
		switch (this.state) {
		case IDLE:
			if (firstToken.startsWith(".") && firstToken.endsWith(":")) {
				String name = line.substring(0, line.length() - 1);
				Variable v = new Variable(name);
				v.fileName = this.fileName;
				v.lineCount = lineCount;
				this.currentString = v;
				this.strings.put(fileName + name, v);
				this.state = STRING;
				return;
			} else if (firstToken.equals(".equ")) {
				parseConst(line);
				return;
			}
			break;
		case START:
			firstToken = getToken(line, 0);
			if (firstToken.startsWith(".") && firstToken.endsWith(":")) {
				String name = line.substring(0, line.length() - 1);
				Variable v = new Variable(name);
				v.fileName = this.fileName;
				v.lineCount = lineCount;
				this.currentString = v;
				this.strings.put(fileName + name, v);
				this.state = STRING;
				return;
			} else if (firstToken.equals(".equ")) {
				parseConst(line);
				return;
			}
			switch (firstToken) {
			case ".type":
				String type = getTokenAfter(line, ',');
				type = getToken(type, 0);
				switch (type) {
				case "@object":
					this.current = new Variable(this.current);
					break;
				case "@function":
					this.current = new Function(this.current, fileName);
					this.current.type = AssemblerObject.FUNCTION;
					this.state = FUNCTION;

					break;
				}
				break;
			case ".size":
				String content = getToken(line, 2);
				int size = 0;
				if (content != null && !content.equals("")) {
					size = Integer.parseInt(content);
				} else {
					throw new FPGAParseException(
							"Missing comma (,) in .size directive. Line: " + lineCount + ": " + line);
				}
				((Variable) this.current).setSize(size);
				break;
			case ".zero":
				((Variable) this.current).itemCount++;
				break;
			case ".ascii":
				String s = getString(line);// getToken(line, 1);
				// s = s.substring(1, s.length() - 1);

				s = StringEscapeUtils.unescapeJava(s);

				((Variable) this.current).add(s.getBytes(StandardCharsets.ISO_8859_1));
				// ((Variable) this.current).itemCount = ((Variable)
				// this.current).content.length;
				break;
			case ".byte":
				content = getToken(line, 1);
				((Variable) this.current).content[((Variable) this.current).itemCount] = Byte.parseByte(content);
				((Variable) this.current).itemCount++;
				break;
			case ".short":
				content = getToken(line, 1);
				short cs = Short.parseShort(content);
				byte[] bytes = toShortBytes(cs);
				((Variable) this.current).content[((Variable) this.current).itemCount * 2] = bytes[0];
				((Variable) this.current).content[((Variable) this.current).itemCount * 2 + 1] = bytes[1];
				((Variable) this.current).itemCount++;
				break;
			case ".long":
				content = getToken(line, 1);
				Variable string = this.strings.get(fileName + content);
				if (string != null) {
					((Variable) this.current).itemCount++;
					((Variable) this.current).dataLabelName = string.name;
					this.current.dataLabel = string;
					break;
				}
				int ci = Integer.parseInt(content);
				bytes = toIntBytes(ci);
				((Variable) this.current).content[((Variable) this.current).itemCount * 4] = bytes[0];
				((Variable) this.current).content[((Variable) this.current).itemCount * 4 + 1] = bytes[1];
				((Variable) this.current).content[((Variable) this.current).itemCount * 4 + 2] = bytes[2];
				((Variable) this.current).content[((Variable) this.current).itemCount * 4 + 3] = bytes[3];
				((Variable) this.current).itemCount++;
				break;
			case ".string":
				this.current.stringContent = getToken(line, 1);
//System.out.println("################ " + this.current);				
				s = this.current.stringContent;
//System.out.println("################ " + s);				
				s = s.substring(1, s.length() - 1) + "\u0000";
				s = StringEscapeUtils.unescapeJava(s);
				((Variable) this.current).add(s.getBytes(StandardCharsets.ISO_8859_1)); // StandardCharsets.UTF_8)
				// ((Variable) this.current).itemCount += ((Variable)
				// this.current).content.length;
				break;
			}
			break;
		case STRING:
			firstToken = getToken(line, 0);
			firstToken = getToken(firstToken, 0);
			switch (firstToken) {
			case ".string":
				String s = line.substring(line.indexOf(getToken(line, 1)));
				s = s.substring(1, s.length() - 1) + "\u0000";
				s = StringEscapeUtils.unescapeJava(s);
				this.currentString.content = s.getBytes(StandardCharsets.ISO_8859_1);
				this.currentString.type = AssemblerObject.BYTE;
				this.currentString.size = this.currentString.content.length;
				this.state = START;
				break;
			}
			break;
		case FUNCTION:
			firstToken = getToken(line, 0);
			firstToken = getToken(firstToken, 0);
			switch (firstToken) {
			case ".equ":
				parseConst(line);
				return;
			case ".size":
				this.state = START;
			case ".p2align":
			case ".section":
				return;
			}
			if (!firstToken.endsWith(":")) {
				Instruction i = instructions.instructions.get(firstToken);
				if (i != null) {
					Function f = (Function) (this.current);
					i = (Instruction) i.clone();
					i.source = line;
					f.instructions.add(i);
					i.owner = f;
					i.lineCount = lineCount;
				} else {
					throw new FPGAParseException("Unknown instruction " + firstToken + " in file " + this.fileName
							+ ", line: " + lineCount + ": " + line);
				}
			} else {
				// Labels
				Label l = new Label(firstToken.substring(0, firstToken.length() - 1));
				Function f = (Function) (this.current);
				f.instructions.add(l);
//				if (l.name.equals("DIR_IS_FILE_OR_SUBDIR"))
//					System.out.println("SADF");
				this.labels.put(fileName + l.name, l);
				l.lineCount = lineCount;
			}
			break;
		}

	}

	private String getString(String line) {
		if (line.matches("(.+)(\\s+)\"(.*)\"")) {
			Pattern p = Pattern.compile("(.+)(\\s+)\"(.*)\"");
			Matcher m = p.matcher(line);
			m.find();
			return m.group(3);
		}
		return line;
	}

	private void parseConst(String line) {
		String name = getToken(line, 1).trim();
		if (name.endsWith(",")) {
			name = name.substring(0, name.length() - 1);
		}
		String value = getTokenBefore(getTokenAfter(line, ','), '#');
		int val;
		if (value.startsWith("0x")) {
			val = Integer.parseInt(value.substring(2), 16);
		} else {
			val = Integer.parseInt(value);
		}
		Constant c = new Constant(name, val);
		this.constants.put(name, c);
	}

	private int getType(Variable v) {
		if (v.dataLabelName != null)
			return AssemblerObject.INT; // pointer is 4 bytes long
		int i = v.size / v.itemCount;
		switch (i) {
		case 1:
			return AssemblerObject.BYTE;
		case 2:
			return AssemblerObject.SHORT;
		case 4:
			return AssemblerObject.INT;
		}
		return 0;
	}

	private byte[] toShortBytes(short s) {
		byte[] ret = new byte[2];
		ret[0] = (byte) ((s >> 8) & 0xFF);
		ret[1] = (byte) (s & 0xFF);
		return ret;
	}

	private byte[] toIntBytes(int ci) {
		byte[] ret = new byte[4];
		ret[0] = (byte) ((ci >> 24) & 0xFF);
		ret[1] = (byte) ((ci >> 16) & 0xFF);
		ret[2] = (byte) ((ci >> 8) & 0xFF);
		ret[3] = (byte) (ci & 0xFF);
		return ret;
	}

	private String getTokenBefore(String content, char toFind) {
		if (content.indexOf(toFind) == -1) {
			return content.trim();
		}
		return content.substring(0, content.indexOf(toFind)).trim();
	}

	private String getTokenAfter(String content, char toFind) {
		return content.substring(content.indexOf(toFind) + 1).trim();
	}

	private String getToken(String line, int i) {
		String[] tokens = line.split("\\s+");
		try {
			if (tokens.length > 0)
				return tokens[i].trim();
			else
				return line.trim();
		} catch (Exception ex) {
			return null;
		}
	}

	public void flattenLabels() {
		for (Function f : this.functions.values()) {
			List<Instruction> flatten = new ArrayList<Instruction>();
			for (int i = 0; i < f.instructions.size(); i++) {
				Instruction instr = f.instructions.get(i);
				if (instr instanceof Label) {
					// TODO: Label on the last instruction!
					Instruction next = f.instructions.get(i + 1);
					next.labelName = instr.name;
					next.label = (Label) instr;
					instr.assignedTo = next;
				} else {
					flatten.add(instr);
				}
			}
			f.instructions = flatten;
		}
	}

	public void prepare(int addr) throws FPGAParseException {
		Function main = this.functions.get("main");
		if (main != null) {
			if (addr == 0xB000 || addr == 39424) {
				// this is for the boot loader programs, being assembled to be loaded by the
				// boot loader
				// boot loader programs begin at 0xB000 and has the stack set to 500000.

				// add mov.w sp, 500000 instruction at the beginning of the main function
				main.instructions.add(0, getStackInstruction());
				// replace ret instruction with the halt instruction
				if (main.instructions.get(main.instructions.size() - 1).name.equals("ret")) {
					main.instructions.set(main.instructions.size() - 1, getHaltInstruction());
				}
			}
			for (Instruction i : main.instructions) {
				i.prepare(this, main);
				i.address = addr;
				try {
					addr += i.generate().length;
				} catch (Exception ex) {
				}
			}
			if (main.instructions.size() > 0) {
				main.address = main.instructions.get(0).address;
			}
		}
		for (Function f : this.functions.values()) {
			if (!f.name.equals("main")) {
				for (Instruction i : f.instructions) {
					i.prepare(this, f);
					i.address = addr;
					try {
						addr += i.generate().length;
					} catch (Exception ex) {
					}
				}
				if (f.instructions.size() > 0) {
					f.address = f.instructions.get(0).address;
				}
			}
		}

		for (Variable v : this.globalVars.values()) {
			v.address = addr;
			addr += align(v.size);
		}

		for (Variable s : this.strings.values()) {
			s.address += addr;
			addr += align(s.size);
		}
	}

	private Instruction getHaltInstruction() {
		Instruction i = new Instruction("halt") {
			@Override
			public void prepare(Parser parser, Function f) throws FPGAParseException {
			}

			@Override
			public byte[] generate() {
				return new byte[] { (byte) 0xff, (byte) 0xf0 };
			}
		};
		return i;
	}

	private Instruction getStackInstruction() {
		Instruction i = new Instruction("mov.w") {
			@Override
			public void prepare(Parser parser, Function f) throws FPGAParseException {
			}

			@Override
			public byte[] generate() {
				// mov.w sp, 500000
				return new byte[] { 0x0f, (byte) 0xb0, 0x00, 0x07, (byte) 0xa1, (byte) 0x20 };
			}
		};
		return i;
	}

	private int align(int size) {
		if (size % 2 == 0)
			return size;
		else
			return size + 1;
	}

	public void assemble() {
		for (Label l : this.labels.values()) {
			try {
				l.address = l.assignedTo.address;
			} catch (Exception ex) {
				System.out.printf("ERROR: l:%s, l.address: %s, l.assignedTo: %s\n", l, l.address, l.assignedTo);
				ex.printStackTrace();
			}
		}

		Function main = this.functions.get("main");
		if (main != null) {
			for (Instruction i : main.instructions) {
				if (i.operandAsLabel != null) {
					i.operandObj = findOperand(i.operandAsLabel, main);
					i.operandObj.referencedFrom.add(i);
					i.operand = i.operandObj.address + i.number;

					// if the current instruction points to a function,
					// put the reference to a function's label as well
					if (i.operandObj instanceof Function) {
						Function ff = (Function) i.operandObj;
						if (ff.instructions.get(0).label != null) {
							ff.instructions.get(0).label.referencedFrom.add(i);
						}
					}
				}
			}
		}
		for (Function f : this.functions.values()) {
			if (!f.name.equals("main")) {
				for (Instruction i : f.instructions) {
					if (i.operandAsLabel != null) {
						i.operandObj = findOperand(i.operandAsLabel, f);
						i.operandObj.referencedFrom.add(i);
						i.operand = i.operandObj.address + i.number;

						// if the current instruction points to a function,
						// put the reference to a function's label as well
						if (i.operandObj instanceof Function) {
							Function ff = (Function) i.operandObj;
							if (ff.instructions.get(0).label != null) {
								ff.instructions.get(0).label.referencedFrom.add(i);
							}
						}

					}
				}
			}
		}

		for (Variable v : this.globalVars.values()) {
			// p6: .long .LC0
			if (v.dataLabel != null) {
				// TODO: check this out
				v.content = toIntBytes(v.dataLabel.address);
				v.dataLabel.referencedFrom.add(v);
			}
		}
	}

	private AssemblerObject findOperand(String key, Function f) {
		if (this.globalVars.containsKey(key)) {
			return this.globalVars.get(key);
		}
		if (this.strings.containsKey(f.fileName + key)) {
			return this.strings.get(f.fileName + key);
		}
		if (this.functions.containsKey(key)) {
			return this.functions.get(key);
		}
		if (this.labels.containsKey(f.fileName + key)) {
			return this.labels.get(f.fileName + key);
		}
		if (this.constants.containsKey(key)) {
			return this.constants.get(key);
		}
		return null;
	}

	public void printText() throws FPGAParseException {
		Function main = this.functions.get("main");
		if (main != null) {
			System.out.println(main.name + ":");
			for (Instruction i : main.instructions) {
				byte[] code = i.generate();
				System.out.println(i.address + " (" + String.format("%08X", i.address) + ")" + ":"
						+ Hex.encodeHexString(code, false) + "\t-> " + ((i.labelName != null) ? i.labelName + ": " : "")
						+ i.source);
			}
		}
		for (Function f : this.functions.values()) {
			if (!f.name.equals(this.mainFileName + "main")) {
				System.out.println(f.name + ":" + "\t(referenced from=" + f.getReferencedFrom() + ")");
				for (Instruction i : f.instructions) {
					byte[] code = i.generate();
					System.out.println(i.address + " (" + String.format("%08X", i.address) + ")" + ":"
							+ Hex.encodeHexString(code, false) + "\t-> "
							+ ((i.labelName != null) ? i.labelName + ": " : "") + i.source);
				}
			}
		}

		for (Variable v : this.globalVars.values()) {
			System.out.println(v.toString());
		}

		for (Variable v : this.strings.values()) {
			System.out.println(v.toString());
		}

		for (Label l : this.labels.values()) {
			System.out.println(l.toString());
		}

		for (Constant c : this.constants.values()) {
			System.out.println(c.name + "=" + c.address);
		}
	}

	public void save(String executable, int fillOffset) throws IOException, FPGAParseException {
		FileOutputStream out = new FileOutputStream(executable);

		for (int i = 0; i < fillOffset; i++)
			out.write(0);

		Function main = this.functions.get("main");
		if (main != null) {
			for (Instruction i : main.instructions) {
				byte[] code = i.generate();
				out.write(code);
			}
		}
		for (Function f : this.functions.values()) {
			if (!f.name.equals("main")) {
				for (Instruction i : f.instructions) {
					byte[] code = i.generate();
					out.write(code);
				}
			}
		}

		for (Variable v : this.globalVars.values()) {
			out.write(v.content);
			for (int i = 0; i < (v.size % 2); i++)
				out.write(0);
		}

		for (Variable v : this.strings.values()) {
			out.write(v.content);
			for (int i = 0; i < (v.size % 2); i++)
				out.write(0);
		}
		out.close();
	}

	public void saveSymFile(String symFile) throws IOException {
		PrintWriter out = new PrintWriter(new FileWriter(symFile));

		Function main = this.functions.get("main");
		if (main != null)
			out.printf("main = 0x%08X\n", main.address);

		for (Function f : this.functions.values()) {
			if (!f.name.equals("main")) {
				out.printf("%s = 0x%08X\n", f.name, f.address);
			}
		}

		for (Variable v : this.globalVars.values()) {
			out.printf("%s = 0x%08X\n", v.name, v.address);
		}

		for (Variable v : this.strings.values()) {
			out.printf("%s = 0x%08X\n", /* v.fileName + */ v.name, v.address);
		}

		for (Label l : this.labels.values()) {
			Instruction assignedTo = l.assignedTo;
			while (assignedTo instanceof Label) {
				assignedTo = ((Label) (assignedTo)).assignedTo;
			}
			try {
				out.printf("%s = 0x%08X\n", /* ((Function)(assignedTo.owner)).fileName + */ l.name, l.address);
			} catch (Exception ex) {
				System.out.printf("assignedTo: %s, l: %s\n", assignedTo, l);
			}
		}

		out.close();
	}

}
