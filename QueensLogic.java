/**
 * This class implements the logic behind the BDD for the n-queens problem
 * You should implement all the missing methods
 * 
 * @author Stavros Amanatidis
 *
 */
import net.sf.javabdd.*;

public class QueensLogic {
    private int x = 0;
    private int y = 0;
    private int[][] board;
    BDDFactory fact;
    BDD fullBDD;
    BDD currentRestriction;
    boolean changedWhileUpdating = false;

    public QueensLogic() {
       //constructor
    }
    
    public int[][] getGameBoard() {
        return board;
    }

    public void initializeGame(int size) {
        this.x = size;
        this.y = size;
        this.board = new int[x][y];
        
        for(int i = 0 ; i < size ; i++)
        	for(int j = 0 ; j < size ; j++)
        		this.board[i][j] = 0;
        
        constructBDD();
        addNQueensRules();
    }

    private void constructBDD() {
    	this.fact = JFactory.init(2000000,200000);
		fact.setVarNum(x*x);
    }
    
    private void addNQueensRules() {
    	int noOfVars = x*x;
    	
    	BDD rowRulesBDD = fact.one();					//variable for holding all row rules
    	BDD colRulesBDD = fact.one();					//variable for holding all column rules
    	BDD diaRulesTopBDD = fact.one();				//variable for holding all diagonals rules (diagonals going from the top)
    	BDD diaRulesBottomBDD = fact.one();				//variable for holding all diagonals rules (diagonals going from the bottom)
    	
    	BDD rowRules[] = new BDD[x];					//helper arrays for holding respective rules separately
    	BDD colRules[] = new BDD[x];
    	BDD diaRulesTop[] = new BDD[2*x - 1];
    	BDD diaRulesBottom[] = new BDD[2*x - 1];
    	
    	for(int i = 0 ; i < x ; i++){
    		rowRules[i] = fact.zero();
    		colRules[i] = fact.zero();
    	}
    	
    	for(int i = 0 ; i < 2*x - 1 ; i++){
    		diaRulesTop[i] = fact.zero();
    		diaRulesBottom[i] = fact.zero();
    	}
    	   	
    	for(int i = 0 ; i < noOfVars ; i++) { 				// iteration over all variables from x0 to x_(n-1)    		
    		int row = (int) Math.floor(i / x);
    		int col = i % x ;
    		int minDimension = Math.min(col, row);
    		int maxDimension = Math.max(col, row);
    		BDD rowVarRule = fact.one();					// helper variables for holding rules referring to a specific BDD variable
    		BDD colVarRule = fact.one();
    		BDD diaVarRuleTop = fact.one();
    		BDD diaVarRuleBottom = fact.one();
    		BDD diaVarRuleTopZeroed = fact.one();
    		BDD diaVarRuleBottomZeroed = fact.one();
    		
    		for(int j = 0 ; j < x ; j++) {					// iteration over all variables of a given row/column/diagonal
    			
    			// defining row rules
    			int varNo = i + (j - col);     			
    			if (varNo == i)
    				rowVarRule = rowVarRule.and(fact.ithVar(varNo));
    			else rowVarRule = rowVarRule.and(fact.nithVar(varNo));
    			
    			// defining col rules
    			varNo = i + (x * (j - row)); 	    			
    			if (varNo == i)
    				colVarRule = colVarRule.and(fact.ithVar(varNo));
    			else colVarRule = colVarRule.and(fact.nithVar(varNo));
    			
    			// defining diagonal from top rules
    			varNo = i + (j - minDimension) + (x * (j - minDimension));     			  
    			if(maxDimension + (j - minDimension) < x) {
    				if (varNo == i)
    					diaVarRuleTop = diaVarRuleTop.and(fact.ithVar(varNo));
        			else diaVarRuleTop = diaVarRuleTop.and(fact.nithVar(varNo));
    				
    				diaVarRuleTopZeroed = diaVarRuleTopZeroed.and(fact.nithVar(varNo));			// there can be 0 queens in a diagonal
    			}    			
    			
    			// defining diagonal from bottom rules
    			varNo = i - (j - row) + (x * (j - row)); 
    			if(maxDimension - (j - minDimension) < x && maxDimension - (j - minDimension) >= 0) {
    				if (varNo == i)
    					diaVarRuleBottom = diaVarRuleBottom.and(fact.ithVar(varNo));
        			else diaVarRuleBottom = diaVarRuleBottom.and(fact.nithVar(varNo));
    				
    				diaVarRuleBottomZeroed = diaVarRuleBottomZeroed.and(fact.nithVar(varNo));
    			}
    		}   
    		
    		rowRules[row] = rowRules[row].or(rowVarRule);   											// disjunctions of possible queen placements for every row/column/diagonal
    		colRules[col] = colRules[col].or(colVarRule);  
    		diaRulesTop[row - col + x - 1] = diaRulesTop[row - col + x - 1].or(diaVarRuleTop);   
    		diaRulesTop[row - col + x - 1] = diaRulesTop[row - col + x - 1].or(diaVarRuleTopZeroed);  
    		diaRulesBottom[row + col] = diaRulesBottom[row + col].or(diaVarRuleBottom);   
    		diaRulesBottom[row + col] = diaRulesBottom[row + col].or(diaVarRuleBottomZeroed);   
    	}   
    	
    	for(int i = 0 ; i < x ; i++) {
    		rowRulesBDD = rowRulesBDD.and(rowRules[i]);
    		colRulesBDD = colRulesBDD.and(colRules[i]);
    	}
    	
    	for(int i = 0 ; i < 2*x -1; i++) {
    		diaRulesTopBDD = diaRulesTopBDD.and(diaRulesTop[i]);
    		diaRulesBottomBDD = diaRulesBottomBDD.and(diaRulesBottom[i]);
    	}
    	
    	fullBDD = rowRulesBDD.and(colRulesBDD).and(diaRulesTopBDD).and(diaRulesBottomBDD);				// conjunction of all rules
    	System.out.println("BDD is a tautology: " + fullBDD.isOne());
    	System.out.println("BDD is unsatisfiable: " + fullBDD.isZero());
    }
    
    private void restrictBDD() {													// method for restricting BDD with user choice
    	BDD restriction = fact.one();
    	for(int i = 0 ; i < x ; i++)
    		for (int j = 0 ; j < x ; j++) {
    			if(board[i][j] == 1) {
    				restriction = restriction.and(fact.ithVar((j * x) + i));
    			}	
    		}
    	currentRestriction = restriction;
		fullBDD = fullBDD.restrict(restriction);
    }
    
    private int[][] calculateValidDomains() {		// method for calculating whether a queen can be placed at a particular spot, must be placed, or whether it must be a cross
    	int[][] vector = new int[x*x][2];
    	for(int i = 0 ; i < x ; i++)
        	for(int j = 0 ; j < 2 ; j++)
        		vector[i][j] = 0;
    	
    	for(int i = 0 ; i < x*x ; i++) 	{	//checking if queen can be placed
    		BDD restricted = fullBDD.restrict(currentRestriction.and(fact.ithVar(i)));
    		if(!restricted.isZero()) vector[i][0] = 1;										//we check if the BDD is still satisfiable after restriction
    		else vector[i][0] = 0;
    		
    		//checking if cross can be placed; if cross can't be placed, a queen must be placed
    		restricted = fullBDD.restrict(currentRestriction.and(fact.nithVar(i)));
    		if(!restricted.isZero()) vector[i][1] = 1;
    		else vector[i][1] = 0;
    	}
    	
    	return vector;
    }
    
    private void updateBoard(int[][] vector) {
    	changedWhileUpdating = false;
    	for(int i = 0 ; i < x*x ; i++) {
    		if (vector[i][0] == 1 && vector[i][1] == 0) {		// place a queen if it must be placed
    			board[i % x][(int) Math.floor(i / x)] = 1;
    			changedWhileUpdating = true;
    		}
    		else if (vector[i][0] == 0 && vector[i][1] == 1)	//place a cross if it must be placed; else do nothing
    			board[i % x][(int) Math.floor(i / x)] = -1;
    	}
    	
    	if(changedWhileUpdating) {								//if a queen has been placed, because it had to be there, restrict the BDD and update the board again
    		restrictBDD();
    		updateBoard(calculateValidDomains());
    	}
    }

    public boolean insertQueen(int column, int row) {
        if (board[column][row] == -1 || board[column][row] == 1) {
            return true;
        }
        
        board[column][row] = 1;
        restrictBDD();
        updateBoard(calculateValidDomains());
      
        return true;
    }
}
