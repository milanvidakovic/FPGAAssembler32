package fpga.parser.instructions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fpga.parser.FPGAParseException;
import fpga.parser.Parser;
import fpga.parser.objects.AssemblerObject;
import fpga.parser.objects.Function;

public abstract class Instruction extends AssemblerObject implements Cloneable {
	
	public static final int SRC_REG  = 1;
	public static final int DEST_REG = 2;

	public int group;
	public int subgroup;
	
	public int srcReg;
	public int destReg;
	
	/** If this instruction has an operand in form two or four bytes, that operand goes here. */ 
	public int operand;
	/** Name of the label that is an operand. */
	public String operandAsLabel;
	public AssemblerObject operandObj;
	/** In case of st.w [label+number], r0 */
	public int number;
	
	public String source;

	public String labelName;
	public Label label;
	
	public Function owner;
	
	public Instruction(String name) {
		this.name = name;
	}
	
	public abstract byte[] generate() throws FPGAParseException;
	public abstract void prepare(Parser parser, Function f) throws FPGAParseException;
	
	@Override 
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	private int regToNumber(String reg) {
		switch (reg) {
		case "r0" : return 0;
		case "r1" : return 1;
		case "r2" : return 2;
		case "r3" : return 3;
		case "r4" : return 4;
		case "r5" : return 5;
		case "r6" : return 6;
		case "r7" : return 7;
		case "r8" : return 8;
		case "r9" : return 9;
		case "r10" : return 10;
		case "r11" : return 11;
		case "r12" : return 12;
		case "r13" : return 13;
		case "r14" : return 14;
		case "sp" : return 15;
		case "h" : return 14;
		}
		return 0;
	}
	
	public byte[] toIntBytes(int ci) {
		byte[] ret = new byte[4];
		ret[0] = (byte) ((ci >> 24) & 0xFF);
		ret[1] = (byte) ((ci >> 16) & 0xFF);
		ret[2] = (byte) ((ci >> 8) & 0xFF);
		ret[3] = (byte) (ci & 0xFF);
		return ret;
	}

	public byte[] toShortBytes(int ci) {
		byte[] ret = new byte[2];
		ret[0] = (byte) ((ci >> 8) & 0xFF);
		ret[1] = (byte) (ci & 0xFF);
		return ret;
	}

	public boolean parseRegReg(int whichReg) {
		// mov.w r0, r2
		if (this.source.matches("\\s*([a-z]+.?.?)\\s+(r\\d\\d?|sp|h),\\s*(r\\d\\d?|sp|h)((\\s+.*)|$)")) {
			Pattern p = Pattern.compile("\\s*([a-z]+.?.?)\\s+(r\\d\\d?|sp|h),\\s*(r\\d\\d?|sp|h)((\\s+.*)|$)");
			Matcher m = p.matcher(this.source);
			if (m.find()) {
				String reg;
				if (whichReg == SRC_REG) {
					reg = m.group(3);
					this.srcReg = regToNumber(reg);
				} else {
					reg = m.group(2);
					this.destReg = regToNumber(reg);
				}
				return true;
			} else 
				return false;
		} 
		return false;
	}


	public boolean parseRegMemReg(int whichReg) {
		// ld.w r0, [r2]
		if (this.source.matches("\\s*([a-z]+.?.?)\\s+(r\\d\\d?|sp|h),\\s*\\[(r\\d\\d?|sp|h)\\]\\s*(.*|$)")) {
			Pattern p = Pattern.compile("\\s*([a-z]+\\.?.*)\\s+(r\\d\\d?|sp|h),\\s*\\[(r\\d\\d?|sp|h)\\]\\s*(.*|$)");
			Matcher m = p.matcher(this.source);
			if (m.find()) {
				String reg;
				if (whichReg == SRC_REG) {
					reg = m.group(3);
					this.srcReg = regToNumber(reg);
				} else {
					reg = m.group(2);
					this.destReg = regToNumber(reg);
				}
				return true;
			} else 
				return false;
		} 
		return false;
	}

	public boolean parseReg(int whichReg) {
		// push r0
		if (this.source.matches("\\s*([a-z]+.?.?)\\s+(r\\d\\d?|sp|h)((\\s*.*)|$)")) {
			Pattern p = Pattern.compile("\\s*([a-z]+.?.?)\\s+(r\\d\\d?|sp|h)((\\s*.*)|$)");
			Matcher m = p.matcher(this.source);
			if (m.find()) {
				String reg = m.group(2);
				if (whichReg == SRC_REG) {
					this.srcReg = regToNumber(reg);
				} else {
					this.destReg = regToNumber(reg);
				}
				return true;
			} else 
				return false;
		} 
		return false;
	}

	public boolean parseRegNumber(int whichReg) {
		// mov.w r0, 32
		if (this.source.matches("\\s*([a-z]+.?.?)\\s+(r\\d\\d?|sp|h),\\s+(-\\d+|\\d+)\\s*(.*|$)")) {
			Pattern p = Pattern.compile("\\s*([a-z]+.?.?)\\s+(r\\d\\d?|sp|h),\\s+(-\\d+|\\d+)\\s*(.*|$)");
			Matcher m = p.matcher(this.source);
			if (m.find()) {
				String reg = m.group(2);
				if (whichReg == SRC_REG) {
					this.srcReg = regToNumber(reg);
				} else {
					this.destReg = regToNumber(reg);
				}
				String num = m.group(3);
				this.operand = (int) Long.parseLong(num);
				return true;
			} else 
				return false;
		} 
		return false;
	}

	public boolean parseRegMemNumber(int whichReg) {
		// ld.w r0, [32]
		if (this.source.matches("\\s*([a-z]+.?.?)\\s+(r\\d\\d?|sp|h),\\s+\\[(-\\d+|\\d+)\\]\\s*(.*|$)")) {
			Pattern p = Pattern.compile("\\s*([a-z]+.?.?)\\s+(r\\d\\d?|sp|h),\\s+\\[(-\\d+|\\d+)\\]\\s*(.*|$)");
			Matcher m = p.matcher(this.source);
			if (m.find()) {
				String reg = m.group(2);
				if (whichReg == SRC_REG) {
					this.srcReg = regToNumber(reg);
				} else {
					this.destReg = regToNumber(reg);
				}
				String num = m.group(3);
				this.operand = (int) Long.parseLong(num);
				return true;
			} else 
				return false;
		} 
		return false;
	}

	public boolean parseRegMemRegNumber(int whichReg) {
		// ld.w r0, [r1 + (32)]
		if (this.source.matches("\\s*([a-z]+.?.?)\\s+(r\\d\\d?|sp|h),\\s+\\[(r\\d\\d?|sp|h)\\s(\\+|-)\\s\\((-\\d+|\\d+)\\)\\]\\s*(.*|$)")) {
			Pattern p = Pattern.compile("\\s*([a-z]+.?.?)\\s+(r\\d\\d?|sp|h),\\s+\\[(r\\d\\d?|sp|h)\\s(\\+|-)\\s\\((-\\d+|\\d+)\\)\\]\\s*(.*|$)");
			Matcher m = p.matcher(this.source);
			if (m.find()) {
				String reg; 
				if (whichReg == SRC_REG) {
					reg = m.group(3);
					this.srcReg = regToNumber(reg);
				} else {
					reg = m.group(2);
					this.destReg = regToNumber(reg);
				}
				String num = m.group(5);
				this.operand = (int) Long.parseLong(num);
				return true;
			} else 
				return false;
		} 
		return false;
	}

	public boolean parseRegMemRegLabel(Parser parser, int whichReg) {
		// ld.w r0, [r1 + (_file_write_buffer)]
		if (this.source.matches("\\s*([a-z]+.?.?)\\s+(r\\d\\d?|sp|h),\\s+\\[(r\\d\\d?|sp|h)\\s(\\+|-)\\s\\((\\S+)\\)\\]\\s*(.*|$)")) {
			Pattern p = Pattern.compile("\\s*([a-z]+.?.?)\\s+(r\\d\\d?|sp|h),\\s+\\[(r\\d\\d?|sp|h)\\s(\\+|-)\\s\\((\\S+)\\)\\]\\s*(.*|$)");
			Matcher m = p.matcher(this.source);
			if (m.find()) {
				String reg; 
				if (whichReg == SRC_REG) {
					reg = m.group(3);
					this.srcReg = regToNumber(reg);
				} else {
					reg = m.group(2);
					this.destReg = regToNumber(reg);
				}
				String lbl = m.group(5);
				this.operandAsLabel = lbl;
				
				if (!parser.constants.containsKey(lbl)) {
					return false;
				}
				return true;
			} else 
				return false;
		} 
		return false;
	}

	public boolean parseNumber() {
		// push  32
		if (this.source.matches("\\s*([a-z]+.?.?)\\s+(-\\d+|\\d+)\\s*(.*|$)")) {
			Pattern p = Pattern.compile("\\s*([a-z]+.?.?)\\s+(-\\d+|\\d+)\\s*(.*|$)");
			Matcher m = p.matcher(this.source);
			if (m.find()) {
				String num = m.group(2);
				this.operand = (int) Long.parseLong(num);
				return true;
			} else 
				return false;
		} 
		return false;
	}

	public boolean parseRegLabel(Parser parser, int whichReg, Function f) throws FPGAParseException {
		// mov.w r3, .LC23
		if (this.source.matches("\\s*([a-z]+.?.?)\\s+(r\\d\\d?|sp|h),\\s+(\\S+)\\s*(.*|$)")) {
			Pattern p = Pattern.compile("\\s*([a-z]+.?.?)\\s+(r\\d\\d?|sp|h),\\s+(\\S+)\\s*(.*|$)");
			Matcher m = p.matcher(this.source);
			if (m.find()) {
				String reg = m.group(2);
				if (whichReg == SRC_REG) {
					this.srcReg = regToNumber(reg);
				} else {
					this.destReg = regToNumber(reg);
				}
				String lbl = m.group(3);
				
				Pattern p2 = Pattern.compile("(\\S+)(\\+|-)(\\d+)");
				Matcher m2 = p2.matcher(lbl);
				if (m2.find()) {
					lbl = m2.group(1);
					this.number = Integer.parseInt(m2.group(2) + m2.group(3)); 
				}
				
				this.operandAsLabel = lbl;
				
				if (isForbidden(lbl)) {
					throw new FPGAParseException("This label name is forbidden (sp, h, r0, r1, ..., r15). Line: " + source);
				}
				
				if (!parser.globalVars.containsKey(lbl)) {
					if (!parser.strings.containsKey(f.fileName+lbl)) {
						if (!parser.functions.containsKey(lbl)) {
							if (!parser.labels.containsKey(f.fileName + lbl)) {
								if (!parser.constants.containsKey(lbl)) {
									return false;
								}
							}
						}
					}
				}
				
				return true;
			} else 
				return false;
		} 
		return false;
	}

	public boolean parseRegMemLabel(Parser parser, int whichReg, Function f) throws FPGAParseException {
		// ld.w r3, [.LC23]
		if (this.source.matches("\\s*([a-z]+.?.?)\\s+(r\\d\\d?|sp|h),\\s+\\[(\\S+)\\]\\s*(.*|$)")) {
			Pattern p = Pattern.compile("\\s*([a-z]+.?.?)\\s+(r\\d\\d?|sp|h),\\s+\\[(\\S+)\\]\\s*(.*|$)");
			Matcher m = p.matcher(this.source);
			if (m.find()) {
				String reg = m.group(2);
				if (whichReg == SRC_REG) {
					this.srcReg = regToNumber(reg);
				} else {
					this.destReg = regToNumber(reg);
				}
				String lbl = m.group(3);
				
				Pattern p2 = Pattern.compile("(\\S+)(\\+|-)(\\d+)");
				Matcher m2 = p2.matcher(lbl);
				if (m2.find()) {
					lbl = m2.group(1);
					this.number = Integer.parseInt(m2.group(2) + m2.group(3)); 
				}

				this.operandAsLabel = lbl;
				
				if (isForbidden(lbl)) {
					throw new FPGAParseException("This label name is forbidden (sp, h, r0, r1, ..., r15). Line: " + source);
				}
				
				if (!parser.globalVars.containsKey(lbl)) {
					if (!parser.strings.containsKey(f.fileName+lbl)) {
						if (!parser.functions.containsKey(lbl)) {
							if (!parser.labels.containsKey(f.fileName + lbl)) {
								if (!parser.constants.containsKey(lbl)) {
									return false;
								}
							}
						}
					}
				}

				
				return true;
			} else 
				return false;
		} 
		return false;
	}

	public boolean parseLabel(Parser parser, Function f) throws FPGAParseException {
		// push .LC12
		if (this.source.matches("\\s*([a-z]+.?.?)\\s+(\\S+)\\s*(.*|$)")) {
			Pattern p = Pattern.compile("\\s*([a-z]+.?.?)\\s+(\\S+)\\s*(.*|$)");
			Matcher m = p.matcher(this.source);
			if (m.find()) {
				String lbl = m.group(2);
				this.operandAsLabel = lbl;
				
				if (isForbidden(lbl)) {
					throw new FPGAParseException("This label name is forbidden (sp, h, r0, r1, ..., r15). Line: " + source);
				}
			
				if (!parser.globalVars.containsKey(lbl)) {
					if (!parser.strings.containsKey(f.fileName+lbl)) {
						if (!parser.functions.containsKey(lbl)) {
							if (!parser.labels.containsKey(f.fileName + lbl)) {
								if (!parser.constants.containsKey(lbl)) {
									return false;
								}
							}
						}
					}
				}
				
				return true;
			} else 
				return false;
		} 
		return false;
	}

	public boolean parseMemRegReg(int whichReg) {
		// st.w [r2], r0
		if (this.source.matches("\\s*([a-z]+.?.?)\\s+\\[(r\\d\\d?|sp|h)\\],\\s*(r\\d\\d?|sp|h)((\\s+.*)|$)")) {
			Pattern p = Pattern.compile("\\s*([a-z]+.?.?)\\s+\\[(r\\d\\d?|sp|h)\\],\\s*(r\\d\\d?|sp|h)((\\s+.*)|$)");
			Matcher m = p.matcher(this.source);
			if (m.find()) {
				String reg;
				if (whichReg == SRC_REG) {
					reg = m.group(3);
					this.srcReg = regToNumber(reg);
				} else {
					reg = m.group(2);
					this.destReg = regToNumber(reg);
				}
				return true;
			} else 
				return false;
		} 
		return false;
	}

	public boolean parseMemNumberReg(int whichReg) {
		// st.w [32], r0
		if (this.source.matches("\\s*([a-z]+.?.?)\\s+\\[(-\\d+|\\d+)\\],\\s+(r\\d\\d?|sp|h)((\\s+.*)|$)")) {
			Pattern p = Pattern.compile("\\s*([a-z]+.?.?)\\s+\\[(-\\d+|\\d+)\\],\\s+(r\\d\\d?|sp|h)((\\s+.*)|$)");
			Matcher m = p.matcher(this.source);
			if (m.find()) {
				String reg = m.group(3);
				if (whichReg == SRC_REG) {
					this.srcReg = regToNumber(reg);
				} else {
					this.destReg = regToNumber(reg);
				}
				String num = m.group(2);
				this.operand = (int) Long.parseLong(num);
				return true;
			} else 
				return false;
		} 
		return false;
	}

	public boolean parseMemLabelReg(Parser parser, int whichReg, Function f) throws FPGAParseException {
		// st.w [.LC23], r3
		if (this.source.matches("\\s*([a-z]+.?.?)\\s+\\[(\\S+)\\],\\s+(r\\d\\d?|sp|h)((\\s+.*)|$)")) {
			Pattern p = Pattern.compile("\\s*([a-z]+.?.?)\\s+\\[(\\S+)\\],\\s+(r\\d\\d?|sp|h)((\\s+.*)|$)");
			Matcher m = p.matcher(this.source);
			if (m.find()) {
				String reg = m.group(3);
				if (whichReg == SRC_REG) {
					this.srcReg = regToNumber(reg);
				} else {
					this.destReg = regToNumber(reg);
				}
				String lbl = m.group(2);
				
				Pattern p2 = Pattern.compile("(\\S+)(\\+|-)(\\d+)");
				Matcher m2 = p2.matcher(lbl);
				if (m2.find()) {
					lbl = m2.group(1);
					this.number = Integer.parseInt(m2.group(2) + m2.group(3)); 
				}
				
				this.operandAsLabel = lbl;
				
				if (isForbidden(lbl)) {
					throw new FPGAParseException("This label name is forbidden (sp, h, r0, r1, ..., r15). Line: " + source);
				}
				
				if (!parser.globalVars.containsKey(lbl)) {
					if (!parser.strings.containsKey(f.fileName+lbl)) {
						if (!parser.functions.containsKey(lbl)) {
							if (!parser.labels.containsKey(f.fileName + lbl)) {
								if (!parser.constants.containsKey(lbl)) {
									return false;
								}
							}
						}
					}
				}
				
				return true;
			} else 
				return false;
		} 
		return false;
	}

	private boolean isForbidden(String lbl) {
		switch (lbl) {
		case "h":
		case "sp":
			return true;
		}
		if (lbl.startsWith("r") && lbl.length() == 2) {
			if (Character.isDigit(lbl.charAt(1))) {
				return true;
			}
		} 
		if (lbl.startsWith("r") && lbl.length() == 3) {
			if (Character.isDigit(lbl.charAt(1)) && Character.isDigit(lbl.charAt(2))) {
				return true;
			}
		} 
		return false;
	}

	public boolean parseMemRegNumberReg(int whichReg) {
		// st.w [r1 + (32)], r0
		if (this.source.matches("\\s*([a-z]+.?.?)\\s+\\[(r\\d\\d?|sp|h)\\s(\\+|-)\\s\\((-\\d+|\\d+)\\)\\],\\s+(r\\d\\d?|sp|h)((\\s+.*)|$)")) {
			Pattern p = Pattern.compile("\\s*([a-z]+.?.?)\\s+\\[(r\\d\\d?|sp|h)\\s(\\+|-)\\s\\((-\\d+|\\d+)\\)\\],\\s+(r\\d\\d?|sp|h)((\\s+.*)|$)");
			Matcher m = p.matcher(this.source);
			if (m.find()) {
				String reg; 
				if (whichReg == SRC_REG) {
					reg = m.group(5);
					this.srcReg = regToNumber(reg);
				} else {
					reg = m.group(2);
					this.destReg = regToNumber(reg);
				}
				String num = m.group(4);
				this.operand = (int) Long.parseLong(num);
				return true;
			} else 
				return false;
		} 
		return false;
	}

	@Override
	public String toString() {
		return "Instruction " + ((labelName != null) ? ("[label=" + labelName + ", name=" + name + "]") : ("[name=" + name + "]"));
	}

}
