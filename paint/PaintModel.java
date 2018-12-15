package ca.utoronto.utm.paint;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javafx.scene.canvas.GraphicsContext;

public class PaintModel extends Observable implements Observer {
	
	private boolean error = false;
	private String errorMessage;

	public void save(PrintWriter writer) {
		writer.println("Paint Save File Format Version 1.0");
		for (PaintCommand t: commands) {
			writer.println(t.print());
		}
		writer.println("End Paint Save File");
		writer.close();
	}
	
	public void reset(){
		for(PaintCommand c: this.commands){
			c.deleteObserver(this);
		}
		this.commands.clear();
		this.setChanged();
		this.notifyObservers();
	}
	
	public void addCommand(PaintCommand command){
		this.commands.add(command);
		command.addObserver(this);
		this.setChanged();
		this.notifyObservers();
	}
	
	private ArrayList<PaintCommand> commands = new ArrayList<PaintCommand>();

	public void executeAll(GraphicsContext g) {
		for(PaintCommand c: this.commands){
			c.execute(g);
		}
	}
	
	public ArrayList<PaintCommand> getCommands(){
		return this.commands;
	}
	
	public void setCommands(ArrayList<PaintCommand> temp){
		this.commands = temp;
	}
	
	public void setError(String errorMessage, boolean error) {
		this.error = error;
		this.errorMessage = errorMessage;
	}
	
	public boolean getError(){
		return this.error;
	}
	
	public String getErrorMessage(){
		return this.errorMessage;
	}
	
	/**
	 * We Observe our model components, the PaintCommands
	 */
	@Override
	public void update(Observable o, Object arg) {
		this.setChanged();
		this.notifyObservers();
	}
}
