package core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A {@code Term} is a list of {@link Literal} elements. Order of elements
 * is preserved. Duplicate elements are not allowed. A {@code Term} may
 * represent a minterm, an implicant, or a term under construction.
 * 
 * @author lvl2pillow
 *
 */
public class Term extends ArrayList<Literal> {
	// auto-generated serialVersionUID
	private static final long serialVersionUID = 7727278808609346835L;
	
	public Term() {
		super();
	}
	
	public Term(Collection<Literal> term) {
		super(term);
	}
	
	@Override
	public boolean add(Literal newLiteral) {
		// can not add duplicate literal
		if (this.contains(newLiteral)) return false;
		// can not add literal if alternate form already exists
		if (newLiteral.isNormal() && 
				this.contains(new Literal(newLiteral.getIndex(), false)))
			return false;
		else if (newLiteral.isCompliment() && 
				this.contains(new Literal(newLiteral.getIndex(), true)))
			return false;
		return super.add(newLiteral);
	}
	
	/**
	 * 
	 * @return {@code true} if this term covers another term, 
	 * 	else {@code false}. An empty term covers nothing.
	 */
	public boolean covers(Term coveredTerm) {
		if (super.isEmpty()) return false;
		Iterator<Literal> i = super.iterator();
		while (i.hasNext())
			if (!coveredTerm.contains(i.next())) return false;
		return true;
	}
	
	/**
	 * 
	 * @return {@code true} if this term intersects another term, 
	 * 	else {@code false}. An empty term intersects nothing.
	 */
	public boolean intersects(Term coveredTerm) {
		return covers(coveredTerm);
	}
	
	/**
	 * 
	 * @param dataSet
	 * @return {@code true} if this term intersects with the data set, 
	 * 	else {@code false}. An empty term intersects nothing.
	 */
	public boolean intersects(TermList dataSet) {
		boolean intersects = false;
		for (Term minterm : dataSet) {
			if (this.covers(minterm)) {
				intersects = true;
				break;
			}
		}
		return intersects;
	}

}
