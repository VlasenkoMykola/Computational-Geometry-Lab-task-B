public class CNode extends Node<Point> {

    protected CNode left, right, lMax;
    protected ConvexHull hull;

    public CNode (Point c) {
	//new leaf node
	this.isLeaf = true;
	this.color = BLACK;
	this.data = c;
	this.lMax = this;

	hull = new ConvexHull(c);
    }

    public CNode (CNode lMax, CNode left, CNode right) {
	//new internal node
	this.isLeaf = false;//internal node
	this.color = RED;
	this.lMax = lMax;
	this.left = left;
	this.right = right;

	hull = ConvexHull.bridge(left.hull, right.hull);
    }

    public CNode(Point c, CNode leftLeaf, CNode rightLeaf) {
	//new leaf node
	//only use this constructor is the leafs are to be chained
	assert (leftLeaf == null || leftLeaf.isLeaf)
	    && (rightLeaf == null || rightLeaf.isLeaf) :
	"leftLeaf and rightLeaf must be leafs";

	this.isLeaf = true;
	this.color = BLACK;
	this.data = c;
	this.lMax = this;
	this.left = leftLeaf;
	this.right = rightLeaf;

	hull = new ConvexHull(c);
    }

}
