package poker;

import java.util.Random;

/** <p>	Poker Player AI
 * <br> Determines behaviour of the AI players. Behaviour governed by AI's 'skill' level. */
public class AutomatedPokerPlayer extends PokerPlayer {
	
	private int discardLevel; 		// acceptable discard probability. Higher being less likely to discard.
	private int bluffLevel; 		// % of funds invested at which AI is willing to bluff. Higher being less likely to bluff. Advisable 50 - 70
	private int riskLevel; 			// acceptable risk. Higher being less likely to take risk). Advisable 6 - 20
	private int tooRichLevel; 		// % of remaining funds at which amount required is too much. Higher means AI will stay in longer. Advisable 20 - 80
	private int callLevel;			// % of funds willing to be committed to any single hand. Higher means AI will stay in longer. Advisable 50 - 100
	private int aggressionValue;	// value of AI's aggressiveness. Values from 1 to 3, least to most aggressive.
	private int nukeLevel;		 	// % of funds invested at which the AI will go all in. Higher means the AI will wait longer. Advisable 70 - 90
	private int nukeValue;			// strength of hand acceptable to go all in with. Advisable 60 - 80.
	private static final int WEAK_RISK_MOD = 2;		// risk modifier for weak hands
	private static final int WEAK_CALL_MOD = 2;		// call modifier for weak hands
	private static final int RAISE_CAP = 2;			// maximum raise cap modifier
	
	public AutomatedPokerPlayer(String playerName, DeckOfCards deck, int numChips) 
	{	
		super(playerName, deck, numChips, "0");
		AItype();
	}
	
	/** Sets the AI's variables */
	private void AItype(){
		Random rand = new Random();
		switch(rand.nextInt(3)+1){
		case 1: // pro
			discardLevel = 20;
			bluffLevel = 70;
			riskLevel = 20;
			tooRichLevel = 30;
			callLevel = 100;
			aggressionValue = 3;
			nukeLevel = 70;
			nukeValue = 65;
			break;
		case 2: // casual
			discardLevel = 15;
			bluffLevel = 80;
			riskLevel = 14;	
			tooRichLevel = 50;
			callLevel = 75;
			aggressionValue = 2;
			nukeLevel = 80;
			nukeValue = 70;
			break;
		case 3: // scrub
			discardLevel = 5;
			bluffLevel = 50;
			riskLevel = 6;
			tooRichLevel = 80;
			callLevel = 50;
			aggressionValue = 1;
			nukeLevel = 90;
			nukeValue = 80;
			break;
		default: // matches casual
			discardLevel = 10;
			bluffLevel = 80;
			riskLevel = 10;	
			tooRichLevel = 60;
			callLevel = 75;
			aggressionValue = 2;
			nukeLevel = 80;
			nukeValue = 70;
			break;
		}
	}
	
	/** <p> 	Determines discard
	 * @param	input The human player's input, not used by the AI.
	 * @return 	A boolean array on whether to discard that card in that relative index or not. */
	public boolean[] getDiscard(String input){
		int[] probabilities = new int[5];
		boolean[] discard = new boolean[5];

		for(int i=0; i<HandOfCards.HAND_SIZE; i++){
			probabilities[i] = hand.getDiscardProbability(i);
		}

		for(int i=0; i<HandOfCards.HAND_SIZE; i++){
			if(discardLevel<=probabilities[i]){	
				discard[i] = true;
			}
			else{
				discard[i] = false;
			}
		}
		return discard;
	}
	
	/** <p> 	Determines AI betting
	 * @param	input The human player's input, not used by the AI.
	 * @param	currentHandBet The current total bet of the round.
	 * @return 	A string array composed as follows:
	 * <br>		index 0 : "fold", "all-in", "call", or "raise"
	 * <br>		index 1 : if index 0 is 'raise' contains a number (as a string), else empty string. */
	public String[] getBet(String input, int currentHandBet){
		String[] bet = new String[2];
		int handValue = hand.getGameValue()/10000000; // 2 digit hand value
		int raise = 0;
	
		if (fold(handValue, currentHandBet) && !bluff(handValue)){
			// folding, not willing to bluff
			bet[0]="fold"; bet[1]="";
			return bet;	
		}
		
		// from this point onwards AI WILL be betting
		if (currentHandBet-currentBet > chips){
			bet[0]="all-in"; bet[1]="";
			return bet;	
		}
		
		// from here onwards AI can meet the current bet
		if (handValue < riskLevel*WEAK_RISK_MOD){ // AI has a weak hand
			if ((currentBet*100)/(chips+currentBet) > callLevel/WEAK_CALL_MOD){ // stacks out, call
				bet[0]="call"; bet[1]="";
				return bet;	
			} else { // stack remaining, raise
				
				// calc raise amount
				raise=Math.min((currentHandBet-currentBet)*RAISE_CAP, Math.max(currentHandBet-currentBet,(((handValue*aggressionValue)+5)/10)*10));
				if (raise == 0){ // a raise of 0 is a call.
					bet[0]="call"; bet[1]="";
					return bet;	
				}
				if (raise + (currentHandBet-currentBet) > chips){ // raise amount greater than remaining stack, so go all-in
					bet[0]="all-in"; bet[1]="";
					return bet;
				} else {
					bet[0]="raise"; bet[1]=Integer.toString(raise); // else finally, raise by amount
					return bet;
				}
			}
		} else { // AI has a strong hand
			if ((currentBet*100)/(chips+currentBet) > callLevel){
				// purely here to lengthen game
				bet[0]="call"; bet[1]="";
				return bet;	
			} else { // nuke or raise
				if ( ((currentBet*100)/(chips+currentBet) > nukeLevel) && (handValue >= nukeValue)){
					// sufficiently strong to go all in
					bet[0]="all-in"; bet[1]="";
					return bet;
				}
				
				// calc raise amount
				raise=Math.min((currentHandBet-currentBet)*RAISE_CAP, Math.max(currentHandBet-currentBet,(((handValue*aggressionValue)+5)/10)*10));
				if (raise == 0){ // a raise of 0 is a call
					bet[0]="call"; bet[1]="";
					return bet;	
				}
				if (raise + (currentHandBet-currentBet) > chips){ // raise amount greater than remaining stack, so go all-in
					bet[0]="all-in"; bet[1]="";
					return bet;
				} else {
					bet[0]="raise"; bet[1]=Integer.toString(raise); // else finally, raise by amount
					return bet;
				}
			}
		}
	}
	
	/** <p>		Determines whether the AI should fold
	 * <br>		AI will fold if:
	 * <br>		Hand value is simply too low for their taste, or
	 * <br>		Hand value is not sufficiently high AND amount required to stay in is too high
	 * 
	 * @param 	handValue - The strength of the AI's hand
	 * @param 	currentHandBet - The current total bet of the round.
	 * @return  <b>true</b> if AI should fold
	 */
	private boolean fold(int handValue, int currentHandBet){
		
		return ((handValue < riskLevel) || ((handValue < riskLevel*WEAK_RISK_MOD) && (((currentHandBet-currentBet)*100)/chips > tooRichLevel)))? true : false; 
	}
	
	/** <p>		Determines whether the AI should bluff
	 * <br>		AI will bluff if:
	 * <br>		Hand value is not sufficiently high AND AI is already significantly invested
	 * 
	 * @param 	handValue - The strength of the AI's hand
	 * @return  <b>true</b> if AI should bluff
	 */
	private boolean bluff(int handValue){
		
		return ((handValue < riskLevel*WEAK_RISK_MOD) && ((currentBet*100)/(chips+currentBet) > bluffLevel))? true : false;
	}  
}
