package core;

import java.util.HashSet;
import java.util.Iterator;

/**
 * A {@link Term} is a set of {@link Literal} elements. Order of {@link Literal}
 * does not matter. A term can represent a minterm or an implicant, prime
 * implicant, essential prime implicant, or a term under construction.
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
	
	public Term(Term term) {
		super();
		super.addAll(term);
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
	 * @return {@code true} if this term covers another term, 
	 * 	else {@code false}. An empty term covers nothing.
	 */
	public boolean covers(Term term) {
		if (super.isEmpty()) return false;
		Iterator<Literal> i = super.iterator();
		while (i.hasNext())
			if (!term.contains(i.next())) return false;
		return true;
	}
	
	/**
	 * 
	 * @return {@code true} if this term intersects another term, 
	 * 	else {@code false}. An empty term intersects nothing.
	 */
	public boolean intersects(Term term) {
		return covers(term);
	}
	
	/**
	 * 
	 * @param termList a data set.
	 * @return {@code true} if this term intersects with the data set, 
	 * 	else {@code false}. An empty term intersects nothing.
	 */
	public boolean intersects(TermList termList) {
		boolean intersects = false;
		for (Term term : termList) {
			if (this.covers(term)) {
				intersects = true;
				break;
			}
		}
		return intersects;
	}

}
