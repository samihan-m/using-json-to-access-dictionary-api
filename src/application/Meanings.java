package application;

public class Meanings
{
	public String partOfSpeech;
	public Definitions[] definitions;
	
	public Meanings(String partOfSpeech, Definitions[] definitions)
	{
		this.partOfSpeech = partOfSpeech;
		this.definitions = definitions;
	}	
}
