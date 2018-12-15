package ca.utoronto.utm.paint;

import javafx.scene.paint.Color;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Parse a file in Version 1.0 PaintSaveFile format. An instance of this class
 * understands the paint save file format, storing information about
 * its effort to parse a file. After a successful parse, an instance
 * will have an ArrayList of PaintCommand suitable for rendering.
 * If there is an error in the parse, the instance stores information
 * about the error. For more on the format of Version 1.0 of the paint 
 * save file format, see the associated documentation.
 * RESPONDS-TO View
 * 
 * @author Gagandeep Bhangal (1004235696)
 *
 */
public class PaintFileParser {
	private int lineNumber = 0; // the current line being parsed
	private String errorMessage = ""; // error encountered during parse
	private PaintModel paintModel; 
	private Color color;
	private ArrayList<PaintCommand> temp;
	
	/**
	 * Below are Patterns used in parsing 
	 */
	private Pattern pFileStart=Pattern.compile("^PaintSaveFileVersion1.0$");
	private Pattern pFileEnd=Pattern.compile("^EndPaintSaveFile$");
	private Pattern pColour=Pattern.compile("color:([0]*[01]?[0-9]?[0-9]|[0]*2[0-4][0-9]|[0]*25[0-5]),([0]*[01]?[0-9]?[0-9]|[0]*2[0-4][0-9]|[0]*25[0-5]),([0]*[01]?[0-9]?[0-9]|[0]*2[0-4][0-9]|[0]*25[0-5])");
	private Pattern pFilled=Pattern.compile("filled:(false)|filled:(true)");
	
	
	private Pattern pCircleStart=Pattern.compile("^Circle$");
	private Pattern pCircleEnd=Pattern.compile("^EndCircle$");
	private Pattern pCircleCenter=Pattern.compile("center:\\(([0-9]*),([0-9]*)\\)");
	private Pattern pCircleRadius=Pattern.compile("radius:([0-9]*+)");
	
	private Pattern pRectangleStart=Pattern.compile("^Rectangle$");
	private Pattern pRectangleEnd=Pattern.compile("^EndRectangle$");
	private Pattern pRectangleP1=Pattern.compile("p1:\\((-?[0-9]*),(-?[0-9]*)\\)");
	private Pattern pRectangleP2=Pattern.compile("p2:\\((-?[0-9]*),(-?[0-9]*)\\)");
	
	private Pattern pSquiggleStart=Pattern.compile("^Squiggle$");
	private Pattern pSquiggleEnd=Pattern.compile("^EndSquiggle$");
	private Pattern pPoints=Pattern.compile("^points$");
	private Pattern pEndPoints=Pattern.compile("^endpoints$");
	private Pattern pPoint=Pattern.compile("point:\\((-?[0-9]*),(-?[0-9]*)\\)");
	
	/**
	 * Store an appropriate error message in this, including 
	 * lineNumber where the error occurred. Then, set this error in the paintmodel and set the model to the
	 * previous one so nothing is erased on the screen.
	 * @param mesg is the error message to be added to this.errorMessage
	 */
	private void error(String mesg){
		this.errorMessage = "Error in line "+lineNumber+" "+mesg;
		this.paintModel.setError(this.errorMessage, true);
	}
	
	/**
	 * 
	 * @return the error message resulting from an unsuccessful parse
	 */
	public String getErrorMessage(){
		return this.errorMessage;
	}
	
	/**
	 * Parse the inputStream as a Paint Save File Format file.
	 * The result of the parse is stored as an ArrayList of Paint command.
	 * If the parse was not successful, this.errorMessage is appropriately
	 * set, with a useful error message.
	 * 
	 * @param inputStream the open file to parse
	 * @param paintModel the paint model to add the commands to
	 * @return whether the complete file was successfully parsed
	 */
	public boolean parse(BufferedReader inputStream, PaintModel paintModel) {
		this.paintModel = paintModel;
		this.errorMessage="";
		temp = new ArrayList<PaintCommand>();
		
		CircleCommand circleCommand = null; 
		RectangleCommand rectangleCommand = null;
		SquiggleCommand squiggleCommand = null;
	
		try {	
			int state=0; Matcher m; Matcher o; Matcher n; Matcher g; Matcher u; String l;
			this.lineNumber=0;
			
			while ((l = inputStream.readLine()) != null) {
				this.lineNumber++;
				l = l.replaceAll("\\s","");
				if (l.isEmpty() == false) {
					switch(state){
					case 0:
						m=pFileStart.matcher(l);
						if(m.matches()){
							state=1;
							break;
						}
						error("Expected Start of Paint Save File");
						return false;
					case 1: 
						m=pCircleStart.matcher(l);
						o=pRectangleStart.matcher(l);
						n=pSquiggleStart.matcher(l);
						g=pFileEnd.matcher(l);
						if(m.matches()){
							circleCommand = new CircleCommand();
							state=2; 
							break;
						}
						else if(o.matches()){
							rectangleCommand = new RectangleCommand();
							state=7; 
							break;
						}	
						else if(n.matches()){
							squiggleCommand = new SquiggleCommand();
							state=12; 
							break;
						}
						else if(g.matches()){
							state = 17;
							break;
						}
						error("Expected Start of Paint Save File");
						return false;
					case 2:
						m=pColour.matcher(l);
						if (m.matches()) {
							color = Color.rgb(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)));
							circleCommand.setColor(color);	
							state = 3;
							break;
						}
						error("Expected color in proper format");
						return false;
					case 3:
						m = pFilled.matcher(l);
						if (m.matches()) {
							int num;
							if (l.charAt(7) == 'f') {
								num = 1;
							} else {
								num = 2;
							}
							circleCommand.setFill(Boolean.parseBoolean(m.group(num)));
							state = 4;
							break;
						}
						error("Expected filled in proper format");
						return false;
					case 4:
						m=pCircleCenter.matcher(l);
						if (m.matches()) {
							circleCommand.setCentre(new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))));
							state = 5;
							break;
						}
						error("Expected center point in proper format");
						return false;
					case 5:
						m=pCircleRadius.matcher(l);
						if (m.matches()) {
							circleCommand.setRadius(Integer.parseInt(m.group(1)));
							state = 6;
							break;
						}
						error("Expected radius in proper format");
						return false;
					case 6:
						m=pCircleEnd.matcher(l);
						if (m.matches()) {
							temp.add(circleCommand);
							state = 1;
							break;
						}
						error("Expected EndCircle in proper format");
						return false;
					case 7:
						m=pColour.matcher(l);
						if (m.matches()) {
							color = Color.rgb(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)));
							rectangleCommand.setColor(color);
							state = 8;
							break;
						}
						error("Expected color in proper format");
						return false;
					case 8:
						m = pFilled.matcher(l);
						if (m.matches()) {
							int num;
							if (l.charAt(7) == 'f') {
								num = 1;
							} else {
								num = 2;
							}
							rectangleCommand.setFill(Boolean.parseBoolean(m.group(num)));
							state = 9;
							break;
						}
						error("Expected filled in proper format");
						return false;
					case 9:
						m=pRectangleP1.matcher(l);
						if (m.matches()) {
							rectangleCommand.setP1(new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))));
							state = 10;
							break;
						}
						error("Expected p1 point in proper format");
						return false;
					case 10:
						m=pRectangleP2.matcher(l);
						if (m.matches()) {
							rectangleCommand.setP2(new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))));
							state = 11;
							break;
						}
						error("Expected p2 point in proper format");
						return false;
					case 11:
						m=pRectangleEnd.matcher(l);
						if (m.matches()) {
							temp.add(rectangleCommand);
							state = 1;
							break;
						}
						error("Expected EndCircle in proper format");
						return false;
					case 12:
						m=pColour.matcher(l);
						if (m.matches()) {
							color = Color.rgb(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)));
							squiggleCommand.setColor(color);
							state = 13;
							break;
						}
						error("Expected color in proper format");
						return false;
					case 13:
						m = pFilled.matcher(l);
						if (m.matches()) {
							int num;
							if (l.charAt(7) == 'f') {
								num = 1;
							} else {
								num = 2;
							}
							squiggleCommand.setFill(Boolean.parseBoolean(m.group(num)));
							state = 14;
							break;
						}
						error("Expected filled in proper format");
						return false;
					case 14:
						m=pPoints.matcher(l);
						if (m.matches()) {
							state = 15;
							break;
						}
						error("Expected 'points' in proper format");
						return false;
					case 15:
						m=pPoint.matcher(l);
						u =pEndPoints.matcher(l);
						if (m.matches()) {
							squiggleCommand.add(new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))));
							state = 15;
							break;
						}
						else if (u.matches()) {
							state = 16;
							break;
						}
						error("Expected points in proper format");
						return false;
					case 16:
						m=pSquiggleEnd.matcher(l);
						if (m.matches()) {
							temp.add(squiggleCommand);
							state = 1;
							break;
						}
						error("Expected EndSquiggle in proper format");
						return false;
					case 17:
						error("Improper format, lines after End File line");
						return false;
				}
			}	
			}
		}  catch (Exception e){
			
		}
		this.paintModel.setError("", false);
		this.paintModel.setCommands(temp);
		return true;
	}
}
