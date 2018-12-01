package poker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterTest {

	private ArrayList<GameOfPoker> games = new ArrayList<GameOfPoker>();
	static public final int STARTING_CHIPS = 1000;
	private TwitterStream twitterStream;
	
	private static Configuration setUp() {
		ArrayList<String> keys = new ArrayList<>();
		try(BufferedReader br = new BufferedReader(new InputStreamReader(TwitterTest.class.getResourceAsStream("/txts/Keys.txt")))) {
		    String line = br.readLine();

		    while (line != null) {
		        keys.add(line);
		        line = br.readLine();
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Twitter twitter;
		ConfigurationBuilder config = new ConfigurationBuilder();
		config.setDebugEnabled(true)
			.setOAuthConsumerKey(keys.get(0))
			.setOAuthConsumerSecret(keys.get(1))
			.setOAuthAccessToken(keys.get(2))
			.setOAuthAccessTokenSecret(keys.get(3));
		Configuration tf = config.build();
		
		return tf;
	}
	
	private void tweet(String update) throws TwitterException{
		Configuration tf = setUp();
		Twitter twitter = new TwitterFactory(tf).getInstance();
		Status status = twitter.updateStatus(update);
		System.out.println("Successfully updated the status to [" + status.getText() + "].");	
	}
	
	public void directMessage(String userID, String message) throws TwitterException{
		Configuration tf = setUp();
		Twitter twitter = new TwitterFactory(tf).getInstance();
		twitter.sendDirectMessage(userID, message);
		System.out.println("Message sent to user: " + userID);
	}
	
	public void replyToTweet(long tweetID, String message) throws TwitterException{
		Configuration tf = setUp();
		Twitter twitter = new TwitterFactory(tf).getInstance();
		twitter.updateStatus(new StatusUpdate(message).inReplyToStatusId(tweetID));
		System.out.println("Reply has been sent to user.");
	}
	
	public void stop() {
	    if(twitterStream != null) {
	        twitterStream.shutdown();
	    }
	}
	
	private String removeUserID(String input){
		String new_string = "";
		String[] temp_string = null;
		temp_string = input.trim().split("\\s+");
		for(int i=1;i<temp_string.length;i++){
			if(i!=temp_string.length-1)
				new_string += temp_string[i] + " ";
			else
				new_string += temp_string[i];
		}
		return new_string;
	}

	private void GetTweetStreamForKeywords(String[] query) throws TwitterException{
		// The factory instance is re-useable and thread safe.
	    //Twitter twitter = setUp();
		stop();
		Configuration tf = setUp();
		twitterStream = new TwitterStreamFactory(tf).getInstance();
		final Twitter twitter = new TwitterFactory(tf).getInstance();
	    StatusListener listener = new StatusListener() {
	    	public void onStatus(Status status){
	    		String hashtag = "#TeamObsidianDealMeIn";
	    		String mention = "@obsid_poker";
	    		try {
					if(!twitter.showFriendship("obsid_poker", status.getUser().getScreenName()).isSourceFollowedByTarget()){
						//System.out.println("USER IS NOT FOLLOWING US!!!!!!!");
						String message = '@'+status.getUser().getScreenName();
						message += " You are not currently following us, please do so before trying to play and tweet again.";
						replyToTweet(status.getId(), message);
					}
					else if(status.getText().toLowerCase().contains(hashtag.toLowerCase())){
						boolean check = false;
						for(int i=0; i<games.size();i++){	
							if(games.get(i).getPlayer('@'+status.getUser().getScreenName())!=null){
								check = true;
							}   				
						}
						if(!check){
							System.out.println("New Game created for user: @" + status.getUser().getScreenName());
					        DeckOfCards new_deck = new DeckOfCards();	      
					        PokerPlayer new_player = new PokerPlayer(status.getUser().getName(), new_deck, STARTING_CHIPS, ('@'+status.getUser().getScreenName()));
					        GameOfPoker new_game = new GameOfPoker(new_player, new_deck, ('@'+status.getUser().getScreenName()));
					        games.add(new_game);
					        directMessage('@'+status.getUser().getScreenName(), "Welcome to Team Obsidian Poker!\nWe hope you enjoy your game.\nTweet @ our bot to give your next move.");
					        games.get(games.size()-1).playNextRound(null, status.getId());
						}
						else{
							System.out.println("That user is already in a game.");
							String message = '@'+status.getUser().getScreenName();
							message += " You are already in a game, please @ our bot to give your next move.";
							replyToTweet(status.getId(), message);
						}
					}
					else if(status.getText().toLowerCase().contains(mention.toLowerCase())){
						boolean check = false;
						for(int i=0; i<games.size();i++){	
							if(games.get(i).getPlayer('@'+status.getUser().getScreenName())!=null){
								check = true;
								boolean gameOver = false;
								gameOver = games.get(i).playNextRound(removeUserID(status.getText()), status.getId());
								if(gameOver){
									games.remove(i);
									directMessage('@'+status.getUser().getScreenName(), "GAME OVER.\nThanks for playing!");
								}
							}   				
						}
						if(!check){
							System.out.println("User is not currently in a game.");
							String message = '@'+status.getUser().getScreenName();
							message += " You are not currently in a game, please tweet our hashtag to start a new game.";
							replyToTweet(status.getId(), message);
						}
					}
				} catch (TwitterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }

	        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
	            System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
	        }

	        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
	            System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
	        }

	        public void onScrubGeo(long userId, long upToStatusId) {
	            System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
	        }

	        public void onException(Exception ex) {
	            ex.printStackTrace();
	        }

			@Override
			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub
				System.out.println("Program Stalled");
			}			
	    };
	    
	    FilterQuery fq = new FilterQuery();

	    fq.track(query);
	    
	    twitterStream.addListener(listener);
	    twitterStream.filter(fq);	    
	}

	public static void main(String[] args) throws TwitterException{
		TwitterTest test = new TwitterTest();
		//test.tweet("@SamBryan801 Hi there, this is test no. 10");
		String[] keywords = {"#TeamObsidianDealMeIn", "@obsid_poker"};
		test.GetTweetStreamForKeywords(keywords);
		//test.directMessage("@SamBryan801", "Hello Sam Test 2");
	}
} 
