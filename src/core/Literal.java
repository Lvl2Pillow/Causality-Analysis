package core;

/**
 * A variable has two forms: normal (1) and compliment (0), also know as literals.
 * 
 * @author lvl2pillow
 * 
 */
public class Literal extends Variable {
	private final boolean isNormal;		// normal or compliment form
	
	/**
	 * 
	 * @param index variable index.
	 * @param isNormal whether this literal is normal or compliment form.
	 */
	public Literal(int index, boolean isNormal) {
		super(index);
		this.isNormal = isNormal;
	}
	
	/**
	 * 
	 * @param index variable index.
	 * @param name variable name.
	 * @param isNormal whether this literal is normal or compliment form.
	 */
	public Literal(int index, String name, boolean isNormal) {
		super(index, name);
		this.isNormal = isNormal;
	}
	
	// auto-generated
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + (isNormal ? 1231 : 1237);
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
		Literal other = (Literal) obj;
		if (index != other.index)
			return false;
		if (isNormal != other.isNormal)
			return false;
		return true;
	}
	
	@Override
	public String toString() { return isNormal ? name : "~"+name; }
	
	public boolean isNormal() { return isNormal ? isNormal : !isNormal; }
	public boolean isCompliment() {return !isNormal(); }

}
