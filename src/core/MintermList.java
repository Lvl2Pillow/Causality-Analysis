package core;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A {@link LinkedList} of {@link Minterm} elements, representing a data set.
 * 
 * @author lvl2pillow
 *
 */
public class MintermList extends LinkedList<Minterm> {
	// auto-generated serialVersionUID
	private static final long serialVersionUID = -4397936737489018745L;
	
	public MintermList() {
		super();
	}
	
	public MintermList(Collection<Minterm> dataSet) {
		super(dataSet);
	}
	
	/**
	 * Removes all minterms covered by a given term.
	 * 
	 * @param term
	 * @return resulting list of minterms.
	 */
	public MintermList removeTermsCoveredBy(Term term) {
		Iterator<Minterm> i = super.iterator();
		while (i.hasNext()) {
			if (term.covers(i.next()))
				i.remove();
		}
		return this;
	}
	
	/**
	 * Returns all minterms that are not covered by a given term.
	 * 
	 * @param term
	 * @return list of uncovered terms.
	 */
	public MintermList getUncoveredBy(Term term) {
		MintermList uncoveredMinterms = new MintermList();
		for (Minterm minterm : this) {
			if (!term.covers(minterm))
				uncoveredMinterms.add(minterm);
		}	
		return uncoveredMinterms;
	}
	
}
