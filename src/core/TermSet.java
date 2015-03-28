package core;

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
	
	public TermSet(TermSet termSet) {
		super();
		super.addAll(termSet);
	}

	/**
	 * Whether any of these terms intersects with the data set.
	 * 
	 * @param termList the data set.
	 * @return
	 */
	public boolean intersects(TermList termList) {
		boolean intersects = false;
		for (Term term : this) {
			if (term.intersects(termList)) {
				intersects = true;
				break;
			}
		}
		return intersects;
	}
}
