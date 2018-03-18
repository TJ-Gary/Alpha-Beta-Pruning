import java.util.LinkedList;


public class AI_alpha_beta_pruning{
	static int iteration = 0;
	static int alphaCut = 0;
	static int betaCut = 0;
	static int [] fixPruning = {0,1,2,3,4,5,6,7,8};
	static int [] initialKiller = {4,0,2,6,8,1,3,5,7};
	static LinkedList<Integer> numUsed = new LinkedList<Integer>();
	static LinkedList<Integer> killer = new LinkedList<Integer>();

	public static void main(String[] args) {
		String input = new String("OXX___O__");
		//TTT game = new TTT(args[0]);
		int depth = 0;
		if (game.whoGoNext() == 'O') depth = 1;
		System.out.println("Running without alpha-beta pruning");
		System.out.println("Game Result: " + exploreAll(game,depth));
		System.out.println("Moves considered without alpha-beta pruning: " + iteration);
		System.out.println("\n___________________________________________\n");
		iteration = 0;
		System.out.println("Running with alpha-beta pruning");
		System.out.println("\nGame Result: " + exploreWithPruningEntry(game,depth));
		System.out.println("Moves considered with alpha-beta pruning: " + iteration);
		System.out.println("Alpha cuts: " + alphaCut);
		System.out.println("Beta cuts: " + betaCut);
		System.out.println("\n___________________________________________\n");

		iteration = 0;
		alphaCut = 0;
		betaCut = 0;
		for (int i = 0; i < 9; i++){
			numUsed.addLast(new Integer(0));
			killer.addLast(new Integer(initialKiller[i]));
		}

		System.out.println("Running with the killer heuristic");
		System.out.println("\nGame Result: " + exploreWithKillerEntry(game,depth));
		System.out.println("Moves considered with alpha-beta pruning: " + iteration);
		System.out.println("Alpha cuts: " + alphaCut);
		System.out.println("Beta cuts: " + betaCut);
		
		/*
		for (int i = 0; i < 9; i++) System.out.print(numUsed.get(i).intValue()+" ");
		System.out.println();
		for (int i = 0; i < 9; i++) System.out.print(killer.get(i).intValue()+" ");
		*/
	}
	
	public static int exploreAll(TTT game, int depth) {
		iteration++;
		if (game.whoWin() != -2) return game.whoWin();
		if (depth % 2 == 0) game.setHValue(-9);
		else game.setHValue(9);
		
		TTT nextGame;
		int nextHValue;
		for (int i = 0; i < 9; i++) {
			if (game.getValue(i) == '_'){
				nextGame = game.copy();
				nextGame.setBoardChar(i,game.whoGoNext());
				nextHValue = exploreAll(nextGame, depth+1);				
				if (depth % 2 == 0){
					if (game.getHValue() < nextHValue)
						game.setHValue(nextHValue);
				}
				else{
					if (game.getHValue() > nextHValue)
						game.setHValue(nextHValue);
				}
			}			
		}
		return game.getHValue();		
	}
	public static int exploreWithPruningEntry(TTT game, int depth) {
		int rootPreHValue = 9;
		if (game.whoGoNext() == 'O') rootPreHValue = -9;
		return exploreWithPruning(game, depth, rootPreHValue);
	}
	public static int exploreWithPruning(TTT game, int depth, int preHValue) {
		iteration++;
		if (game.whoWin() != -2) return game.whoWin();
		if (depth % 2 == 0) game.setHValue(-9);
		else game.setHValue(9);
		
		TTT nextGame;
		int nextHValue;
		boolean cut;
		for (int i = 0; i < 9; i++) {
			if (game.getValue(fixPruning[i]) == '_'){
				cut = false;
				
				if (depth % 2 == 0) {
					if (game.getHValue() != -9 && preHValue != 9 && preHValue < game.getHValue()){
						game.printBoard();
						System.out.println("Beta Cut");
						cut = true;
						betaCut++;
						i = 9;
					}
				}
				else { 
					if (game.getHValue() != 9 && preHValue != -9 && preHValue > game.getHValue()) {
						game.printBoard();
						System.out.println("Alpha Cut");
						cut = true;
						alphaCut++;
						i = 9;
					}
				}
				
				if (!cut){
					nextGame = game.copy();
					nextGame.setBoardChar(fixPruning[i],game.whoGoNext());
					nextHValue = exploreWithPruning(nextGame, depth+1, game.getHValue());				
					if (depth % 2 == 0){
						if (game.getHValue() < nextHValue)
							game.setHValue(nextHValue);
					}
					else{
						if (game.getHValue() > nextHValue)
							game.setHValue(nextHValue);
					}
				}
			}			
		}
		return game.getHValue();		
	}
	public static int exploreWithKillerEntry(TTT game, int depth) {
		int rootPreHValue = 9;
		if (game.whoGoNext() == 'O') rootPreHValue = -9;
		return exploreWithKiller(game, depth, rootPreHValue);
	}
	public static int exploreWithKiller(TTT game, int depth, int preHValue){
		iteration++;
		if (game.whoWin() != -2) return game.whoWin();
		if (depth % 2 == 0) game.setHValue(-9);
		else game.setHValue(9);
		
		TTT nextGame;
		int nextHValue;
		boolean cut;
		Integer temp;

		for (int i = 0; i < 9; i++) {
			LinkedList<Integer> newKiller = new LinkedList<Integer>();
			for (int j = 0; j < 9; j++) {
				newKiller.addLast(killer.get(j).intValue());
			}

			if (game.getValue(newKiller.get(i).intValue()) == '_'){
				cut = false;
				
				if (depth % 2 == 0) {
					if (game.getHValue() != -9 && preHValue != 9 && preHValue < game.getHValue()){
						game.printBoard();
						System.out.println("Beta Cut");
						cut = true;
						betaCut++;
					}
				}
				else { 
					if (game.getHValue() != 9 && preHValue != -9 && preHValue > game.getHValue()) {
						game.printBoard();
						System.out.println("Alpha Cut");
						cut = true;
						alphaCut++;
					}
				}
				
				if (cut){
					temp = numUsed.get(i).intValue()+1;
					numUsed.set(i, temp);
					i = 9;
				}
				else{
					nextGame = game.copy();
					nextGame.setBoardChar(newKiller.get(i).intValue(),game.whoGoNext());
					nextHValue = exploreWithKiller(nextGame, depth+1, game.getHValue());				
					if (depth % 2 == 0){
						if (game.getHValue() < nextHValue)
							game.setHValue(nextHValue);
					}
					else{
						if (game.getHValue() > nextHValue)
							game.setHValue(nextHValue);
					}
				}
				
			}			
		}
		/*
		for (int j = 0; j < 9; j++){
			for (int k = 0; k < 8; k++){
				if (numUsed.get(k) < numUsed.get(k+1)){
					temp = numUsed.get(k);
					numUsed.set(k, numUsed.get(k+1));
					numUsed.set(k+1, temp);
					temp = killer.get(k);
					killer.set(k, killer.get(k+1));
					killer.set(k+1, temp);
				}
			}
		}
		*/
		return game.getHValue();		

	}
	
}

class TTT { //Tic-tac-toe
	private char[] board;
	private int heuristicValue;
	
	public TTT(String input){
		board = input.toCharArray();
	}
	
	public char[] getBoardChar() { 
		return board; 
	}
	
	public void setBoardChar(int position, char xo) {
		board[position] = xo;
	}
	
	public char getValue(int index) {
		return board[index];
	}
	
	public int getHValue() {
		return heuristicValue;
	}
	
	public void setHValue(int value) {
		heuristicValue = value;
	}
	
	public TTT copy(){
		return new TTT(new String(board));
	}
	
	public int whoWin() {	//return -2:not yet, 1: X win, 0: draw, -1: O win
		for (int i = 0; i < 3; i++) {
			// check three rows
			if (board[i*3] == board[i*3+1] && board[i*3] == board[i*3+2]){
				if (board[i*3] == 'X') return 1;
				else if (board[i*3] == 'O') return -1;
			}
			// check three columns
			if (board[i] == board[i+3] && board[i] == board[i+6]){
				if (board[i] == 'X') return 1;
				else if (board[i] == 'O') return -1;
			}
		}
		// check two diagonals
		if (board[0] == board[4] && board[0] == board[8]){
			if (board[0] == 'X') return 1;
			else if (board[0] == 'O') return -1;
		}
		if (board[2] == board[4] && board[2] == board[6]) {
			if (board[2] == 'X') return 1;
			else if (board[2] == 'O') return -1;
		}
		// check not yet the end
		for (int i = 0; i < 9; i++){
			if (board[i] == '_') return -2;
		}
		// Draw
		return 0;
	}
	
	public char whoGoNext() {
		int numO = 0, numX = 0;
		for (int i = 0; i < 9; i++){
			if (board[i] == 'O') numO++;
			if (board[i] == 'X') numX++;
		}
		if (numO >= numX) return 'X';
		return 'O';
	}
	
	public void printBoard() {
		System.out.println(board[0]+""+board[1]+""+board[2]);
		System.out.println(board[3]+""+board[4]+""+board[5]);
		System.out.print(board[6]+""+board[7]+""+board[8] + "  ");
	}
}
