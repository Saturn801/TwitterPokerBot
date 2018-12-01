package poker;

import java.util.Random;
import java.util.ArrayList;

public class DeckOfCards {
	static public final int DECK_SIZE = 52;
	private ArrayList<PlayingCard> deck = new ArrayList<PlayingCard>();		// I used an ArrayList to store the cards rather than an array to make it easier to remove/add cards to and from it.
	private int counter;		// Counter is an integer that lets the class know how many of the "original" cards have been dealt, and when it can move onto the discarded ones at the end.

	public DeckOfCards() {  // This is the constructor of the class.
		this.reset();
	}
	public void reset(){		// Reset is called whenever the deck needs to be restarted to the original setting (52 cards, one of each type).
		deck = new ArrayList<PlayingCard>();		// Re-initialize the ArrayList.
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
				deck.add(card);		// Create a card object with appropriate variables and add to ArrayList.
			}
		}
		counter = DECK_SIZE;
		this.shuffle();		// Shuffle the deck after resetting for convenience not having to call it afterwards.
	}
	public void shuffle(){			// Shuffle randomly swaps pairs of cards from the deck a high enough number of times so that it is adequately shuffled.
		Random rn = new Random();
		int index;
		PlayingCard temp1, temp2;
		int loop=deck.size()*deck.size();		// Do this a number of times relative to the size of the deck to be shuffled.
		while(loop>0){
			for(int i=0;i<deck.size();i++){			// Loop to swap pairs of cards.
				index = rn.nextInt(deck.size());
				temp1 = deck.get(i);
				temp2 = deck.get(index);
				deck.set(i,temp2);
				deck.set(index,temp1);
			}
			loop--;
		}
	}
	public synchronized PlayingCard dealNext(){		// Deals the next card on top of the deck to a player.
		PlayingCard nextCard = null;		// Initialized to null in case of no cards remaining.
		if(counter==0 && deck.size()!=1){		// If the original 52 cards have been dealt but the size is not 0, that means that some cards have been discarded, and can be shuffled and continue to deal them.
			System.out.println("\nDeck is empty. Moving onto discarded cards:");
			this.shuffle();
			counter = deck.size();
		}
		try{  		// If this try/catch fails, this means that no cards are left in the deck, so print an exception and return null.
			nextCard = deck.remove(1);  
		}catch(IndexOutOfBoundsException e){System.out.println("\nDeck is empty. No discarded cards remaining.");} 
		counter--;
		return nextCard;
	}
	public synchronized void returnCard(PlayingCard discarded){		// This method adds a returned card onto the end of the deck, and prints which card it was (only for testing purposes for now).
		if(discarded==null){
			System.out.println("Error! Returned card is null.");
			return;
		}
		System.out.println(discarded + " has been returned to the deck.");
		deck.add(discarded);
	}
	public String toString(){		// I added a toString method to easily check the cards in a deck.
		String string = "";
		for(int i=0;i<deck.size();i++){				// Loop for printing cards.
			string += "{" + deck.get(i) + "}";
		}
		return string;
	}
}
