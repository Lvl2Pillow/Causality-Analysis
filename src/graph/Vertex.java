package graph;

/**
 * A {@code Vertex} object represents a vertex in a graph. Contains properties
 * about the vertex.
 * 
 * @author sojh2607
 * @author lvl2pillow
 *
 */
public class Vertex {
	private static int nVertices = 0;	// static count of number of Vertex instances
	
	public final int id;				// unique vertex id
	
	private String label;				// vertex label
	private double radius;				// vertex radius
	private int count;					// TODO
	
	public Vertex() {
		this.id = ++nVertices;
		this.label = "";
		this.radius = 0.0;
		this.count = 1;
	}
	
	public Vertex(String label) {
		this.id = ++nVertices;
		this.label = label;
		this.radius = 0.0;
		this.count = 1;
	}
	
	public String setLabel(String newLabel) { return label = newLabel; }
	public double setRadius(double newRadius) { return radius = newRadius; }
	public int incrementCount() { return ++count; }
	public int addCount(int n) { return count += n; }

	// auto-generated
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}
	
	// auto-generated
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vertex other = (Vertex) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}
	
	public int id() { return id; }
	public int getId() { return id(); }
	public String label() { return label; }
	public String getLabel() { return label(); }
	public double radius() { return radius; }
	public double getRadius() { return radius(); }
	public int count() { return count; }
	public int getCount() { return count(); }
	
}
