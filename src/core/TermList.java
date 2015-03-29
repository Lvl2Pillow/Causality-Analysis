package core;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * A {@link ArrayList} of {@link Term} elements, representing a data set.
 * 
 * @author lvl2pillow
 *
 */
public class TermList extends ArrayList<Term> {
	// auto-generated serialVersionUID
	private static final long serialVersionUID = -4397936737489018745L;
	
	public TermList() {
		super();
	}
	
	public TermList(Collection<Term> termList) {
		super(termList);
	}
	
	/**
	 * Removes all terms covered by a given term.
	 * 
	 * @param coveringTerm
	 * @return resulting list of terms.
	 */
	public TermList removeTermsCoveredBy(Term coveringTerm) {
		Iterator<Term> i = super.iterator();
		while (i.hasNext()) {
			if (coveringTerm.covers(i.next()))
				i.remove();
		}
		return this;
	}
	
	/**
	 * Returns all terms that are not covered by a given term.
	 * 
	 * @param coveringTerm
	 * @return list of uncovered terms.
	 */
	public TermList getUncoveredBy(Term coveringTerm) {
		TermList uncoveredTerms = new TermList();
		for (Term term : this) {
			if (!coveringTerm.covers(term))
				uncoveredTerms.add(term);
		}	
		return uncoveredTerms;
	}
	
}
