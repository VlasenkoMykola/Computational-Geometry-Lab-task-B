import java.io.*;
import java.util.ArrayList;
import java.util.List;
//import java.util.Scanner;
import javax.swing.*;
import java.awt.*;




public class Main {

    public static ArrayList<Point> readPointsFromFile(String fileName) throws IOException {
        ArrayList<Point> points = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = br.readLine()) != null) {
	    if (line.charAt(0) == '#')
		continue;
            String[] coordinates = line.split("\\s+");
            double x = Double.parseDouble(coordinates[0]);
            double y = Double.parseDouble(coordinates[1]);
            points.add(new Point(x, y));
        }
        br.close();
        return points;
    }

    // visualization
    // Create Swing window
    public static void showFrame ( DatasetRangeTree convexHull, ArrayList<Point> points) {
	int frameWidth = 800;
	int frameHeight = 800;

	JFrame frame = new JFrame("Convex Hull Visualization");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setSize(frameWidth, frameHeight);

	// Create drawing panel
	DrawingPanel drawingPanel = new DrawingPanel(frameWidth, frameHeight, convexHull, points);

	// Add drawing panel to the frame
	frame.add(drawingPanel);

	// Show the frame
	frame.setVisible(true);
    }

    public static void main(String[] args) {
	try {
	  ArrayList<Point> points = readPointsFromFile("points.txt");

	  DatasetRangeTree convexHull = new DatasetRangeTree();
	  for (Point point: points) {
	      convexHull.insert(point);
	      //showFrame (convexHull, points);
	  }

	  System.out.println("Upper Convex Hull Points:");
	  for (Point point: convexHull.root.hull.getPoints()) {
	      System.out.println(point);
	  }

	  showFrame (convexHull, points);

      } catch (IOException e) {
	  System.err.println("Error reading points from file: " + e.getMessage());
      }
  }
}
