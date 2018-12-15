package ca.utoronto.utm.paint;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;

/**
 * IS-A PaintCommand
 * @author Gagandeep Bhangal
 *
 */
public class SquiggleCommand extends PaintCommand {
	private ArrayList<Point> points=new ArrayList<Point>();
	
	public SquiggleCommand() {
	}
	public void add(Point p){
		this.points.add(p); 
		this.setChanged();
		this.notifyObservers();
	}
	public ArrayList<Point> getPoints(){ return this.points; }
	
	@Override
	public void execute(GraphicsContext g) {
		ArrayList<Point> points = this.getPoints();
		g.setStroke(this.getColor());
		for(int i=0;i<points.size()-1;i++){
			Point p1 = points.get(i);
			Point p2 = points.get(i+1);
			g.strokeLine(p1.x, p1.y, p2.x, p2.y);
		}
		
	}
	/**
	 * Print method which is used to write the data of the squiggle in the correct format to a file during saving.
	 */
	public String print(){
		String s = "";
		s+="Squiggle\n";
		s+="\tcolor:"+r+","+g+","+b+"\n";
		s+="\tfilled:"+fill+"\n";
		s+="\tpoints\n";
		for (Point p: points) {
			s+="\t\t point:("+p.x+","+p.y+")\n";
		}
		s+="\tend points\n";
		s+="End Squiggle";
		return s;
	}
}
