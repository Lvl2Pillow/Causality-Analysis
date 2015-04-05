package core;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * An {@link ArrayList} of {@link Term} elements, representing a data set.
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
	 * Removes all minterms covered by a specified term.
	 * 
	 * @param coveringTerm
	 * @return
	 */
	public boolean removeTermsCoveredBy(Term coveringTerm) {
		Iterator<Term> i = super.iterator();
		while (i.hasNext()) {
			if (coveringTerm.covers(i.next()))
				i.remove();
		}
		return true;
	}
	
	/**
	 * Returns a data set with all minterms that are not covered by a specified term. 
	 * </br>
	 * <b>Note:</b> This method will never return an empty data set. If the
	 * resulting data set is empty, this method will return the unmodified
	 * data set instead.
	 * 
	 * 
	 * @param coveringTerm
	 * @return data set with uncovered minterms.
	 */
	public TermList termsUncoveredBy(Term coveringTerm) {
		TermList uncoveredTerms = new TermList();
		for (Term minterm : this) {
			if (!coveringTerm.covers(minterm))
				uncoveredTerms.add(minterm);
		}	
		return uncoveredTerms.isEmpty() ? this : uncoveredTerms;
	}
	
}
