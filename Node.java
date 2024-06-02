public class Node<E extends Comparable<E>> {

    final protected static boolean RED = false;
    final protected static boolean BLACK = true;

    protected E data;
    protected Node<E> left, right, lMax;
    protected boolean color;
    protected boolean isLeaf;

    public Node() {

    }

    //new leaf node
    public Node(E e) {
	this.isLeaf = true;
	this.color = BLACK;
	this.data = e;
	this.lMax = this;
    }

    //new internal node
    public Node(Node<E> lMax, Node<E> left, Node<E> right) {
	this.isLeaf = false;//internal node
	this.color = RED;
	this.lMax = lMax;
	this.left = left;
	this.right = right;
    }

    //new leaf node for chained leafs tree (proshitoe derevo)
    //only use this constructor if the leafs are to be chained
    public Node(E e, Node<E> leftLeaf, Node<E> rightLeaf) {
	assert (leftLeaf == null || leftLeaf.isLeaf)
	    && (rightLeaf == null || rightLeaf.isLeaf) :
	"leftLeaf and rightLeaf must be leafs";

	this.isLeaf = true;
	this.color = BLACK;
	this.data = e;
	this.lMax = this;
	this.left = leftLeaf;
	this.right = rightLeaf;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

}
