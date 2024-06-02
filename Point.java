public class Point implements Comparable<Point> {

    public double x;
    public double y;

    public Point(double x, double y) {
	this.x = x;
	this.y = y;
    }

    public Point(Point other) {
	this.x = other.x;
	this.y = other.y;
    }

    public String toString() {
	return "(" + x + ", " + y + ")";
    }

    public double getX() {
	return x;
    }

    public double getY() {
	return y;
    }

    public int compareTo(Point other) {
	if (this.x < other.x) {
	    return -1;
	}
	else if (this.x > other.x) {
	    return +1;
	}
	if (this.y < other.y) {
	    return -1;
	}
	else if (this.y > other.y) {
	    return +1;
	}
	else {
	    return 0;
	}
    }
}

