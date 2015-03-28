package core;

import java.util.Collection;
import java.util.HashSet;

/**
 * A {@link HashSet} of {@link Term} elements, representing an implicant,
 * prime implicant, or essential prime implicant.
 * 
 * @author lvl2pillow
 *
 */
public class TermSet extends HashSet<Term> {
	// auto-generated serialVersionUID
	private static final long serialVersionUID = -522610864782742419L;
	
	public TermSet() {
		super();
	}
	
	public TermSet(Collection<Term> termSet) {
		super(termSet);
	}

	/**
	 * Whether any of these terms intersects with the data set.
	 * 
	 * @param termList the data set.
	 * @return
	 */
	public boolean intersects(MintermList dataSet) {
		boolean intersects = false;
		for (Term term : this) {
			if (term.intersects(dataSet)) {
				intersects = true;
				break;
			}
		}
		return intersects;
	}
	
	public boolean covers(MintermList dataSet) {
		// TODO
		return false;
	}
}
