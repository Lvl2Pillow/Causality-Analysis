package core;

/**
 * A variable has two forms: normal (1) and compliment (0), also know as literals.
 * 
 * @author lvl2pillow
 * 
 */
public class Literal {
	public final int index; 			// unique variable index
	
	private final String name;			// variable name
	private final boolean isNormal;		// normal or compliment form
	
	/**
	 * 
	 * @param index unique variable index.
	 * @param isNormal whether literal is normal or compliment form.
	 */
	public Literal(int index, boolean isNormal) {
		this.index = index;
		this.name = "";
		this.isNormal = isNormal;
	}
	
	/**
	 * 
	 * @param index unique variable index.
	 * @param name variable name.
	 * @param isNormal whether literal is normal or compliment form.
	 */
	public Literal(int index, String name, boolean isNormal) {
		this.index = index;
		this.name = name;
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

	public String toString() { return isNormal ? name : "~"+name; }
	public String name() { return name; }
	public String getName() { return name(); }
	public int index() { return index; }
	public int getIndex() { return index(); }
	public boolean isNormal() { return isNormal ? isNormal : !isNormal; }
	public boolean isCompliment() {return !isNormal(); }

}
