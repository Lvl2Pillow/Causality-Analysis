package core;

/**
 * A variable from the data set.
 * 
 * @author lvl2pillow
 *
 */
public class Variable {
	protected final int index; 			// variable index
	protected final String name;		// variable name
	
	public Variable(int index) {
		this.index = index;
		this.name = Integer.toString(index);
	}
	
	public Variable(int index, String name) {
		this.index = index;
		this.name = name;
	}
	
	// auto-generated
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		return result;
	}

	// auto-generated
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Variable other = (Variable) obj;
		if (index != other.index)
			return false;
		return true;
	}

	@Override
	public String toString() { return name; }
	
	public String getName() { return name; }
	public int getIndex() { return index; }
}
