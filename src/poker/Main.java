package poker;

import java.util.ArrayList;

public class Main {
	static private void testPlayingCard(){
		PlayingCard[] cards = new PlayingCard[52];		// Array of size 52 to store the cards in.
		int count=0;
		int gameValue;
		for(int i=0;i<4;i++){							// Two nested for loops to cycle through the 4 suits and 13 types for each suit.
			for(int j=0;j<13;j++){
				if(j==0){								// If it is an ace, the game value is 14.
					gameValue = 14;
				}
				else{									// Otherwise, it is the same as it's face value.
					gameValue = j+1;
				}
				PlayingCard card = new PlayingCard(PlayingCard.TYPES[j],PlayingCard.SUITS[i],j+1,gameValue);
				cards[count] = card;		// Create a card object with appropriate variables and add to array.
				count++;
			}
		}
		for(int i=0;i<52;i++){				// Loop for printing cards.
			System.out.println(cards[i]);
		}
	}
	
	
	static private void testDeckOfCards(){
		DeckOfCards deck = new DeckOfCards();
		System.out.println(deck);	// Print out the starting deck.
		int i=0;
		PlayingCard test;
		while(i<=54){		// Loop through and "deal" the cards, printing them as they are removed.
			if(i==50){		// This if statement is just to test the returnCard method.
				System.out.println("\n" + deck);		// Print out the deck as is at this point.
				deck.returnCard(new PlayingCard(PlayingCard.TYPES[0],PlayingCard.SUITS[0],1,14));		// Return the Ace of Hearts and Ace of Spades.
				deck.returnCard(new PlayingCard(PlayingCard.TYPES[0],PlayingCard.SUITS[1],1,14));
				System.out.println(deck + "\n");		// Print out the updated deck to make sure they were added correctly.
			}											// The loop should go through all 52 original cards, the two returned cards, and try to deal one more but print the appropriate error message.
			test = deck.dealNext();
			System.out.println(test);
			i++;
		}

		deck.reset();			// Reset the deck, and do everything again (mainly just to check that reset works okay and that everything still works as it should).
		System.out.println(deck);
		i=0;

		while(i<=54){
			if(i==50){
				System.out.println("\n" + deck);
				deck.returnCard(new PlayingCard(PlayingCard.TYPES[0],PlayingCard.SUITS[0],1,14));
				deck.returnCard(new PlayingCard(PlayingCard.TYPES[0],PlayingCard.SUITS[1],1,14));
				System.out.println(deck + "\n");
			}
			test = deck.dealNext();
			System.out.println(test);
			i++;
		}
	}
	
	
	static private void testHandOfCards(){
		DeckOfCards deck = new DeckOfCards();			// Create a new deck and hand with 5 cards from that deck.
		HandOfCards hand = new HandOfCards(deck);
		PlayingCard card1, card2, card3, card4, card5;

		System.out.println("EXAMPLES OF ALL HAND TYPES & CHECKS:");

		card1 = new PlayingCard("A",'H',1,14);
		card2 = new PlayingCard("K",'H',13,13);
		card3 = new PlayingCard("Q",'H',12,12);
		card4 = new PlayingCard("J",'H',11,11);
		card5 = new PlayingCard("10",'H',10,10);

		hand.setHand(card1, card2, card3, card4, card5);
		System.out.println("\n\n" + hand);
		System.out.println(hand.typeHand());
		System.out.println();
		for(int j=0;j<HandOfCards.HAND_SIZE;j++){
			System.out.println("Card: " + j + " discard probability -> " + hand.getDiscardProbability(j));
		}

		card1 = new PlayingCard("8",'H',8,8);
		card2 = new PlayingCard("7",'H',7,7);
		card3 = new PlayingCard("6",'H',6,6);
		card4 = new PlayingCard("5",'H',5,5);
		card5 = new PlayingCard("4",'H',4,4);

		hand.setHand(card1, card2, card3, card4, card5);
		System.out.println("\n\n" + hand);
		System.out.println(hand.typeHand());
		System.out.println();
		for(int j=0;j<HandOfCards.HAND_SIZE;j++){
			System.out.println("Card: " + j + " discard probability -> " + hand.getDiscardProbability(j));
		}

		card1 = new PlayingCard("8",'H',8,8);
		card2 = new PlayingCard("8",'H',8,8);
		card3 = new PlayingCard("8",'H',8,8);
		card4 = new PlayingCard("8",'H',8,8);
		card5 = new PlayingCard("4",'H',4,4);

		hand.setHand(card1, card2, card3, card4, card5);
		System.out.println("\n\n" + hand);
		System.out.println(hand.typeHand());
		System.out.println();
		for(int j=0;j<HandOfCards.HAND_SIZE;j++){
			System.out.println("Card: " + j + " discard probability -> " + hand.getDiscardProbability(j));
		}

		card1 = new PlayingCard("8",'H',8,8);
		card2 = new PlayingCard("8",'H',8,8);
		card3 = new PlayingCard("8",'H',8,8);
		card4 = new PlayingCard("4",'H',4,4);
		card5 = new PlayingCard("4",'H',4,4);

		hand.setHand(card1, card2, card3, card4, card5);
		System.out.println("\n\n" + hand);
		System.out.println(hand.typeHand());
		System.out.println();
		for(int j=0;j<HandOfCards.HAND_SIZE;j++){
			System.out.println("Card: " + j + " discard probability -> " + hand.getDiscardProbability(j));
		}

		card1 = new PlayingCard("8",'H',8,8);
		card2 = new PlayingCard("8",'H',8,8);
		card3 = new PlayingCard("3",'H',3,3);
		card4 = new PlayingCard("4",'H',4,4);
		card5 = new PlayingCard("4",'H',4,4);

		hand.setHand(card1, card2, card3, card4, card5);
		System.out.println("\n\n" + hand);
		System.out.println(hand.typeHand());
		System.out.println();
		for(int j=0;j<HandOfCards.HAND_SIZE;j++){
			System.out.println("Card: " + j + " discard probability -> " + hand.getDiscardProbability(j));
		}

		card1 = new PlayingCard("8",'H',8,8);
		card2 = new PlayingCard("7",'H',7,7);
		card3 = new PlayingCard("6",'C',6,6);
		card4 = new PlayingCard("5",'D',5,5);
		card5 = new PlayingCard("4",'S',4,4);

		hand.setHand(card1, card2, card3, card4, card5);
		System.out.println("\n\n" + hand);
		System.out.println(hand.typeHand());
		System.out.println();
		for(int j=0;j<HandOfCards.HAND_SIZE;j++){
			System.out.println("Card: " + j + " discard probability -> " + hand.getDiscardProbability(j));
		}

		card1 = new PlayingCard("8",'H',8,8);
		card2 = new PlayingCard("7",'H',7,7);
		card3 = new PlayingCard("6",'H',6,6);
		card4 = new PlayingCard("5",'S',5,5);
		card5 = new PlayingCard("4",'H',4,4);

		hand.setHand(card1, card2, card3, card4, card5);
		System.out.println("\n\n" + hand);
		System.out.println(hand.typeHand());
		if(hand.isBustedFlush()){
			System.out.print("+ Busted Flush");
		}
		System.out.println();
		for(int j=0;j<HandOfCards.HAND_SIZE;j++){
			System.out.println("Card: " + j + " discard probability -> " + hand.getDiscardProbability(j));
		}

		card1 = new PlayingCard("8",'H',8,8);
		card2 = new PlayingCard("8",'H',8,8);
		card3 = new PlayingCard("8",'C',8,8);
		card4 = new PlayingCard("9",'D',9,9);
		card5 = new PlayingCard("4",'S',4,4);

		hand.setHand(card1, card2, card3, card4, card5);
		System.out.println("\n\n" + hand);
		System.out.println(hand.typeHand());
		System.out.println();
		for(int j=0;j<HandOfCards.HAND_SIZE;j++){
			System.out.println("Card: " + j + " discard probability -> " + hand.getDiscardProbability(j));
		}

		card1 = new PlayingCard("8",'H',8,8);
		card2 = new PlayingCard("8",'H',8,8);
		card3 = new PlayingCard("10",'C',10,10);
		card4 = new PlayingCard("10",'D',10,10);
		card5 = new PlayingCard("9",'S',9,9);

		hand.setHand(card1, card2, card3, card4, card5);
		System.out.println("\n\n" + hand);
		System.out.println(hand.typeHand());
		System.out.println();
		for(int j=0;j<HandOfCards.HAND_SIZE;j++){
			System.out.println("Card: " + j + " discard probability -> " + hand.getDiscardProbability(j));
		}

		card1 = new PlayingCard("8",'H',8,8);
		card2 = new PlayingCard("7",'H',7,7);
		card3 = new PlayingCard("10",'C',10,10);
		card4 = new PlayingCard("10",'D',10,10);
		card5 = new PlayingCard("9",'S',9,9);

		hand.setHand(card1, card2, card3, card4, card5);
		System.out.println("\n\n" + hand);
		System.out.println(hand.typeHand());
		if(hand.isBrokenStraight()){
			System.out.print("+ Broken Straight");
		}
		System.out.println();
		for(int j=0;j<HandOfCards.HAND_SIZE;j++){
			System.out.println("Card: " + j + " discard probability -> " + hand.getDiscardProbability(j));
		}

		card1 = new PlayingCard("8",'H',8,8);
		card2 = new PlayingCard("3",'H',3,3);
		card3 = new PlayingCard("10",'H',10,10);
		card4 = new PlayingCard("10",'D',10,10);
		card5 = new PlayingCard("5",'H',5,5);

		hand.setHand(card1, card2, card3, card4, card5);
		System.out.println("\n\n" + hand);
		System.out.println(hand.typeHand());
		if(hand.isBustedFlush()){
			System.out.print("+ Busted Flush");
		}
		System.out.println();
		for(int j=0;j<HandOfCards.HAND_SIZE;j++){
			System.out.println("Card: " + j + " discard probability -> " + hand.getDiscardProbability(j));
		}

		card1 = new PlayingCard("8",'H',8,8);
		card2 = new PlayingCard("3",'H',3,3);
		card3 = new PlayingCard("10",'C',10,10);
		card4 = new PlayingCard("10",'D',10,10);
		card5 = new PlayingCard("5",'S',5,5);

		hand.setHand(card1, card2, card3, card4, card5);
		System.out.println("\n\n" + hand);
		System.out.println(hand.typeHand());
		System.out.println();
		for(int j=0;j<HandOfCards.HAND_SIZE;j++){
			System.out.println("Card: " + j + " discard probability -> " + hand.getDiscardProbability(j));
		}

		card1 = new PlayingCard("8",'H',8,8);
		card2 = new PlayingCard("9",'H',9,9);
		card3 = new PlayingCard("7",'C',7,7);
		card4 = new PlayingCard("10",'D',10,10);
		card5 = new PlayingCard("5",'S',5,5);

		hand.setHand(card1, card2, card3, card4, card5);
		System.out.println("\n\n" + hand);
		System.out.println(hand.typeHand());
		if(hand.isBrokenStraight()){
			System.out.print("+ Broken Straight");
		}
		System.out.println();
		for(int j=0;j<HandOfCards.HAND_SIZE;j++){
			System.out.println("Card: " + j + " discard probability -> " + hand.getDiscardProbability(j));
		}

		card1 = new PlayingCard("8",'H',8,8);
		card2 = new PlayingCard("3",'H',3,3);
		card3 = new PlayingCard("7",'C',7,7);
		card4 = new PlayingCard("10",'H',10,10);
		card5 = new PlayingCard("5",'H',5,5);

		hand.setHand(card1, card2, card3, card4, card5);
		System.out.println("\n\n" + hand);
		System.out.println(hand.typeHand());
		if(hand.isBustedFlush()){
			System.out.print("+ Busted Flush");
		}
		System.out.println();
		for(int j=0;j<HandOfCards.HAND_SIZE;j++){
			System.out.println("Card: " + j + " discard probability -> " + hand.getDiscardProbability(j));
		}

		card1 = new PlayingCard("8",'H',8,8);
		card2 = new PlayingCard("3",'H',3,3);
		card3 = new PlayingCard("7",'C',7,7);
		card4 = new PlayingCard("10",'D',10,10);
		card5 = new PlayingCard("5",'S',5,5);

		hand.setHand(card1, card2, card3, card4, card5);
		System.out.println("\n\n" + hand);
		System.out.println(hand.typeHand());
		System.out.println();
		for(int j=0;j<HandOfCards.HAND_SIZE;j++){
			System.out.println("Card: " + j + " discard probability -> " + hand.getDiscardProbability(j));
		}


		System.out.println("\n\n\nLOW ACE BROKEN STRAIGHTS (A,5,4,3,2): ");

		card1 = new PlayingCard("A",'H',1,14);
		card2 = new PlayingCard("A",'H',1,14);
		card3 = new PlayingCard("3",'C',3,3);
		card4 = new PlayingCard("5",'D',5,5);
		card5 = new PlayingCard("2",'S',2,2);

		hand.setHand(card1, card2, card3, card4, card5);
		System.out.println("\n\n" + hand);
		System.out.println(hand.typeHand());
		if(hand.isBrokenStraight()){
			System.out.print("+ Broken Straight");
		}
		System.out.println();
		for(int j=0;j<HandOfCards.HAND_SIZE;j++){
			System.out.println("Card: " + j + " discard probability -> " + hand.getDiscardProbability(j));
		}


		card1 = new PlayingCard("A",'H',1,14);
		card2 = new PlayingCard("5",'H',5,5);
		card3 = new PlayingCard("3",'C',3,3);
		card4 = new PlayingCard("5",'D',5,5);
		card5 = new PlayingCard("2",'S',2,2);

		hand.setHand(card1, card2, card3, card4, card5);
		System.out.println("\n\n" + hand);
		System.out.println(hand.typeHand());
		if(hand.isBrokenStraight()){
			System.out.print("+ Broken Straight");
		}
		System.out.println();
		for(int j=0;j<HandOfCards.HAND_SIZE;j++){
			System.out.println("Card: " + j + " discard probability -> " + hand.getDiscardProbability(j));
		}


		card1 = new PlayingCard("10",'H',10,10);
		card2 = new PlayingCard("A",'H',1,14);
		card3 = new PlayingCard("3",'C',3,3);
		card4 = new PlayingCard("4",'D',4,4);
		card5 = new PlayingCard("2",'S',2,2);

		hand.setHand(card1, card2, card3, card4, card5);
		System.out.println("\n\n" + hand);
		System.out.println(hand.typeHand());
		if(hand.isBrokenStraight()){
			System.out.print("+ Broken Straight");
		}
		System.out.println();
		for(int j=0;j<HandOfCards.HAND_SIZE;j++){
			System.out.println("Card: " + j + " discard probability -> " + hand.getDiscardProbability(j));
		}


		System.out.println("\n\n\n\n\n\n\nRANDOM HANDS EXAMPLES:");

		int i=0;								// Loop a few times while drawing 5 cards each time from the deck and updating the hand/checking it's new type.
		while(i<3){
			card1 = deck.dealNext();
			card2 = deck.dealNext();
			card3 = deck.dealNext();
			card4 = deck.dealNext();
			card5 = deck.dealNext();
			hand.setHand(card1, card2, card3, card4, card5);
			System.out.println("\n\n" + hand);
			System.out.println(hand.typeHand());
			if(hand.isBustedFlush()){
				System.out.print("+ Busted Flush");
			}
			else if(hand.isBrokenStraight()){
				System.out.print("+ Broken Straight");
			}
			System.out.println();
			for(int j=0;j<HandOfCards.HAND_SIZE;j++){
				System.out.println("Card: " + j + " discard probability -> " + hand.getDiscardProbability(j));
			}
			i++;
		}	
	}
	
	
	static private void testPokerPlayer(){
		DeckOfCards deck = new DeckOfCards();
		PokerPlayer testPlayer = new PokerPlayer("Sam", deck, 1000, "@Sam");
		System.out.println(testPlayer.getHand());
		System.out.println(testPlayer);
		System.out.println();
		boolean[] discard = testPlayer.getDiscard(null);
		int num = testPlayer.getHand().discardHand(discard);
		System.out.println(num + " cards have been discarded.");
		System.out.println();
		System.out.println(testPlayer.getHand());
		System.out.println(testPlayer);
		System.out.println();
		testPlayer.updateChips(250);
		System.out.println(testPlayer);
		System.out.println();
		testPlayer.updateChips(-1250);
		System.out.println(testPlayer);
	}
	
	
	static private void testHandOfPoker(){
		DeckOfCards deck = new DeckOfCards();
		PokerPlayer testPlayer = new PokerPlayer("Sam", deck, 1000, "@Sam");
		PokerPlayer testPlayer2 = new PokerPlayer("Mark", deck, 1500, "@Mark");
		PokerPlayer testPlayer3 = new PokerPlayer("Julia", deck, 750, "@Julia");
		ArrayList<PokerPlayer> players = new ArrayList<PokerPlayer>();
		players.add(testPlayer);
		players.add(testPlayer2);
		players.add(testPlayer3);
		HandOfPoker testRound = new HandOfPoker(players);
	}
	
	
	static private void testGameOfPoker(){
		DeckOfCards deck = new DeckOfCards();
		PokerPlayer testPlayer = new PokerPlayer("Sam", deck, 1000, "@Sam");
		GameOfPoker game = new GameOfPoker(testPlayer, deck, "@SamBryan801");
		game.playGame();
	}
	
	static private void testTwitterTest(){
		ArrayList<GameOfPoker> games = new ArrayList<GameOfPoker>();
		TwitterTest twitter = new TwitterTest();
	}
	
	public static void main(String[] args) {
		//testPlayingCard();
		//testDeckOfCards();
		testHandOfCards();
		//testPokerPlayer();
		//testHandOfPoker();				
		//testGameOfPoker();
		//testTwitterTest();				// CHECK IF USERS ARE FOLLOWING THE BOT.
		
		System.exit(0);
	}

}
