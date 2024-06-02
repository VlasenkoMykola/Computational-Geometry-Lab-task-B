public class DatasetRangeTree extends BlackRedTree<Point> {

    protected CNode root;

    public DatasetRangeTree () {
	super();
    }

    protected static void DOWN (CNode n) {
	assert n != null : "n != null";
	assert !n.isLeaf : "!n.isLeaf";

	n.left.hull = new ConvexHull(ConcatenableQueue.concatenate(n.hull.split(n.lMax.data, LEFT, true), n.left.hull));
	n.right.hull = new ConvexHull(ConcatenableQueue.concatenate(n.right.hull, n.hull));
    }

    protected static void UP (CNode n) {
	assert n != null : "n != null";
	assert !n.isLeaf : "!n.isLeaf";

	n.hull = ConvexHull.bridge(n.left.hull, n.right.hull);
    }

    protected static CNode rotateLeft (CNode n) {
	assert n != null : "n != null";
	assert !n.isLeaf && !n.right.isLeaf && n.right.color == RED :
	"rotateLeft a BLACK right node will break the tree balance";

	DOWN(n);
	CNode tempCNode = n.right;
	boolean tempColor = n.color;
	DOWN(tempCNode);
	n.right = tempCNode.left;
	n.color = tempCNode.color;
	UP(n);
	tempCNode.left = n;
	tempCNode.color = tempColor;
	UP(tempCNode);
	return tempCNode;
    }

    protected static CNode rotateRight (CNode n) {
	assert n != null : "n != null";
	assert !n.isLeaf && !n.left.isLeaf && n.left.color == RED :
	"rotateRight a BLACK left node will break the tree balance";

	DOWN(n);
	CNode tempCNode = n.left;
	boolean tempColor = n.color;
	DOWN(tempCNode);
	n.left = tempCNode.right;
	n.color = tempCNode.color;
	UP(n);
	tempCNode.right = n;
	tempCNode.color = tempColor;
	UP(tempCNode);
	return tempCNode;
    }

    protected CNode addLeaf (Point c, CNode nLeft, CNode nRight) {

	size ++;
	return new CNode(c);
    }

    protected static void flipTripleColor (CNode n) {
	assert n != null : "n != null";
	assert !n.isLeaf && !n.left.isLeaf && !n.right.isLeaf : "!n.isLeaf && !n.left.isLeaf && !n.right.isLeaf";

	n.color = !n.color;
	n.left.color = !n.left.color;
	n.right.color = !n.right.color;
    }

    protected static CNode fixUp (CNode n) {
	assert n != null : "n != null";

	if (n.isLeaf) {
	    return n;
	}

	if (n.left.color == BLACK && n.right.color == RED) {
	    n = rotateLeft(n);
	}
	else {
	    if (n.left.color == RED && n.left.left.color == RED) {
		assert n.right.color == BLACK : "n.right.color == BLACK";

		n = rotateRight(n);
	    }

	    if (n.left.color == RED && n.right.color == RED) {
		flipTripleColor(n);
	    }
	}
	return n;
    }

    protected CNode insertAt (CNode n, Point e) {
	assert n != null : "n != null";

	//pre-condition: n.hull is well-defined and as a whole

	if (e.compareTo(n.lMax.data) <= 0) {
	    if (n.isLeaf) {
		if (e.compareTo(n.data) == 0) {//corner case, replace old value by the new one
		    n.data = e;
		}
		else {
		    CNode nNew = addLeaf(e, n.left, n);
		    n = new CNode(nNew, nNew, n);
		}
	    }
	    else {
		DOWN(n);
		n.left = insertAt(n.left, e);
		UP(n);
	    }
	}
	else {
	    if (n.isLeaf) {
		CNode nNew = addLeaf(e, n, n.right);
		n = new CNode(n, n, nNew);
	    }
	    else {
		DOWN(n);
		n.right = insertAt(n.right, e);
		UP(n);
	    }
	}

	n = fixUp(n);

	//post-condition: n.hull is well-defined and as a whole

	return n;
    }

    protected CNode deleteAt (CNode n, Point e) {
	assert n != null : "n != null";
	assert !n.isLeaf : "!n.isLeaf";
	assert n.color == RED || n.left.color == RED : "n.color == RED || n.left.color == RED";

	//pre-condition: n.hull is well-defined and as a whole

	if (e.compareTo(n.lMax.data) <= 0) {//to the left subtree
	    if (n.left.isLeaf) {
		if (e.compareTo(n.left.data) != 0) {//didn'd find e
		    return n;
		}
		else {//base case
		    assert n.color == RED : "n.color == RED";
		    assert n.right.isLeaf : "n.right.isLeaf";

		    DOWN(n);
		    removeLeaf(n.left);
		    return n.right;
		}
	    }

	    DOWN(n);

	    if (e.compareTo(n.lMax.data) == 0) {
		//need to update this lMax
		//happens only once per delete
		assert !n.left.isLeaf : "!n.left.isLeaf";

		CNode tempCNode = n.left;
		while (!tempCNode.right.isLeaf) {
		    tempCNode = tempCNode.right;
		}
		n.lMax = tempCNode.lMax;
	    }

	    if (n.left.color == RED || n.left.left.color == RED) {//safe to go down
		n.left = deleteAt(n.left,e);
		UP(n);
	    }
	    else {//need help
		assert !n.right.isLeaf : "!n.right.isLeaf";

		flipTripleColor(n);
		n.left = deleteAt(n.left,e);
		if (n.left.color == RED) {//didn't use the help
		    UP(n);
		    flipTripleColor(n);
		}
		else if (n.right.left.color == BLACK) {
		    UP(n);
		    n = rotateLeft(n);
		}
		else {
		    n.right = rotateRight(n.right);//note: now n is no "UP" yet
		    UP(n);
		    n = rotateLeft(n);
		    flipTripleColor(n);
		}
	    }
	}
	else {//to the right subtree
	    if (n.right.isLeaf) {
		if (e.compareTo(n.right.data) != 0) {//didn't find e
		    return n;
		}
		else {//base case
		    DOWN(n);
		    removeLeaf(n.right);
		    n.left.color = BLACK;
		    //note: n.left is either a RED internal node, in which case n is BLACK, or n.left is a BLACK leaf node
		    return n.left;
		}
	    }
	    else if (n.right.left.color == RED) {//safe to go down
		DOWN(n);
		n.right = deleteAt(n.right,e);
		UP(n);
	    }
	    else if (n.color == RED) {//need help
		assert n.left.color == BLACK : "n.left.color == BLACK";

		flipTripleColor(n);
		DOWN(n);
		n.right = deleteAt(n.right,e);
		UP(n);
		if (n.right.color == RED) {//didn't use the help
		    flipTripleColor(n);
		}
		else if (n.left.left.color == RED) {
		    n = rotateRight(n);
		    flipTripleColor(n);
		}
	    }
	    else {
		assert n.left.color == RED : "n.left.color == RED";

		n = rotateRight(n);
		DOWN(n);
		n.right = deleteAt(n.right,e);
		UP(n);
		if (n.right.color == RED) {//didn't use the help
		    n = rotateLeft(n);
		}
	    }
	}

	//post-condition: n.hull is well-defined and as a whole
	return n;
    }

    public void insert (Point e) {
	if (root == null) {//empty tree
	    root = new CNode(e);
	    size = 1;
	}
	else {
	    root = insertAt(root,e);
	    if (root.color == RED) {
		root.color = BLACK;
	    }
	}
    }

    public void delete (Point e) {
	if (root == null) {
	    return;
	}
	else if (root.isLeaf) {
	    if (e.compareTo(root.data) == 0) {
		root = null;
		size = 0;
	    }
	}
	else {
	    if (root.left.color == BLACK && root.right.color == BLACK) {//may need help
		root.color = RED;
	    }
	    root = deleteAt(root, e);
	    if (root.color == RED) {//didn't use the help
		root.color = BLACK;
	    }
	}
    }

}

