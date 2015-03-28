package core;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * A {@link LinkedList} of {@link Term} elements, representing a data set.
 * 
 * @author lvl2pillow
 *
 */
public class TermList extends LinkedList<Term> {
	// auto-generated serialVersionUID
	private static final long serialVersionUID = -4397936737489018745L;
	
	public TermList() {
		super();
	}
	
	public TermList(TermList termList) {
		super();
		super.addAll(termList);
	}
	
	/**
	 * Removes all terms covered by a given term.
	 * 
	 * @param term
	 * @return resulting list of terms.
	 */
	public TermList removeTermsCoveredBy(Term term) {
		Iterator<Term> i = super.iterator();
		while (i.hasNext()) {
			if (term.covers(i.next()))
				i.remove();
		}
		return this;
	}
	
	/**
	 * Returns all terms that are not covered by a given term.
	 * 
	 * @param term
	 * @return list of uncovered terms.
	 */
	public TermList getUncoveredBy(Term term) {
		TermList uncoveredTerms = new TermList();
		for (Term minterm : this) {
			if (!term.covers(minterm))
				uncoveredTerms.add(minterm);
		}	
		return uncoveredTerms;
	}
	
}
