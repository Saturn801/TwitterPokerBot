package poker;

import java.util.Random;

public class HandOfCards {
	static public final int HAND_SIZE = 5;		// Constant to hold the number of cards in a hand.
	static public final int[] TYPE_VALUES = {0,100000000,200000000,300000000,400000000,500000000,600000000,700000000,800000000,900000000};
	// Constant values for each type of hand possible.
	private PlayingCard[] hand = new PlayingCard[HAND_SIZE];	// Array of PlayingCards in the hand.
	private DeckOfCards deck;

	public HandOfCards(DeckOfCards inputDeck) {  // This is the constructor of the class.
		deck = inputDeck;
		int i=0;
		while(i<HAND_SIZE){					// Add x number of cards to the hand from a given deck.
			hand[i] = deck.dealNext();
			i++;
		}
		this.sort();						// Then sort the hand in descending order (by face value).
	}
	private void sort(){					// A simple sort method with a basic algorithm.
		PlayingCard temp;
		for (int i = 0; i < HAND_SIZE; i++) {							
			for (int j = i+1; j < HAND_SIZE; j++) {
				if (hand[i].getGameValue() < hand[j].getGameValue()) {
					temp = hand[i];
					hand[i] = hand[j];
					hand[j] = temp;
				}
			}
		}
	}
	public DeckOfCards getDeck(){			// Returns the reference to the deck that the hand is drawing from.
		return this.deck;
	}
	public String toString(){				// Print each card in the hand in sequence.
		String string = "";
		for(int i=0;i<HAND_SIZE;i++){				// Loop for printing cards.
			string += "{" + hand[i] + "}";
		}
		string += "    " + typeHand();
		if(isBustedFlush()){
			string += " + Busted Flush";
		}
		else if(isBrokenStraight()){
			string += " + Broken Straight";
		}
		return string;
	}

	// This method I used for testing, so I could manually set the cards in the hand.
	public void setHand(PlayingCard card1, PlayingCard card2, PlayingCard card3, PlayingCard card4, PlayingCard card5){
		hand[0] = card1;
		hand[1] = card2;
		hand[2] = card3;
		hand[3] = card4;
		hand[4] = card5;
		this.sort();
	}
	
	public void setCard(int cardPosition, PlayingCard card){
		hand[cardPosition] = card;
	}
	
	public PlayingCard getCard(int cardPosition){
		return hand[cardPosition];
	}

	public int getDiscardProbability(int cardPosition){
		if(cardPosition < 0 || cardPosition > 4){	// If an invalid position is entered, just return 0.
			return 0;
		}
		if(this.getGameValue()>=TYPE_VALUES[8]){	// If it is a Straight Flush or better, keep the hand.
			return 0;
		}
		else if(this.isFourOfAKind()){		
			return this.handleFourOfAKind(cardPosition);
		}
		else if(this.getGameValue()>=TYPE_VALUES[5] && this.getGameValue()<TYPE_VALUES[7]){		// If it is between a Flush and a Full House, keep the hand.
			return 0;
		}
		else if(this.isStraight()){			// If it is a Straight, keep the hand unless it is a broken Flush (since this is the only likely scenario of improving the hand). If this is the case, have some probability of discarding the 'odd' card.
			if(this.isBustedFlush()){
				return this.handleBustedFlush(cardPosition)/2;	// Very low probability to try for Flush if already a Straight.
			}
			else{
				return 0;
			}
		}
		else if(this.isThreeOfAKind()){		// A Three of a Kind cannot be a Busted Flush or a Broken Straight; the only way to viably improve the hand would be to go to a Four of a Kind of a Full House; both of which would only be affected by the two cards not in the trio.
			return this.handleThreeOfAKind(cardPosition);
		}
		else if(this.isTwoPair()){			// A Two Pair also cannot be a Broken Straight or a Busted Flush, so the only viable way to improve it would be to go to a Full House; via discarding the 'odd' card.
			return this.handleTwoPair(cardPosition);
		}
		else if(this.isOnePair()){		// A One Pair could be both a Broken Straight or a Busted Flush, so need to check for those and handle them, otherwise deal with the One Pair on it's own.
			if(this.isBustedFlush()){
				return (this.handleBustedFlush(cardPosition)*2)%100;	// Slightly lower probabilities to try for Flush/Straight when One Pair.
			}
			else if(this.isBrokenStraight()){
				return (this.handleBrokenStraight(cardPosition)*4)%100;
			}
			else{
				return this.handleOnePair(cardPosition);
			}	
		}
		else{		// A High Hand could be both a Broken Straight or a Busted Flush, so need to check for those and handle them, otherwise deal with the High Hand on it's own.
			if(this.isBustedFlush()){
				return (this.handleBustedFlush(cardPosition)*3)%100;	// If High Hand, set higher probability to try for Flush or Straight.
			}
			else if(this.isBrokenStraight()){
				return (this.handleBrokenStraight(cardPosition)*8)%100;
			}
			else{
				return this.handleHighHand(cardPosition);
			}
		}
	}

	private int handleHighHand(int cardPosition){
		if(cardPosition==0 || cardPosition==1){		// Keep the two highest cards.
			return 0;
		}	
		else{				// Discard the three lowest cards.
			return 100;
		}
	}

	private int handleOnePair(int cardPosition){	// If it's a One Pair, keep the two cards in the pair and discard the remaining 3.
		for(int i = 0;i<HAND_SIZE;i++){
			if(i!=cardPosition && hand[i].getGameValue()==hand[cardPosition].getGameValue()){
				return 0;	// If another card with same value is found; it is in the pair, so keep it.
			}
		}
		return 100;		// Otherwise, it's a 'spare' card, so discard it.
	}

	private int handleTwoPair(int cardPosition){	// If the card at position is part of either pair; keep it. Otherwise, discard it.
		if(hand[0].getGameValue()==hand[1].getGameValue() && hand[2].getGameValue()==hand[3].getGameValue() && cardPosition!=4){
			return 0;
		}
		else if(hand[0].getGameValue()==hand[1].getGameValue() && hand[3].getGameValue()==hand[4].getGameValue() && cardPosition!=2){
			return 0;
		}
		else if(hand[1].getGameValue()==hand[2].getGameValue() && hand[3].getGameValue()==hand[4].getGameValue() && cardPosition!=0){
			return 0;
		}
		else{
			return 100;		
		}
	}

	private int handleThreeOfAKind(int cardPosition){	// If the card at position is in the trio; keep it. Otherwise, discard it.
		if(hand[0].getGameValue()==hand[2].getGameValue() && cardPosition>=0 && cardPosition<=2){
			return 0;
		}
		else if(hand[1].getGameValue()==hand[3].getGameValue() && cardPosition>=1 && cardPosition<=3){
			return 0;
		}
		else if(hand[2].getGameValue()==hand[4].getGameValue() && cardPosition>=2 && cardPosition<=4){
			return 0;
		}
		else{
			return 100;		
		}
	}

	private int handleFourOfAKind(int cardPosition){	// If it's a Four of a Kind, keep the 4 'equal' cards, and have a random possibility of discarding the remaining card.
		if(hand[0].getGameValue()==hand[1].getGameValue()){
			if(cardPosition==4){
				Random rn = new Random();
				int roll = rn.nextInt(100);
				return roll;
			}
			else{
				return 0;
			}
		}
		else{
			if(cardPosition==0){
				Random rn = new Random();
				int roll = rn.nextInt(100);
				return roll;
			}
			else{
				return 0;
			}
		}
	}

	private int handleBustedFlush(int cardPosition){
		if(!this.isOddCardInBustedFlush(cardPosition)){		// If the card is not the 'odd' one out; return 0.
			return 0;
		}
		else{		// If it is the 'odd' card; then there is a probability of discarding it.
			return Math.round(100/4);		// One in four cards will be of the suit you need (not taking into account that there are some in your hand already), and assuming that the card you need IS still in the deck.
		}
	}

	private int handleBrokenStraight(int cardPosition){
		if(!this.isOddCardInBrokenStraight(cardPosition)){	// If the card is not the 'odd' one out; return 0.
			return 0;
		}
		else{
			return Math.round(100/13);		// One in thirteen cards will be the one you need (not taking into account that there are some in your hand already), and assuming that the card you need IS still in the deck.
		}
	}

	// This method returns the game value corresponding to the given hand.
	public int getGameValue(){
		int value = 0;								// Value will be the corresponding game value.
		if(this.isRoyalFlush()){					// If statements to check for each type of hand.
			value += TYPE_VALUES[9];	// Add the constant value for that type.
			return value;							// Return value.
		}
		else if(this.isStraightFlush()){				
			value += TYPE_VALUES[8];
			value += this.straightGameValue();	// For every other type apart from royal flush we also need to check the actual cards involved, to get a more concrete game value.
			return value;
		}
		else if(this.isFourOfAKind()){						
			value += TYPE_VALUES[7];
			value += hand[2].getGameValue();		// For Four of a Kind and Full House, just add the game value of the middle card, since in both cases that will belong to either the four cards the same in four of a kind, or the three cards the same in full house.
			return value;							// The remaining one card doesn't need to be taken into account since it is impossible to have two four of a kind hands with the same four cards.
		}
		else if(this.isFullHouse()){						
			value += TYPE_VALUES[6];
			value += hand[2].getGameValue();
			return value;							// The remaining pair doesn't need to be checked since it's impossible to have two sets of three cards the same with the same value.
		}
		else if(this.isFlush()){							
			value += TYPE_VALUES[5];
			value += this.highHandGameValue();		// For the flush it is just the same as the high hand check.
			return value;
		}
		else if(this.isStraight()){		
			value += TYPE_VALUES[4];
			value += this.straightGameValue();		// This is the same as the straight flush game value so no need for a new method.
			return value;
		}
		else if(this.isThreeOfAKind()){					// As with four of a kind and full house, for three of a kind just add the game value of the middle card, since this always belongs to the set of three cards that are the same, and no it's impossible to have duplicate hands.
			value += TYPE_VALUES[3];
			value += hand[2].getGameValue();
			return value;
		}
		else if(this.isTwoPair()){							
			value += TYPE_VALUES[2];
			value += this.twoPairGameValue();
			return value;
		}
		else if(this.isOnePair()){							
			value += TYPE_VALUES[1];
			value += this.onePairGameValue();
			return value;
		}
		else{												
			value += TYPE_VALUES[0];
			value += this.highHandGameValue();
			return value;
		}
	}

	private int straightGameValue(){			// For a straight just return the game value of the first card in the straight (since this is the highest), or if that card is an ace and the next highest a 5, return the game value 5 (since it will be the straight 5,4,3,2,A).
		int card_value, value;
		if(hand[0].getGameValue()==14 && hand[1].getGameValue()==5){
			card_value = 5;
		}
		else{
			card_value = hand[0].getGameValue();
		}
		value = card_value;
		return value;
	}

	private int twoPairGameValue(){		// For two pair, it is necessary to check which cards are the pairs, and then take the game value of the pairs and the remaining constant while giving the higher pair more importance than the lower pair and the lower pair higher importance than the lone card.
		int value=0;
		if(hand[0].getGameValue()==hand[1].getGameValue() && hand[2].getGameValue()==hand[3].getGameValue()){
			value += Math.pow(hand[0].getGameValue(), 6) + Math.pow(hand[2].getGameValue(), 4) + hand[4].getGameValue();
		}
		else if(hand[0].getGameValue()==hand[1].getGameValue() && hand[3].getGameValue()==hand[4].getGameValue()){
			value += Math.pow(hand[0].getGameValue(), 6) + Math.pow(hand[3].getGameValue(), 4) + hand[2].getGameValue();
		}
		else{
			value += Math.pow(hand[1].getGameValue(), 6) + Math.pow(hand[3].getGameValue(), 4) + hand[0].getGameValue();
		}
		return value;
	}

	private int onePairGameValue(){			// For one pair it is necessary to check which two cards are the pair, then give their game values higher importance, and then the remaining three cards descending importance based on which is higher.
		int value=0;
		if(hand[0].getGameValue()==hand[1].getGameValue()){
			value += Math.pow(hand[0].getGameValue(), 5) + Math.pow(hand[2].getGameValue(), 3) + Math.pow(hand[3].getGameValue(), 2) + hand[4].getGameValue();
		}
		else if(hand[1].getGameValue()==hand[2].getGameValue()){
			value += Math.pow(hand[1].getGameValue(), 5) + Math.pow(hand[0].getGameValue(), 3) + Math.pow(hand[3].getGameValue(), 2) + hand[4].getGameValue();
		}
		else if(hand[2].getGameValue()==hand[3].getGameValue()){
			value += Math.pow(hand[2].getGameValue(), 5) + Math.pow(hand[0].getGameValue(), 3) + Math.pow(hand[1].getGameValue(), 2) + hand[4].getGameValue();
		}
		else{
			value += Math.pow(hand[3].getGameValue(), 5) + Math.pow(hand[0].getGameValue(), 3) + Math.pow(hand[1].getGameValue(), 2) + hand[2].getGameValue();
		}
		return value;
	}

	private int highHandGameValue(){		// For high hand I just give a higher power to the more valuable cards, so that they get higher priority.
		int value = 0;
		value += Math.pow(hand[0].getGameValue(), 5) + Math.pow(hand[1].getGameValue(), 4) + Math.pow(hand[2].getGameValue(), 3) + Math.pow(hand[3].getGameValue(), 2) + hand[4].getGameValue();
		return value;
	}

	// This method I added for simplicity to check whether all the cards in the hand are of the same suit.
	private boolean isSameSuit(){
		if(hand[0].getSuit() == hand[1].getSuit() && hand[0].getSuit() == hand[2].getSuit() && hand[0].getSuit() == hand[3].getSuit() && hand[0].getSuit() == hand[4].getSuit()){
			return true;
		}
		else{
			return false;
		}
	}

	// This methods checks whether the hand is a busted flush; if it has 4 cards with the same suit and the remaining card of another suit.
	public boolean isBustedFlush(){
		int totalhearts = 0, totalclubs = 0, totalspades = 0, totaldiamonds = 0;
		for(int i=0;i<HAND_SIZE;i++){
			if(hand[i].getSuit()=='H'){
				totalhearts++;
			}
			else if(hand[i].getSuit()=='C'){
				totalclubs++;
			}
			else if(hand[i].getSuit()=='S'){
				totalspades++;
			}
			else{
				totaldiamonds++;
			}
		}
		if(totalhearts==4 || totalclubs==4 || totalspades==4 || totaldiamonds==4){
			return true;
		}
		else{
			return false;
		}
	}

	// Method for checking if the card at a certain index (when the hand is a Busted Flush) is the 'odd' card (the one of different suit).
	private boolean isOddCardInBustedFlush(int cardPosition){
		if(!this.isBustedFlush()){	// If it's not a Busted Flush.
			return false;
		}
		for(int i=0;i<HAND_SIZE;i++){
			if(i!=cardPosition && hand[i].getSuit()==hand[cardPosition].getSuit()){		// If another card is found with the same suit, it is not the odd one out; return false.
				return false;
			}
		}
		return true;	// If no other cards with the same suit are found, then the card is the odd one out; return true.
	}

	private boolean isOddCardInBrokenStraight(int cardPosition){
		if(this.typeOfBrokenStraight()==1){		// 4-sequence; check the end cards to see if they're part of the sequence.
			if((cardPosition==0 && hand[0].getGameValue()!=hand[1].getGameValue()+1) || (cardPosition==4 && hand[4].getGameValue()!=hand[3].getGameValue()-1)){
				return true;
			}
			else{
				return false;
			}
		}
		// For the 3-sequence ones; need to check the 2 remaining cards and compare them to either 'edge' of the sequence.
		else if(this.typeOfBrokenStraight()==2){	// 3-sequence (first three);
			if(cardPosition==3 && hand[3].getGameValue()!=hand[2].getGameValue()-2){
				return true;
			}
			else if(cardPosition==4){
				if(hand[4].getGameValue()==hand[2].getGameValue()-1 || hand[4].getGameValue()==hand[2].getGameValue()-2){
					return false;
				}
				else{
					return true;
				}
			}
			else{
				return false;
			}
		}
		else if(this.typeOfBrokenStraight()==3){	// 3-sequence (middle three);
			if((cardPosition==0 && hand[0].getGameValue()!=hand[1].getGameValue()+2) || (cardPosition==4 && hand[4].getGameValue()!=hand[3].getGameValue()-2)){
				return true;
			}
			else{
				return false;
			}
		}
		else if(this.typeOfBrokenStraight()==4){	// 3-sequence (last three);
			if(cardPosition==1 && hand[2].getGameValue()!=hand[2].getGameValue()+2){
				return true;
			}
			else if(cardPosition==0){
				if(hand[0].getGameValue()==hand[2].getGameValue()+1 || hand[0].getGameValue()==hand[2].getGameValue()+2){
					return false;
				}
				else{
					return true;
				}
			}
			else{
				return false;
			}
		}
		else if(this.typeOfBrokenStraight()==5){	// Two x 2-sequence with first four cards;
			if(cardPosition==4){	// The last card is the odd one out.
				return true;
			}
			else{
				return false;
			}
		}
		else if(this.typeOfBrokenStraight()==6){	// Two x 2-sequence with first two and last two;
			if(cardPosition==2){	// The middle card is the odd one out.
				return true;
			}
			else{
				return false;
			}
		}
		else if(this.typeOfBrokenStraight()==7){	// Two x 2-sequence with last four cards;
			if(cardPosition==0){	// The first card is the odd one out.
				return true;
			}
			else{
				return false;
			}
		}
		else if(this.isAceLowBrokenStraight()){
			String cards[] = {"A","5","4","3","2"};	// Array containing the cards that should be in the straight.
			if(!this.isOnePair()){	// If there isn't a One Pair, it suffices to check if the card at position is one of the ones in the array, if it is; it's not the odd one out.
				for(int i=0;i<cards.length;i++){
					if(hand[cardPosition].getType()==cards[i]){	
						return false;
					}
				}
				return true;
			}
			else{	// If it is a One Pair, then just return true for one of the two cards in the pair.
				if(cardPosition!=4){
					if(hand[cardPosition].getGameValue()==hand[cardPosition+1].getGameValue()){
						return true;
					}
					else{
						return false;
					}
				}
				else{
					return false;
				}
			}
		}
		else{		// If it's not a Broken Straight.
			return false;
		}
	}

	// This method uses the typeOfBrokenStraight method to check if a given hand is a Broken Straight or not.
	public boolean isBrokenStraight(){
		if(this.typeOfBrokenStraight()!=0 || this.isAceLowBrokenStraight()){		// 1 is 4-sequence, 2,3,4 are 3-sequence and higher/lower card and 5,6,7 are two sets of 2-sequence that are 1 apart.
			return true;
		}
		else{
			return false;
		}
	}

	// This method deals with the specific case where the Broken Straight is the ace low one; A,5,4,3,2.
	private boolean isAceLowBrokenStraight(){
		String cards[] = {"A","5","4","3","2"};	// Array containing the cards that should be in the straight.
		int numOfDistinctCards=0;
		for(int i=0;i<HAND_SIZE;i++){				// Loop through the cards and check if they're the same as any of the cards from the array.
			for(int j=0;j<cards.length;j++){
				if(hand[i].getType()==cards[j]){	// If they are, increment the numOfDistinctCards, and set that position in the array to null (we don't care about duplicates).
					numOfDistinctCards++;
					cards[j]="";
				}
			}
		}
		if(numOfDistinctCards==4){		// If by the end there are 4 distinct cards from that array, then we have the Broken Straight of A,5,4,3,2.
			return true;
		}
		else{
			return false;
		}
	}

	// This method checks if the type of a Broken Straight (for example, if sequence of 4, not a broken straight, sequence of 3 and one card higher/lower, etc).
	private int typeOfBrokenStraight(){
		int sequence, sum, j;
		for(int i=0;i<HAND_SIZE;i++){	// Loop through and find sequences of cards that are in descending order.
			sequence=1;
			sum=1;
			for(j=i+1;j<HAND_SIZE;j++){
				if(hand[j].getGameValue()==hand[i].getGameValue()-sum){
					sequence++;
					sum++;
				}
				else{
					break;	// Break at position j where the sequence fails.
				}
			}
			if(sequence==5){	// This is a Straight.
				return 0;
			}
			else if(sequence==4){	// Easy to find Broken Straight.
				return 1;
			}
			else if(sequence==3){	// If there's three in sequence, need to check remaining cards.
				if(hand[0].getGameValue()==hand[1].getGameValue()+1 && hand[1].getGameValue()==hand[2].getGameValue()+1){	// If the first three are in sequence.
					if(hand[3].getGameValue()==hand[2].getGameValue()-2 || hand[4].getGameValue()==hand[2].getGameValue()-1 || hand[4].getGameValue()==hand[2].getGameValue()-2){	// If either of the other two cards are one or two higher than the highest card in the sequence or one or two lower than the lowest card in the sequence (so they would be the start/end).
						return 2;
					}
					else{
						return 0;
					}
				}
				else if(hand[1].getGameValue()==hand[2].getGameValue()+1 && hand[2].getGameValue()==hand[3].getGameValue()+1){	// If the middle three are in sequence.
					if(hand[0].getGameValue()==hand[1].getGameValue()+2 || hand[4].getGameValue()==hand[3].getGameValue()-2){	// If either of the other two cards are one or two higher than the highest card in the sequence or one or two lower than the lowest card in the sequence (so they would be the start/end).
						return 3;
					}
					else{
						return 0;
					}
				}
				else{	// If the last three are in sequence.
					if(hand[0].getGameValue()==hand[2].getGameValue()+1 || hand[0].getGameValue()==hand[2].getGameValue()+2 || hand[1].getGameValue()==hand[2].getGameValue()+2){	// If either of the other two cards are one or two higher than the highest card in the sequence or one or two lower than the lowest card in the sequence (so they would be the start/end).
						return 4;
					}
					else{
						return 0;
					}
				}
			}
			else if(sequence==2){	// If there's two in the sequence, we need to check for another sequence.
				int k, sequence2, sum2;
				for(k=j;k<HAND_SIZE;k++){	
					sequence2=1;
					sum2=1;
					for(int l=k+1;l<HAND_SIZE;l++){
						if(hand[l].getGameValue()==hand[k].getGameValue()-sum2){
							sequence2++;
							sum2++;
						}
						else{
							break;	// Break at position j where the sequence fails.
						}
					}
					if(sequence2==2){	// If there's another sequence of length 2, check the difference between them (if there is only 1 number in between, it is a Broken Straight).
						if(hand[1].getGameValue()==hand[0].getGameValue()-1 && hand[3].getGameValue()==hand[2].getGameValue()-1){	// If the two sequences are in the first 4 cards.
							if(hand[1].getGameValue()==hand[2].getGameValue()+2){
								return 5;
							}
							else{
								return 0;
							}
						}
						else if(hand[1].getGameValue()==hand[0].getGameValue()-1 && hand[4].getGameValue()==hand[3].getGameValue()-1){	// If the first two cards and the last two cards are in sequence.
							if(hand[1].getGameValue()==hand[3].getGameValue()+2){
								return 6;
							}
							else{
								return 0;
							}
						}
						else{	// If the two sequences are in the last 4 cards.
							if(hand[2].getGameValue()==hand[3].getGameValue()+2){
								return 7;
							}
							else{
								return 0;
							}
						}
					}
				}
			}
		}
		return 0;
		/*
		 *  POSSIBLE COMBINATIONS:
		 * 	A,A,K,Q,J	Pair at start
		 * 	A,K,K,Q,J	Pair in middle
		 * 	A,K,Q,J,J	Pair at end
		 * 
		 * 	Pairs should be variations of others below
		 * 
		 * 	10,6,5,4,3	4-sequence at end
		 * 	7,6,5,4,2	4-sequence at start
		 * 	10,6,4,3,2	3-sequence and gap (left/right)
		 * 	10,9,7,6,3	2 sets of 2-sequence with 1 in between
		 */
	}

	// Because a royal flush is just one specific hand, I checked whether the values were the same.
	public boolean isRoyalFlush(){
		if(this.isSameSuit() && hand[0].getGameValue()==14 && hand[1].getGameValue()==13 && hand[2].getGameValue()==12 && hand[3].getGameValue()==11 && hand[4].getGameValue()==10){
			return true;
		}
		else{
			return false;
		}
	}
	// For this method I just checked if each consecutive card has a face value of one lower than the last card, and since my ace is at the start, I have to check the special case where it's 5,4,3,2,A.
	public boolean isStraightFlush(){
		if(this.isSameSuit() && !this.isRoyalFlush() && ((hand[1].getGameValue()==hand[0].getGameValue()-1 && hand[2].getGameValue()==hand[1].getGameValue()-1 && hand[3].getGameValue()==hand[2].getGameValue()-1 && hand[4].getGameValue()==hand[3].getGameValue()-1) || hand[2].getGameValue()==hand[1].getGameValue()-1 && hand[3].getGameValue()==hand[2].getGameValue()-1 && hand[4].getGameValue()==hand[3].getGameValue()-1 && hand[0].getGameValue()==14 && hand[1].getGameValue()==5)){
			return true;
		}
		else{
			return false;
		}
	}
	// This method is fairly simple; if either the first and fourth cards are the same, or if the second and fifth cards are the same.
	public boolean isFourOfAKind(){
		if(hand[0].getGameValue()==hand[3].getGameValue() || hand[1].getGameValue()==hand[4].getGameValue()){
			return true;
		}
		else{
			return false;
		}
	}
	// A full house consists of both a three of a kind, and a pair together in one hand, so I just check for two varieties of this; the pair is at the start and the three of a kind the end, or the other way around.
	public boolean isFullHouse(){
		if((hand[0].getGameValue()==hand[2].getGameValue() && hand[3].getGameValue()==hand[4].getGameValue()) || (hand[0].getGameValue()==hand[1].getGameValue() && hand[2].getGameValue()==hand[4].getGameValue())){
			return true;
		}
		else{
			return false;
		}
	}
	// For a flush I just need to check if the suits are the same, and it's none of the above hands.
	public boolean isFlush(){
		if(this.isSameSuit() && !this.isRoyalFlush() && !this.isStraightFlush() && !this.isFourOfAKind() && !this.isFullHouse()){
			return true;
		}
		else{
			return false;
		}
	}
	// For straight, I needed to have the 'special' check for when the ace is at the end of the straight (5,4,3,2,A), and then have the other check for the rest of them, where the face value of each consecutive card is one less than the previous one).
	public boolean isStraight(){
		if(!this.isSameSuit() && ((hand[1].getGameValue()==hand[0].getGameValue()-1 && hand[2].getGameValue()==hand[1].getGameValue()-1 && hand[3].getGameValue()==hand[2].getGameValue()-1 && hand[4].getGameValue()==hand[3].getGameValue()-1) || (hand[2].getGameValue()==hand[1].getGameValue()-1 && hand[3].getGameValue()==hand[2].getGameValue()-1 && hand[4].getGameValue()==hand[3].getGameValue()-1 && hand[0].getGameValue()==14 && hand[1].getGameValue()==5))){
			return true;
		}
		else{
			return false;
		}
	}
	// The first thing to check with a three of a kind is that it's not a four of a kind, and then after that I just check the three 'possibilities' for them; the three first cards are the same, the three middle, or the three last.
	public boolean isThreeOfAKind(){
		if(!this.isFourOfAKind() && ((hand[0].getGameValue()==hand[2].getGameValue()) || (hand[1].getGameValue()==hand[3].getGameValue()) || (hand[2].getGameValue()==hand[4].getGameValue()))){
			return true;
		}
		else{
			return false;
		}
	}
	// For two pair you also need to check that it's not a four of a kind or a full house, and then check every possibility again.
	public boolean isTwoPair(){
		if(!this.isFullHouse() && !this.isFourOfAKind() && ((hand[0].getGameValue()==hand[1].getGameValue() && hand[2].getGameValue()==hand[3].getGameValue()) || (hand[0].getGameValue()==hand[1].getGameValue() && hand[3].getGameValue()==hand[4].getGameValue()) || (hand[1].getGameValue()==hand[2].getGameValue() && hand[3].getGameValue()==hand[4].getGameValue()))){
			return true;
		}
		else{
			return false;
		}
	}
	// In this case we check that it's not a two pair (and therefore also not a four/three of a kind), then just check every option for a pair.
	public boolean isOnePair(){
		if(!this.isTwoPair() && ((hand[0].getGameValue()==hand[1].getGameValue()) || (hand[1].getGameValue()==hand[2].getGameValue()) || (hand[2].getGameValue()==hand[3].getGameValue()) || (hand[3].getGameValue()==hand[4].getGameValue()))){
			return true;
		}
		else{
			return false;
		}
	}
	// High hand just returns true if every other method returns false.
	public boolean isHighHand(){
		if(!this.isFlush() && !this.isFourOfAKind() && !this.isFullHouse() && !this.isOnePair() && !this.isRoyalFlush() && !this.isStraight() && !this.isStraightFlush() && !this.isThreeOfAKind() && !this.isTwoPair()){
			return true;
		}
		else{
			return false;
		}
	}
	public String compareTo(HandOfCards hand){
		if (this.getGameValue() > hand.getGameValue()){
			return "First hand is greater.";
		}
		else if (this.getGameValue() < hand.getGameValue()){
			return "Second hand is greater.";
		}
		else{
			return "Both hands are of equal value.";
		}
	}

	// This method will discard the cards from the hand and replace them, returning the number of discarded cards.
	public int discardHand(boolean[] discard){
		int numDiscarded = 0;
		for(int i=0;i<HAND_SIZE;i++){
			if(discard[i]==true){
				deck.returnCard(hand[i]);
				hand[i] = deck.dealNext();
				numDiscarded++;		
			}
		}
		this.sort();
		return numDiscarded;
		/*
		int[] probabilities = new int[5];
		int numDiscarded = 0, roll;
		Random rn = new Random();
		for(int i=0;i<HAND_SIZE;i++){		// This loop gets the probabilities for each card (if this was done in the other loop, then they would change once a new card was drawn, so they are stored).
			probabilities[i] = this.getDiscardProbability(i);
		}
		for(int i=0;i<HAND_SIZE;i++){
			roll = rn.nextInt(101);
			if(probabilities[i]!=0 && roll<=probabilities[i]){	// If it has a probability to discard, and the random number is lower or equal than that probability, then discard it.
				deck.returnCard(hand[i]);
				hand[i] = deck.dealNext();
				numDiscarded++;
			}
		}
		this.sort();
		return numDiscarded;
		*/
	}
	
	// METHOD FOR EASE OF TESTING
	public String typeHand(){
		if(this.isRoyalFlush()){
			return "Royal Flush";
		}
		else if(this.isStraightFlush()){
			return "Straight Flush";
		}
		else if(this.isFourOfAKind()){
			return "Four of a Kind";
		}
		else if(this.isFullHouse()){
			return "Full House";
		}
		else if(this.isFlush()){
			return "Flush";
		}
		else if(this.isStraight()){
			return "Straight";
		}
		else if(this.isThreeOfAKind()){
			return "Three of a Kind";
		}
		else if(this.isTwoPair()){
			return "Two Pair";
		}
		else if(this.isOnePair()){
			return "One Pair";
		}
		else{
			return "High Hand";
		}
	}
}
