package core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A {@link Term} is a set of {@link Literal} elements. Order of elements
 * does not matter. A term can represent an implicant, prime implicant, 
 * essential prime implicant, or a term under construction.
 * 
 * @author lvl2pillow
 *
 */
public class Term extends HashSet<Literal> {
	// auto-generated serialVersionUID
	private static final long serialVersionUID = -5774491305692434616L;
	
	public Term() {
		super();
	}
	
	public Term(Collection<Literal> term) {
		super(term);
	}
	
	@Override
	public boolean add(Literal literal) {
		// can not add literal if compliment form already exists
		if (this.contains(new Literal(literal.index(), !literal.normal())))
			return false;
		return super.add(literal);
	}
	
	/**
	 * 
	 * @return {@code true} if this term covers a minterm, 
	 * 	else {@code false}. An empty term covers nothing.
	 */
	public boolean covers(Minterm minterm) {
		if (super.isEmpty()) return false;
		Iterator<Literal> i = super.iterator();
		while (i.hasNext())
			if (!minterm.contains(i.next())) return false;
		return true;
	}
	
	/**
	 * 
	 * @return {@code true} if this term intersects a minterm, 
	 * 	else {@code false}. An empty term intersects nothing.
	 */
	public boolean intersects(Minterm minterm) {
		return covers(minterm);
	}
	
	/**
	 * 
	 * @param dataSet
	 * @return {@code true} if this term intersects with the data set, 
	 * 	else {@code false}. An empty term intersects nothing.
	 */
	public boolean intersects(MintermList dataSet) {
		boolean intersects = false;
		for (Minterm minterm : dataSet) {
			if (this.covers(minterm)) {
				intersects = true;
				break;
			}
		}
		return intersects;
	}

}
