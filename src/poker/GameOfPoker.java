package poker;

import java.util.ArrayList;
import java.util.Random;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import twitter4j.TwitterException;

public class GameOfPoker {
	private ArrayList<PokerPlayer> players = new ArrayList<PokerPlayer>();
	private DeckOfCards deck;
	private HandOfPoker currentHand;
	private int currentRound;
	private String twitterID;
	private static final int MAX_BOTS = 4;
	
	public GameOfPoker(PokerPlayer humanPlayer, DeckOfCards gameDeck, String userID){
		players.add(humanPlayer);
		deck = gameDeck;
		currentRound = 0;
		twitterID = userID;
	}
	
	public void addPlayer(PokerPlayer AIPlayer){	// Able to add the AIPlayers afterwards
		players.add(AIPlayer);
	}
	
	public boolean playNextRound(String input, long tweetID) throws TwitterException{
		TwitterTest twitter = new TwitterTest();
		if(currentRound==0){	// TWITTER DONE
			// Setup game
			System.out.println("GAME START!!\n\n\n");
			
			// AI setup....
			ArrayList<String> AInames = new ArrayList<>();
			try(BufferedReader br = new BufferedReader(new InputStreamReader(TwitterTest.class.getResourceAsStream("/txts/names.txt")))) {
			    String line = br.readLine();

			    while (line != null) {
			        AInames.add(line);
			        line = br.readLine();
			    }
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Random rn = new Random();
			int num_players = rn.nextInt(MAX_BOTS) + 1;
			for(int i=1; i<num_players+1; i++){
				
				// gets random int, if int leads to an already used name, gets next random and rechecks until name not used
				int randomName = rn.nextInt(AInames.size());
				for (int j=0; j<players.size(); j++){
					if (AInames.get(randomName).equals(players.get(j).getName())){
						randomName = rn.nextInt(AInames.size());
						j=0;
					}
				}
				PokerPlayer bot = new AutomatedPokerPlayer(AInames.get(randomName), deck, 1000);
				players.add(bot);
			}
			// AI setup complete.
			
			currentHand = new HandOfPoker(players);
			System.out.println("\n\nNEW ROUND!\n\n");
			String temp = "NEW ROUND!\n\n";
			temp += currentHand.toString();
			temp += currentHand.getPlayerHand();
			temp += "Enter your next action ('continue', 'discard X Y Z'): ";
			twitter.directMessage(twitterID, temp);
			System.out.println(currentHand);
			System.out.println(currentHand.getHands());
			System.out.println("Enter your next action ('continue', 'discard X Y Z'): ");
			currentRound = 1;
		}
		else if(currentRound==1){	// TWITTER DONE
			// Discard & setup bet
			boolean valid = currentHand.discardRound(input, tweetID);
			if(!valid){
				return false;
			}
			String temp = currentHand.getPlayerHand();
			temp += "MINIMUM BET IS 10 CHIPS.";
			temp += "\nFULL BETTING ROUND COMMENCES:\n";
			temp += "Enter your next action ('call', 'fold', 'raise N', or 'all-in'): ";
			twitter.directMessage(twitterID, temp);
			System.out.println(currentHand.getHands());
			currentHand.setCurrentBet(currentHand.MINIMUM_BET);
			// Then follows the betting round.
			for(int i=0;i<currentHand.getPlayerStatuses().length;i++){
				currentHand.getPlayerStatuses()[i] = "NOT_SET";
			}
			System.out.println("MINIMUM BET IS 10 CHIPS.");
			System.out.println("\nFULL BETTING ROUND COMMENCES:\n");
			System.out.println("Enter your next action ('call', 'fold', 'raise N', or 'all-in'): ");
			currentRound = 2;
		}
		else if(currentRound==2){
			// Betting round & check end game			
			boolean valid = currentHand.BettingRound(input, tweetID);
			if(!valid){
				return false;
			}
			// LOOP WHILE PLAYER IS "FOLD" OR "ALL-IN"
			while((currentHand.checkPlayersBettingStatus(currentHand.getCurrentBet())!=players.size() && currentHand.checkFoldedPlayers()!=players.size()-1 ) && (currentHand.getPlayerStatuses()[0].equalsIgnoreCase("fold") || currentHand.getPlayerStatuses()[0].equalsIgnoreCase("all-in"))){
				currentHand.BettingRound(null, tweetID);
			}
			if(currentHand.checkPlayersBettingStatus(currentHand.getCurrentBet())==players.size() || currentHand.checkFoldedPlayers()==players.size()-1){			
				currentHand.handleResult();
				System.out.println(currentHand.winnerNotify());
				
				String temp = currentHand.winnerNotify();
				temp += currentHand.toString();
				twitter.directMessage(twitterID, temp);
				
				System.out.println(currentHand);
				checkEndHandStatus();
				if(players.size()==1 || isPlayerBankrupt()){
					return true;
					//System.out.println("\n\nGAME IS OVER!\n\n");
					//System.out.println("The winner is: " + players.get(0).getName() + "! Congratulations!");
				}
				else{
					temp = "\n\nNEW ROUND!\n\n";
					temp += currentHand.getPlayerHand();
					temp += "Enter your next action ('continue', 'discard X Y Z'): ";
					System.out.println(currentHand.getHands());
					System.out.println("Enter your next action ('continue', 'discard X Y Z'): ");
					currentRound = 1;
					twitter.directMessage(twitterID, temp);
				}
			}
			else{
				System.out.println("Enter your next action ('call', 'fold', 'raise N', or 'all-in'): ");
				twitter.directMessage(twitterID, "Enter your next action ('call', 'fold', 'raise N', or 'all-in'): ");
			}
		}
		return false;
	}
	
	private boolean isPlayerBankrupt(){
		boolean check = false;
		for(int i=0; i<players.size(); i++){
			if(players.get(i).getClass() == PokerPlayer.class){
				return check;
			}
		}
		check = true;
		return check;
	}
	
	private void checkEndHandStatus() throws TwitterException{
		deck.reset();
		TwitterTest twitter = new TwitterTest();
		String temp = "";
		for(int i=0; i<players.size(); i++){
			if(players.get(i).isBankrupt()){
				System.out.println(players.get(i).getName() + " has been removed from the game for being bankrupt.");
				temp += players.get(i).getName() + " has been removed from the game for being bankrupt.\n\n";
				players.remove(i);
				i=-1;
			}
			else{
				players.get(i).restartPlayer(deck);
			}
		}
		currentHand = new HandOfPoker(players);
		if(!temp.equals("")){
			twitter.directMessage(twitterID, temp);
		}
	}
	
	public void playGame(){
	}
	
	public PokerPlayer getPlayer(String userID){
		PokerPlayer player = null;
		for(int i=0;i<players.size();i++){
			if(players.get(i).getID().equalsIgnoreCase(userID)){
				player = players.get(i);
			}
		}
		return player;
	}
}
