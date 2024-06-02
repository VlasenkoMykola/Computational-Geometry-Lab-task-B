import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConvexHull extends ConcatenableQueue<Point> {

    public ConvexHull () {
	super();
    }

    public ConvexHull (Point c) {
	super(c);
    }

    public ConvexHull (ConcatenableQueue<Point> q) {
	super(q);
    }

    public ConvexHull (List<Point> pointList) {
	super();
	Collections.sort(pointList);
	ArrayList<Point> hull = new ArrayList<Point>();
	for (Point c : pointList) {
	    while (hull.size() > 1) {
		Point b = hull.get(hull.size() - 1);
		Point a = hull.get(hull.size() - 2);
		if (computeSlope(a,b) > computeSlope(b,c)) {
		    break;
		}
		else {
		    hull.remove(hull.size() - 1);
		}
	    }
	    hull.add(c);
	}

	for (Point p : hull) {
	    this.insert(p);
	}
    }

    public static ConvexHull bridge (ConvexHull lHull, ConvexHull rHull) {
	assert lHull != null && rHull != null : "lHull != null && rHull != null";
	assert lHull.root != null && rHull.root != null : "lHull.root != null && rHull.root != null";

	//locate the appropriate pointers on both hulls
	Node<Point> lItr = lHull.root;
	Node<Point> rItr = rHull.root;

	boolean done = false;
	double middleX = (lHull.getMaxNode().data.x + rHull.getMinNode().data.x)/2.0;

	while ( !done ) {
	    double t = computeSlope(lItr.lMax, rItr.lMax);
	    int iL = determineCase(lItr.lMax, t);
	    int iR = determineCase(rItr.lMax, t);

	    switch (iL) {
	    case -1:
		switch (iR) {
		case -1:
		    rItr = rItr.right;
		    break;
		case 0:
		    lItr = lItr.right;
		    if (!rItr.isLeaf && rItr.right != null) {
			rItr = rItr.right;
		    }
		    break;
		case +1: //the most difficult one
		    double lHeight = lItr.lMax.data.y +
			computeSlope(lItr.lMax, lItr.lMax.right)*(middleX - lItr.lMax.data.x);
		    double rHeight = rItr.lMax.data.y +
			computeSlope(rItr.lMax.left, rItr.lMax)*(middleX - rItr.lMax.data.x);
		    if ( lHeight <= rHeight) {
			rItr = rItr.left;
		    }
		    else {
			lItr = lItr.right;
		    }
		    break;
		}
		break;
	    case 0:
		switch (iR) {
		case -1:
		    if (!lItr.isLeaf && lItr.left != null) {
			lItr = lItr.left;
		    }
		    rItr = rItr.right;
		    break;
		case 0://done!
		    lItr = lItr.lMax;
		    rItr = rItr.lMax;
		    done = true;
		    break;
		case +1:
		    if (!lItr.isLeaf && lItr.left != null) {
			lItr = lItr.left;
		    }
		    rItr = rItr.left;
		    break;
		}
		break;
	    case +1:
		switch (iR) {
		case -1:
		    lItr = lItr.left;
		    rItr = rItr.right;
		    break;
		case 0:
		    lItr = lItr.left;
		    if (!rItr.isLeaf && rItr.right != null) {
			rItr = rItr.right;
		    }
		    break;
		case +1:
		    lItr = lItr.left;
		    break;
		}
		break;
	    }
	}

	assert sanityCheck(lHull, rHull, lItr, rItr) : "wrong cut!";

	assert lItr != null && rItr != null : "otherwise, something is wrong";

	return new ConvexHull(concatenate(lHull.split(lItr.data, LEFT, true), rHull.split(rItr.data, RIGHT, false)));
    }

    public Point locateAtSlope (double t) {
	assert this.root != null : "this.root != null";

	Node<Point> itr = root;
	while (itr != null) {
	    int i = determineCase(itr.lMax, t);
	    if (i*t < 0) {//note: t positive angle vs. negative angle, the behaviors are different
		itr = itr.right;
	    }
	    else if (i*t > 0) {
		itr = itr.left;
	    }
	    else {
		itr = itr.lMax;
		break;
	    }
	}

	assert itr != null : "something is wrong if itr == null";

	return itr.data;
    }

    public boolean isValidHull ( ) {
	if (root == null) {
	    return true;
	}
	Node<Point> n = minNode;
	if (n.right == null) {
	    return true; //a one-node hull is valid
	}

	n = n.right;
	while (n.right != null) {
	    if (computeSlope(n.left, n) < computeSlope(n, n.right)) {
		return false;
	    }
	    n = n.right;
	}

	return true;
    }

    public static boolean sameHull (ConvexHull h1, ConvexHull h2) {
	assert h1 != null && h2 != null : "h1 != null && h2 != null";

	boolean result = true;

        Iterator<Point> n1 = h1.iterator();
        Iterator<Point> n2 = h1.iterator();
	while (true) {
	    if (!n1.hasNext() || !n2.hasNext()) {
		if (n1.hasNext() || n2.hasNext()) {
		    result = false;
		}
		break;
	    }
	    Point p1 = n1.next();
	    Point p2 = n2.next();
	    if (p1.compareTo(p2) != 0) {
		result = false;
		break;
	    }
	}

	return result;
    }

    public void printHull ( ) {
	for (Point p: this) {
	    System.out.println(p);
	}
    }

    public List<Point> getPoints ( ) {
        ArrayList<Point> points = new ArrayList<>();
	for (Point p: this) {
	    points.add(p);
	}
	return points;
    }

    protected static double computeSlope (Node<Point> leftN, Node<Point> rightN) {
	assert leftN != null && rightN != null : "leftN != null && rightN != null";
	assert leftN.isLeaf && rightN.isLeaf : "leftN.isLeaf && rightN.isLeaf";
	assert (rightN.data.x - leftN.data.x)	 > 0 : "angle is defined from left to right";

	return (rightN.data.y - leftN.data.y)/(rightN.data.x - leftN.data.x);
    }

    protected static double computeSlope (Point p1, Point p2) {
	assert p1 != null && p2 != null : "p1 != null && p2 != null";
	assert (p2.x - p1.x)	 > 0 : "angle is defined from left to right";

	return (p2.y - p1.y)/(p2.x - p1.x);
    }

    protected static int determineCase (Node<Point> n, double t) {
	assert n != null : "n != null";
	assert n.isLeaf : "n.isLeaf";

	boolean leftAbove = true;//fake left corner (at y=-infty)
	boolean rightAbove = false;//fake right corner (at y=-infty)

	if ( (n.left != null) && computeSlope(n.left, n) < t  ) {
	    leftAbove = false;
	}

	if ( (n.right != null) && computeSlope(n, n.right) > t  ) {
	    rightAbove = true;
	}

	if (leftAbove && rightAbove) {
	    return -1;
	}
	else if (!leftAbove && !rightAbove) {
	    return +1;
	}
	else {
	    assert leftAbove && !rightAbove : "concave";

	    //corner case for single-node-ConvexHull also comes here
	    return 0;
	}
    }

    private static boolean sanityCheck (ConvexHull lHull, ConvexHull rHull, Node<Point> lPtr, Node<Point> rPtr) {

	double t = computeSlope(lPtr, rPtr);
	if (lPtr.left != null && computeSlope(lPtr.left, lPtr) < t) {
	    return false;
	}
	if (lPtr.right != null && computeSlope(lPtr, lPtr.right) > t) {
	    return false;
	}
	if (rPtr.left != null && computeSlope(rPtr.left, rPtr) < t) {
	    return false;
	}
	if (rPtr.right != null && computeSlope(rPtr, rPtr.right) > t) {
	    return false;
	}

	return true;
    }

}
