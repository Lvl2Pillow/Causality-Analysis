package core;

/**
 * A variable has two forms: normal (1) and compliment (0), also know as literals.
 * 
 * @author lvl2pillow
 * 
 */
public class Literal {
	private static int nLiterals = 0;	// static count of number of {@link Literal} instances
	
	public final int index; 			// unique variable index
	
	private final String name;			// variable name
	private final boolean normal;		// normal or compliment
	
	/**
	 * 
	 * @param normal whether literal is normal or compliment form.
	 */
	public Literal(boolean normal) {
		this.index = ++nLiterals;
		this.name = Integer.toString(index);
		this.normal = normal;
	}
	
	/**
	 * 
	 * @param name variable name.
	 * @param normal whether literal is normal or compliment form.
	 */
	public Literal(String name, boolean normal) {
		this.index = ++nLiterals;
		this.name = name;
		this.normal = normal;
	}
	
	// auto-generated
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + (normal ? 1231 : 1237);
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
		if (normal != other.normal)
			return false;
		return true;
	}

	public String toString() { return normal ? name : "~"+name; }
	public String name() { return name; }
	public String getName() { return name(); }
	public int index() { return index; }
	public int getIndex() { return index(); }
	public boolean normal() { return normal; }
	public boolean getNormal() { return normal; }
	public boolean isNormal() { return normal ? normal : !normal; }
	public boolean isCompliment() {return !isNormal(); }

}
