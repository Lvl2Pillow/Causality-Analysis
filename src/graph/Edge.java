package graph;

import util.Stat;

/**
 * A {@code Edge} object represents a edge in a graph. Contains properties
 * about the edge.
 * 
 * @author sojh2607
 * @author lvl2pillow
 *
 */
public class Edge {
	private static int nEdges = 0;		// static count of number of Edge instances
	private static final double normalizedCap = 10.0;	// normalize between 1-10
	
	public final int id;				// unique vertex id
	
	private String label;				// edge label
	private int count;					// TODO
	private static int minCount = Integer.MAX_VALUE;	// min count from all Edge instances
	private static int maxCount = Integer.MIN_VALUE;	// max count from all Edge instances
	
	public Edge() {
		this.id = ++nEdges;
		this.label = "";
		this.count = 0;
	}
	
	public Edge(String label) {
		this.id = ++nEdges;
		this.label = label;
		this.count = 0;
	}
        
	public String setLabel(String newLabel) { return label = newLabel; }
	public int incrementCount() { return addCount(1); }
	public int addCount(int n) { 
		// update min and max counts
		minCount = (n < minCount) ? n : minCount;
		maxCount = (n > maxCount) ? n : maxCount;
		return count += n;
	}
	
	/**
	 * Returns a normalized count (between 1 and 10).
	 * 
	 */
	public double getNormalizedCount() {
		return Stat.normalize(count, minCount, maxCount)*(normalizedCap-1.0)+1.0;
	}
	public String label() { return label; }
	public String getLabel() { return label(); }
	public int count() { return count; }
	public int getCount() { return count(); }

}
