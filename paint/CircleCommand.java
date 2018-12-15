package ca.utoronto.utm.paint;
import javafx.scene.canvas.GraphicsContext;

/**
 * IS-A PaintCommand
 * @author Gagandeep Bhangal 
 *
 */
public class CircleCommand extends PaintCommand {
	private Point centre;
	private int radius;
	
	public CircleCommand(Point centre, int radius){
		this.centre = centre;
		this.radius = radius;
	}
	
	public CircleCommand(){	}
	
	public Point getCentre() { return centre; }
	public void setCentre(Point centre) {
		this.centre = centre; 
		this.setChanged();
		this.notifyObservers();
	}
	public int getRadius() { return radius; }
	public void setRadius(int radius) { 
		this.radius = radius; 
		this.setChanged();
		this.notifyObservers();
	}
	public void execute(GraphicsContext g){
		int x = this.getCentre().x;
		int y = this.getCentre().y;
		int radius = this.getRadius();
		if(this.isFill()){
			g.setFill(this.getColor());
			g.fillOval(x-radius, y-radius, 2*radius, 2*radius);
		} else {
			g.setStroke(this.getColor());
			g.strokeOval(x-radius, y-radius, 2*radius, 2*radius);
		}
	}
	/**
	 * Print method which is used to write the data of the circle in the correct format to a file during saving.
	 */
	public String print(){
		String s = "";
		s+="Circle\n";
		s+="\tcolor:"+r+","+g+","+b+"\n";
		s+="\tfilled:"+fill+"\n";
		s+="\tcenter:("+this.getCentre().x+","+this.getCentre().y+")\n";
		s+="\tradius:"+ radius +"\n";
		s+="End Circle";
		return s;
	}
}
