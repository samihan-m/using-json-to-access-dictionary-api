package application;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class Main extends Application
{
	static boolean debug = false;	//this can be enabled through user input and will activate some debug printing
	@Override
	public void start(Stage primaryStage)
	{
		try
		{
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		String input = "";
		Scanner consoleScanner = new Scanner(System.in);
		while(!input.equals("!EXIT")) 
		{
			System.out.println("Enter an English word for which you want the phonetic version: \tOr, type '!EXIT' to exit.\nType !DEBUG to toggle debug mode.");
			input = consoleScanner.nextLine();
			
			if(input.equals("!DEBUG"))
			{
				debug = !debug;
				
				String debugStatus = "DISABLED";
				if(debug)
				{
					debugStatus = "ENABLED";
				}
				
				System.out.println("DEBUG MODE IS NOW: " + debugStatus);
				
				continue;	//skips the rest of the loop - i don't want the program to think the command is a word input
			}
			
			//checking to see if the input is actually a word and doesn't have symbols/numbers
			boolean isProperWord = true;
			for(int i = 0; i < input.length(); i++)
			{
				if(!Character.isLetter(input.charAt(i)))
				{
					isProperWord = false;
				}
			}
			
			if(isProperWord)
			{
				try 
				{
					String[] phonetics = getPhonetic(input);
					if(debug)
						System.out.println(Arrays.toString(phonetics));
					else
					{
						int phoneticCount = phonetics.length;
						for(int i = 0; i < phoneticCount; i++)
						{
							System.out.println("Pronunciation " + (i+1) + ": " + phonetics[i]);
						}
					}
				} catch(Exception e) 
				{
					//This means the API is down or the connection can't be made or something and the program won't work
					System.out.print("Connection to Dictionary API cannot be established..");
					e.printStackTrace();
				}
			}
			else
			{
				System.out.println("You entered an invalid word. Try a different one.");
			}
			//System.out.print("Pre-Window");	//this happens before the window is opened
			//launch(args);		//disabling window because it doesn't do anything HAHA
			//System.out.print("Post-Window");	//this happens after the window is closed
			System.out.println("\n");	//just a newline to make it look better
		}
		System.out.println("Exiting..");
		consoleScanner.close();
	}
	
	//everything below is non-javafx stuff

	/**
	 * Returns an array of phonetic pronunciations for the entered word.
	 * The word argument should be in English.
	 * 
	 * Calls an API I found when I looked up "Dictionary API".
	 * Turns the API response into a JSON object using GSON and some classes I wrote to map the JSON.
	 * 
	 * @param word	a word in English to get pronunciations for
	 * @return	phonetics	an array of Strings, the phonetic pronunciations of the word
	 * @throws Exception	if the connection to the API cannot be established
	 * @see	WordObject
	 * @see Phonetic
	 * @see Meanings
	 * @see Definitions
	 */
	public static String[] getPhonetic(String word) throws Exception
	{
		String URL = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;
		
		HttpClient client = HttpClient.newHttpClient();
		
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(URL)).timeout(Duration.ofMinutes(1)).GET().build();
		
		HttpResponse<String> dictResponse = client.sendAsync(request, BodyHandlers.ofString()).join(); 
		
		String data = dictResponse.body();

		if(debug)
			System.out.println(data);
		
		Gson gson = new Gson();
		Type wordObjects = new TypeToken<List<WordObject>>(){}.getType();
		List<WordObject> list = gson.fromJson(data, wordObjects);
		
		int phoneticCount = list.size();
		String[] phonetics = new String[phoneticCount];
		
		for(int i = 0; i < phoneticCount; i++)
		{
			try
			{
				phonetics[i] = list.get(i).phonetics[0].text;
			}catch(Exception e)
			{
				//if this happens, it means that for some reason the Dictionary API returned a blank phonetics array.
				//just ignore this and move on, nothing i can do
				phonetics[i] = "ERROR: NO PRONUNCIATION FOUND";
			}
			
		}
		return phonetics;
	}
	
}
