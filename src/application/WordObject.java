package application;

public class WordObject
{
	public String word;
	public Phonetic[] phonetics;
	public Meanings[] meanings;
	
	public WordObject(String word, Phonetic[] phonetics, Meanings[] meanings)
	{
		this.word = word;
		this.phonetics = phonetics;
		this.meanings = meanings;
	}
}
