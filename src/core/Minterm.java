package core;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A {@link Minterm} is a list of {@link Literal} elements. Order of elements
 * is preserved. Duplicate elements are not allowed.
 * 
 * @author lvl2pillow
 *
 */
public class Minterm extends ArrayList<Literal> {
	// auto-generated serialVersionUID
	private static final long serialVersionUID = 7727278808609346835L;
	
	public Minterm() {
		super();
	}
	
	public Minterm(Collection<Literal> minterm) {
		super(minterm);
	}
	
	@Override
	public boolean add(Literal literal) {
		// can not add duplicate literal
		if (this.contains(literal)) return false;
		// can not add literal if compliment form already exists
		if (this.contains(new Literal(literal.index(), !literal.normal()))) return false;
		return super.add(literal);
	}

}
