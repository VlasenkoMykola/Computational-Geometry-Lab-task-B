import java.util.List;

public class BoundingBox {
    double minX;
    double maxX;
    double minY;
    double maxY;
    public BoundingBox() {
	this.minX = 0;
	this.maxX = 0;
	this.minY = 0;
	this.maxY = 0;
    }
    public BoundingBox(double minX, double maxX, double minY, double maxY) {
	this.minX = minX;
	this.maxX = maxX;
	this.minY = minY;
	this.maxY = maxY;
    }

    public BoundingBox(List<Point> points) {
	this.minX = 0;
	this.maxX = 0;
	this.minY = 0;
	this.maxY = 0;
        if (points != null) {
	    for (Point point: points) {
		this.minX = Math.min(this.minX, point.x);
		this.minY = Math.min(this.minY, point.y);
		this.maxX = Math.max(this.maxX, point.x);
		this.maxY = Math.max(this.maxY, point.y);
	    }
	}
    }

    public boolean contains(Point point) {
        return this.minX <= point.x && point.x <= this.maxX &&
        this.minY <= point.y && point.y <= this.maxY;
    }

}
