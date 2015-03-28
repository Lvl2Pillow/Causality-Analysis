package core;

/**
 * A variable has two forms, normal (1) and compliment (0), also know as literals.
 * 
 * @author lvl2pillow
 * 
 */
public class Literal {
	private final String variableName;	// variable name
	private final int variableIndex; 	// variable index
	private final boolean normal;		// normal or compliment
	
	public Literal(int variableIndex, boolean normal) {
		this.variableName = Integer.toString(variableIndex);
		this.variableIndex = variableIndex;
		this.normal = normal;
	}
	
	public Literal(String variableName, int variableIndex, boolean normal) {
		this.variableName = variableName;
		this.variableIndex = variableIndex;
		this.normal = normal;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Literal)) return false;
		return (((Literal) o).variableIndex == variableIndex 
				&& ((Literal) o).normal == normal) ? true : false;
	}
	
	@Override
	public int hashCode() {
		return normal ? variableIndex : -1*variableIndex;
	}
	
	public String toString() { return normal ? variableName : "~"+variableName; }
	public String name() { return variableName; }
	public String variableName() { return variableName; }
	public int index() { return variableIndex; }
	public int variableIndex() { return variableIndex; }
	public boolean normal() { return normal; }
	public boolean isNormal() { return normal ? normal : !normal; }
	public boolean isCompliment() {return !isNormal(); }

}
