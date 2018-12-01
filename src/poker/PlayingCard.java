package poker;

public class PlayingCard {
	static public final char[] SUITS = {'H','S','C','D'};		// Constants to access the suits and type of card from.
	static public final String[] TYPES = {"A","2","3","4","5","6","7","8","9","10","J","Q","K"};

	private String type;		
	private char suit;
	private int faceValue;
	private int gameValue;
	public PlayingCard(String inputType, char inputSuit, int inputFaceValue, int inputGameValue) {  // This is the constructor of the class.
		type = inputType;
		suit = inputSuit;
		faceValue = inputFaceValue;
		gameValue = inputGameValue;
	}
	public String toString(){								// This is called whenever the class is printed or when called directly.
		String string = this.type + this.suit;		
		return string;
	}
	public String getType(){			// These three methods are used for accessing the private variables of the class.
		return this.type;
	}
	public char getSuit(){			// These three methods are used for accessing the private variables of the class.
		return this.suit;
	}
	public int getFaceValue(){
		return this.faceValue;
	}
	public int getGameValue(){
		return this.gameValue;
	}
}