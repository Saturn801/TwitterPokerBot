package poker;

public class PokerPlayer {
	protected String name;			// PokerPlayer holds a name, a hand of cards, and the number of chips.
	protected HandOfCards hand;	
	protected int chips;
	protected int currentBet;
	protected int index;
	private String twitterID;
	
	public PokerPlayer(String playerName, DeckOfCards deck, int numChips, String playerID) {  // The constructor takes in a name for the player, and a deck of cards to initialize the hand with, along with their initial chips value.
		name = playerName;
		hand = new HandOfCards(deck);
		chips = numChips;
		currentBet = 0;
		twitterID = playerID;
	}

	public void restartPlayer(DeckOfCards deck){	// This method prepares the player for a new round (resets bets, draws a new hand, etc)
		hand = new HandOfCards(deck);
		currentBet = 0;
	}
	
	public boolean isInteger(String str) {
	    for (int i = 0; i < str.length(); i++) {
	        if (!Character.isDigit(str.charAt(i))) {
	            return false;
	        }
	    }
	    return true;
	}
	
	public boolean isValidDiscardInteger(String str) {
		if(!isInteger(str)){
			return false;
		}
	    int num = Integer.parseInt(str);
	    if(num<1 || num>5){
	    	return false;
	    }
	    return true;
	}
	
	public String[] getBet(String input, int currentHandBet){
		String[] bet_split = null;
		bet_split = input.trim().split("\\s+");
		
		if((!bet_split[0].equalsIgnoreCase("raise") && !bet_split[0].equalsIgnoreCase("call") && !bet_split[0].equalsIgnoreCase("fold") && !bet_split[0].equalsIgnoreCase("all-in")) || ((bet_split[0].equalsIgnoreCase("call") || bet_split[0].equalsIgnoreCase("fold") || bet_split[0].equalsIgnoreCase("all-in")) && bet_split.length!=1) || (bet_split[0].equalsIgnoreCase("raise") && bet_split.length!=2) || (bet_split[0].equalsIgnoreCase("raise") && bet_split.length==2 && !isInteger(bet_split[1]))){
			System.out.println("Input invalid, please enter in the correct format: ");
			return null;
		}
		return bet_split;
	}
	
	public boolean[] getDiscard(String input){
		String[] discard_split = null;
		boolean[] discard = new boolean[5];
		
		discard_split = input.trim().split("\\s+");
		if((!discard_split[0].equalsIgnoreCase("continue") && !discard_split[0].equalsIgnoreCase("discard")) || (discard_split[0].equalsIgnoreCase("continue") && discard_split.length!=1) || ((discard_split[0].equalsIgnoreCase("discard") && (discard_split.length==1 || discard_split.length>4)) || (discard_split[0].equalsIgnoreCase("discard") && discard_split.length==2 && !isValidDiscardInteger(discard_split[1])) || (discard_split[0].equalsIgnoreCase("discard") && discard_split.length==3 && (!isValidDiscardInteger(discard_split[1]) || !isValidDiscardInteger(discard_split[2]))) || (discard_split[0].equalsIgnoreCase("discard") && discard_split.length==4 && (!isValidDiscardInteger(discard_split[1]) || !isValidDiscardInteger(discard_split[2]) || !isValidDiscardInteger(discard_split[3]))))){
			System.out.println("Input invalid, please enter in the correct format: ");
			return null;
		}
		int check;
		for(int i=0;i<5;i++){
			if(discard_split[0]=="continue"){
				discard[i] = false;
			}
			else{
				check=0;
				for(int j=1;j<discard_split.length;j++){
					if((Integer.parseInt(discard_split[j])-1)==i){
						check=1;
					}
				}
				if(check==0){
					discard[i] = false;
				}
				else if(check==1){
					discard[i] = true;
				}
			}
		}
		return discard;
	}
	
	public void betChips(int numChips){		// This is used when the player bets chips, to store their current bet so that it can be removed from them if they lose.
		chips -= numChips;
		currentBet += numChips;
	}
	
	public void updateChips(int numChips){	// This method will be used for either adding or subtracting chips from the player (add negative chips to remove).
		chips += numChips;
	}
	
	public boolean isBankrupt(){	// This method determines whether a player is bankrupt or not, depending on their remaining chips.
		if(chips<=0)
			return true;
		else
			return false;
	}
	
	public int getHandValue(){		// This will return the gameValue of each players hand, to compare.
		return this.hand.getGameValue();
	}
	
	public String toString(){	// The toString method just returns a string with info about the player (name, chips, and whether they're bankrupt).
		String output = "";
		output += this.name + "'s chips:   " + this.chips;
		if(isBankrupt())
			output += "\n" + this.name + " is bankrupt and must leave the game.";
		else
			output += "\n" + this.name + " is NOT bankrupt and may keep playing.";
		return output;
	}
	// Added three get methods for accessing the name, hand, and their current bet.
	public String getName(){
		return this.name;
	}
	
	public HandOfCards getHand(){
		return this.hand;
	}
	
	public int getChips(){
		return this.chips;
	}
	
	public int getCurrentBet(){
		return this.currentBet;
	}
	
	public String getID(){
		return this.twitterID;
	}

	public void setHand(HandOfCards newHand){	// Method to forcefully set the hand of a player (for use in testing only).
		hand = newHand;
	}
}
