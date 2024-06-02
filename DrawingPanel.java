import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;

class DrawingPanel extends JPanel {
    private ArrayList<Point> points;

    private DatasetRangeTree convexHull;
    // BoundingBox for the set of points
    private double minX, minY, maxX, maxY;
    // coefficients from point coordinates to screen
    public double coefficientX, coefficientY;

    public DrawingPanel(int screenWidth, int screenHeight, DatasetRangeTree convexHull, ArrayList<Point> points) {
	this.convexHull = convexHull;
	this.points = points;
	BoundingBox bb = new BoundingBox(points);
        this.minX = bb.minX;
        this.minY = bb.minY;
        this.maxX = bb.maxX;
        this.maxY = bb.maxY;

        setPreferredSize(new Dimension(screenWidth, screenHeight));
	double pointsWidth = maxX - minX;
	double pointsHeight = maxY - minY;
	// to screen pixel coordinates
	this.coefficientX = (double) screenWidth / pointsWidth;
	this.coefficientY = (double) screenHeight/ pointsHeight;
    }

    public int panelX( double x) {
	return (int) ((x - minX) * coefficientX);
    }
    public int panelY( double y) {
        return (int) ((maxY - y) * coefficientY);
    }



    /**
     * Draw an arrow line between two points.
     * @param g the graphics component.
     * @param x1 x-position of first point.
     * @param y1 y-position of first point.
     * @param x2 x-position of second point.
     * @param y2 y-position of second point.
     * @param d  the width of the arrow.
     * @param h  the height of the arrow.
     */
    private void drawArrowLine(Graphics2D g, int x1, int y1, int x2, int y2, int d, int h) {
	int dx = x2 - x1, dy = y2 - y1;
	double D = Math.sqrt(dx*dx + dy*dy);
	double xm = D - d, xn = xm, ym = h, yn = -h, x;
	double sin = dy / D, cos = dx / D;

	x = xm*cos - ym*sin + x1;
	ym = xm*sin + ym*cos + y1;
	xm = x;

	x = xn*cos - yn*sin + x1;
	yn = xn*sin + yn*cos + y1;
	xn = x;

	int[] xpoints = {x2, (int) xm, (int) xn};
	int[] ypoints = {y2, (int) ym, (int) yn};

	g.drawLine(x1, y1, x2, y2);
	g.fillPolygon(xpoints, ypoints, 3);
    }

    private void drawPoint(Graphics2D g2d, Point point) {
	    g2d.fillOval(panelX(point.x) - 3, panelY(point.y) - 3, 6, 6);
    }

    private void drawConvexHull(Graphics2D g2d, CNode node) {
            if (node == null)
                return;

	    List<Point> points = node.hull.getPoints();
	    int numPoints = points.size();
	    if (numPoints == 1) {
		Point point = points.get(0);
		g2d.fillOval(panelX(point.x) - 4, panelY(point.y) - 4, 8, 8);

	    }
	    else if (numPoints > 1) {
		Point from = points.get(0);
		for (int i=1; i< numPoints; i++) {
		    Point point = points.get(i);
		    g2d.drawLine(panelX(from.x), panelY(from.y), panelX(point.x), panelY(point.y));
		    from = point;
		}
	    }
	    g2d.setColor(Color.GREEN);
	    drawConvexHull(g2d, node.left);
	    drawConvexHull(g2d, node.right);
    }

    private void drawNode(Graphics2D g2d, CNode node) {
        if (node == null)
            return;

	if (node.isLeaf) {
	    // real point for leaf nodes
	    Point point = node.data;
	    g2d.setColor(Color.BLACK);
	    drawPoint(g2d, point);
	} else {
	    CNode leftNode = node.left;
	    drawNode(g2d, leftNode);
	    CNode rightNode = node.right;
	    drawNode(g2d, rightNode);
	}
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

	/*
	float thickness = 3;
	Stroke oldStroke = g2d.getStroke();
	g2d.setStroke(new BasicStroke(thickness));

        // Draw bounding box rectangle
        //g2d.setColor(Color.BLUE);
        //g2d.drawRect(1, 1, (int) getWidth()-2, getHeight()-2);

	g2d.setStroke(oldStroke);
	*/

	//g2d.setColor(Color.YELLOW);
        g2d.setColor(Color.BLUE);
	drawConvexHull(g2d, convexHull.root);

	// Draw points
	drawNode(g2d, convexHull.root);
    }
}
