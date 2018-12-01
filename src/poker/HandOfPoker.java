package poker;

import java.util.ArrayList;

import twitter4j.TwitterException;

public class HandOfPoker {		// A hand of poker will contain an arrayList of the players in that round, and an integer value representing the current money in the pot.
	private ArrayList<PokerPlayer> players = new ArrayList<PokerPlayer>();
	private int currentBet;
	static public final int MINIMUM_BET = 10;
	private String[] playerStatus;
	private String winnerNotify="";
	
	public HandOfPoker(ArrayList<PokerPlayer> newPlayers){	// Initialize with ArrayList of players.
		players = newPlayers;
		playerStatus = new String[players.size()];
	}
	
	public boolean discardRound(String playerInput, long tweetID) throws TwitterException{		// This method goes through every player and asks what cards to discard if human player or calculates which to discard if bot and then discards them and gets a new hand.
		TwitterTest twitter = new TwitterTest();
		String temp = "";
		for(int i=0;i<players.size();i++){
			System.out.println(players.get(i).getName() + " has started discarding.");
			boolean[] discard = null;
			discard = players.get(i).getDiscard(playerInput);
			if(discard==null){
				temp = players.get(0).getID() + " Input invalid, please tweet us again in the correct format.";
				twitter.replyToTweet(tweetID, temp);
				return false;
			}
			int numDiscarded=players.get(i).getHand().discardHand(discard);
			
			temp += players.get(i).getName() + " has discarded ";
			temp += (numDiscarded==0)? "nothing." : (numDiscarded==1)? "1 card." : numDiscarded +" cards.";
			temp += "\n\n";
			
			System.out.println(players.get(i).getName() + " has discarded "+numDiscarded+" card(s).\n\n");

		}
		twitter.directMessage(players.get(0).getID(), temp);
		return true;
	}
	
	public int checkPlayersBettingStatus(int currentBet){
		int doneCount = 0;
		for(int i=0;i<players.size();i++){
			if((playerStatus[i].equalsIgnoreCase("call") && players.get(i).getCurrentBet()==currentBet) || playerStatus[i].equalsIgnoreCase("fold") || playerStatus[i].equalsIgnoreCase("all-in")){
				doneCount++;
			}
		}
		return doneCount;
	}
	
	public int checkFoldedPlayers(){
		int foldCount = 0;
		for(int i=0;i<players.size();i++){
			if(playerStatus[i].equalsIgnoreCase("fold")){
				foldCount++;
			}
		}
		return foldCount;
	}
	
	public boolean BettingRound(String playerInput, long tweetID) throws TwitterException{
		int numChipsNeeded;
		String[] input;
		TwitterTest twitter = new TwitterTest();
		String temp = "";
		for(int currentPlayer=0; currentPlayer<players.size(); currentPlayer++){
			if(!playerStatus[currentPlayer].equalsIgnoreCase("fold") && !playerStatus[currentPlayer].equalsIgnoreCase("all-in")){
				System.out.println("CURRENT BET: " + currentBet);
				temp += "CURRENT BET: " + currentBet+"\n";
				input = players.get(currentPlayer).getBet(playerInput, currentBet);
				if(input==null){
					temp = players.get(0).getID() + " Input invalid, please tweet us again in the correct format.";
					twitter.replyToTweet(tweetID, temp);
					return false;
				}
				numChipsNeeded = currentBet - players.get(currentPlayer).getCurrentBet();
				if((input[0].equalsIgnoreCase("call") && players.get(currentPlayer).getChips()<numChipsNeeded) || (input[0].equalsIgnoreCase("raise") && players.get(currentPlayer).getChips()<(numChipsNeeded + Integer.parseInt(input[1])))){
					System.out.println("You do not have enough chips to do that, please enter something else.");
					return false;
				}
				if(input[0].equalsIgnoreCase("call")){	// DONE
					playerStatus[currentPlayer] = "call";
					if(players.get(currentPlayer).getCurrentBet()<currentBet){
						players.get(currentPlayer).betChips(currentBet - players.get(currentPlayer).getCurrentBet());
					}
					System.out.println(players.get(currentPlayer).getName() + " has called the current bet.");
					temp += players.get(currentPlayer).getName() + " has called the current bet.\n";
				}
				else if(input[0].equalsIgnoreCase("fold")){	// DONE
					playerStatus[currentPlayer] = "fold";
					System.out.println(players.get(currentPlayer).getName() + " has folded and is out of this round.");
					temp += players.get(currentPlayer).getName() + " has folded and is out of this round.\n";
					if(players.get(currentPlayer).getCurrentBet()==0){
						players.get(currentPlayer).betChips(MINIMUM_BET);						
						System.out.println("However they must bet the minimum bet, so " + MINIMUM_BET + " has been added to the pot from their stack.");
						temp += "However they must bet the minimum bet, so " + MINIMUM_BET + " has been added to the pot from their stack.\n";
					}
				}
				else if(input[0].equalsIgnoreCase("raise")){	// DONE
					playerStatus[currentPlayer] = "raise";
					if(players.get(currentPlayer).getCurrentBet()<currentBet){
						players.get(currentPlayer).betChips(currentBet - players.get(currentPlayer).getCurrentBet());
					}
					players.get(currentPlayer).betChips(Integer.parseInt(input[1]));
					currentBet += Integer.parseInt(input[1]);
					System.out.println(players.get(currentPlayer).getName() + " has raised the bet to " + currentBet);
					temp += players.get(currentPlayer).getName() + " has raised the bet to " + currentBet +"\n";
				}
				else if(input[0].equalsIgnoreCase("all-in")){	// DONE
					playerStatus[currentPlayer] = "all-in";
					players.get(currentPlayer).betChips(players.get(currentPlayer).getChips());
					if(currentBet<players.get(currentPlayer).getCurrentBet()){
						currentBet = players.get(currentPlayer).getCurrentBet();
					}
					System.out.println(players.get(currentPlayer).getName() + " has gone all in with " + players.get(currentPlayer).getCurrentBet());
					temp += players.get(currentPlayer).getName() + " has gone all in with " + players.get(currentPlayer).getCurrentBet() +"\n";
				}
			}
			if( (checkPlayersBettingStatus(currentBet)==players.size()) || (checkFoldedPlayers()==players.size()-1)){		
				twitter.directMessage(players.get(0).getID(), temp);
				return true;
			}
			temp+= "\n";
		}
		twitter.directMessage(players.get(0).getID(), temp);
		return true;
	}
	
	/**<p>	Handles the result of the round
	 * <br>	Distributes chips to winners (resolves ties)
	 * <br>	Compiles string of winning players names and amounts  */
	public void handleResult(){
		ArrayList<PokerPlayer> winners; // Duplicate players arrayList.
		winners = new ArrayList<PokerPlayer>(players);

		int totalPot=0;
		for (int i=0; i<players.size(); i++){ // Calculate total pot.
			totalPot+=players.get(i).getCurrentBet();
		}
		winnerNotify = "The total value of the pot is: "+totalPot+"\n\n";
		
		for (int i = 0; i < winners.size(); i++) { // remove folders from winners arrayList.
			if (playerStatus[players.indexOf(winners.get(i))].equalsIgnoreCase("fold")){
				winners.remove(i);
				i--;
			}
		}

		PokerPlayer temp;
		for (int i = 0; i < winners.size(); i++) { // Sort new arrayList in order of handValues, highest first.	
			for (int j = i+1; j < winners.size(); j++) {
				if (winners.get(i).getHandValue()< winners.get(j).getHandValue()) {
					temp = winners.get(i);
					winners.set(i, winners.get(j));
					winners.set(j, temp);
				}
			}
		}
		
		for (int i = 0; i < winners.size(); i++) { // Sort again, ordering ties into lower to higher bets.	
			for (int j = i+1; j < winners.size(); j++) {
				if ( (winners.get(i).getHandValue() == winners.get(j).getHandValue()) && (winners.get(i).getCurrentBet() > winners.get(j).getCurrentBet()) ) {
					temp = winners.get(i);
					winners.set(i, winners.get(j));
					winners.set(j, temp);
				}
			}
		}
		
		if (winners.size()==1){
			winnerNotify+= winners.get(0).getName() +" is the only player remaining and wins the pot.\n\n";
			winners.get(0).updateChips(totalPot);
			totalPot = 0;
		}
		
		int position=0;	// start looking at first place in the array (i.e highest game value)
		int winnings;	// players winnings
		while (totalPot>0 && position<winners.size()){ 
							// Go through winners, distribute winnings based on possible calculated winnings
							// if the remaining totalPot does not hit 0 after each winner/'tied winners', continue on to next place.
			
			if((position < winners.size()-1) && (winners.get(position).getHandValue() == winners.get(position+1).getHandValue()) ){ // tie detected
				int numTies = 0;
				int numFulls = 0;
				
				if (totalPot > 0){
					winnerNotify+= (position == 0)? "Tied for first place: \n" : (position == 1)? "Tied for second place: \n" : 
									(position == 2)? "Tied for third place: \n" : "Tied for fourth place: \n";
				}
				
				for (int i=0; i<winners.size(); i++) { // go through ALL players, including current player
					if (winners.get(position).getHandValue() == winners.get(i).getHandValue()) {
						numTies++; 																// number of tied players (will count self, so numTies is minimum of 2)
						if (winners.get(i).getCurrentBet() == currentBet) numFulls++; 			// number of tied non side pot players
					}
				}
				
				for (int i=0; i<numTies; i++){
					winnings = Math.min(calcWinnings(winners.get(position), numTies, numFulls, totalPot), totalPot);

					if (winnings > 0){
						winners.get(position).updateChips(winnings);
						totalPot -= winnings;
		
						winnerNotify+= winners.get(position).getName();
						winnerNotify+= " wins " +winnings+".\n";
					}
					position++;
				}
				winnerNotify+= "\n";
				
			} else { // no tie
				int numFulls = (winners.get(position).getCurrentBet() == currentBet)? 1 : 0; // met bet or side potter?
				winnings = Math.min(calcWinnings(winners.get(position), 1, numFulls, totalPot), totalPot);

				if (winnings > 0){
					winners.get(position).updateChips(winnings);
					totalPot -= winnings;
					
					winnerNotify+= (position == 0)? "In first place: " : (position == 1)? "In second place: " : 
									(position == 2)? "In third place: " : (position == 3)? "In fourth place: " : "In fifth place: ";
					
					winnerNotify+= winners.get(position).getName();
					winnerNotify+= " wins " +winnings+".\n\n";
				}
				position++;	
			}
		}
	}
	
	/**<p> Determines the maximum amount a given player can win
	 * @param player		The player to check
	 * @param numTies		The number of tied players involved
	 * @param numFulls		The number of tied players with a full bet involved
	 * @param totalPot		The pot total
	 * @return 				<b>int</b> for the maximum amount the player can win */
	private int calcWinnings(PokerPlayer player, int numTies, int numFulls, int totalPot){	
		int winnings = 0;
	
		if (playerStatus[players.indexOf(player)].equalsIgnoreCase("fold")){ // folded, no monies for you
			return 0;
		}
		
		if ( (numTies == 1) && (numFulls == 1)){ // no ties, full bet, wins all the things
			return totalPot;
		}
		
		if ( (numTies == 1) && (numFulls == 0)){ // no ties, side bet, wins some of the things
			winnings = 0;
			for (int i=0; i<players.size(); i++){
				winnings+= Math.min(players.get(i).getCurrentBet(), player.getCurrentBet());
			}
			return winnings;
		}
			
		if (numTies > 1){ // player in question is part of a tie
			if (player.getCurrentBet() != currentBet){ // player is a side bet
				winnings = 0;
				for (int i=0; i<players.size(); i++){
					winnings+= Math.min(players.get(i).getCurrentBet(), player.getCurrentBet());
				}
				winnings = Math.min(winnings, totalPot/(numTies));
				return winnings;
			}
			
			if (player.getCurrentBet() == currentBet){ // player is a full bet (thanks to sorting, sides have been dealt with)
				numFulls = (numFulls==0)? 1 : numFulls; //shouldn't be possible but just in case
				return totalPot/numFulls;
			}			
		}	
		return winnings;
	}
	
	/** returns the compiled string of winners/winnings */
	public String winnerNotify(){
		return winnerNotify;
	}
	
	
	
	
	public String getHands(){		// This returns each players current hand.
		String output = "";
		for(int i=0; i<this.players.size(); i++){
			output += players.get(i).getName() + "'s hand:   " + players.get(i).getHand().toString() + "\n\n";
		}
		return output;
	}
	
	public String getPlayerHand(){		// This returns each players current hand.
		String output = "";
		output += "Your current hand is:   " + players.get(0).getHand().toString() + "\n\n";
		return output;
	}
	
	public String toString(){	// This just return information about each player.
		String output = "";
		for(int i=0; i<this.players.size(); i++){
			output += players.get(i).toString() + "\n\n";
		}
		return output;
	}
	
	public void setCurrentBet(int bet){
		this.currentBet = bet;
	}
	
	public String[] getPlayerStatuses(){
		return this.playerStatus;
	}
	
	public int getCurrentBet(){
		return this.currentBet;
	}
	
	public PokerPlayer checkPlayer(int position){		// This can be used to access players from the round to get their information.
		return players.get(position);
	}
}
