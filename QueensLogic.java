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
        restrictBDD();
    }

    private void constructBDD() {
    	this.fact = JFactory.init(2000000,200000);
		fact.setVarNum(x*x);
    }
    
    private void addNQueensRules() {
    	int noOfVars = x*x;
    	
    	BDD rowRulesBDD = fact.one();			//TODO: Find out how to initialise empty expressions !!! NULL OR TRUE (fact.one()) ???
    	BDD colRulesBDD = fact.one();
    	BDD diaRulesTopBDD = fact.one();
    	BDD diaRulesBottomBDD = fact.one();
    	
    	BDD rowRules[] = new BDD[x];		
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
    	   	
    	for(int i = 0 ; i < noOfVars ; i++) { // iteration over all variables from x0 to x_(n-1)    		
    		int row = (int) Math.floor(i / x);
    		int col = i % x ;
    		int minDimension = Math.min(col, row);
    		BDD rowVarRule = fact.one();
    		BDD colVarRule = fact.one();
    		BDD diaVarRuleTop = fact.one();
    		BDD diaVarRuleBottom = fact.one();
    		
    		for(int j = 0 ; j < x ; j++) {
    			
    			// row rules
    			int varNo = i + (j - col);     			
    			if (varNo == i)
    				rowVarRule = rowVarRule.and(fact.ithVar(varNo));
    			else rowVarRule = rowVarRule.and(fact.nithVar(varNo));
    			
    			// col rules
    			varNo = i + (x * (j - row)); 	    			
    			if (varNo == i)
    				colVarRule = colVarRule.and(fact.ithVar(varNo));
    			else colVarRule = colVarRule.and(fact.nithVar(varNo));
    			
    			// diagonal from top rules
    			varNo = i + (j - minDimension) + (x * (j - minDimension));     			  
    			if(col - (j - minDimension) >= 0 && row - (j - minDimension) >= 0 && col - (j - minDimension) < x && row - (j - minDimension) < x) {
    				if (varNo == i)
    					diaVarRuleTop = diaVarRuleTop.and(fact.ithVar(varNo));
        			else diaVarRuleTop = diaVarRuleTop.and(fact.nithVar(varNo));
    			}    			
    			
    			//diagonal from bottom rules
    			varNo = i - (j - minDimension) + (x * (j - minDimension)); 
    			if(col - (j - minDimension) >= 0 && row + (j - minDimension) >= 0 && col - (j - minDimension) < x && row + (j - minDimension) < x) {
    				if (varNo == i)
    					diaVarRuleBottom = diaVarRuleBottom.and(fact.ithVar(varNo));
        			else diaVarRuleBottom = diaVarRuleBottom.and(fact.nithVar(varNo));
    			}
    		}   
    		
    		rowRules[row] = rowRules[row].or(rowVarRule);   		
    		colRules[col] = colRules[col].or(colVarRule);  
    		diaRulesTop[row - col + (x - 1)] = diaRulesTop[row - col + (x - 1)].or(diaVarRuleTop);   
    		diaRulesBottom[row + col] = diaRulesBottom[row + col].or(diaVarRuleBottom);   
    	}   
    	
    	for(int i = 0 ; i < x ; i++) {
    		rowRulesBDD = rowRulesBDD.and(rowRules[i]);
    		colRulesBDD = colRulesBDD.and(colRules[i]);
    		diaRulesTopBDD = diaRulesTopBDD.and(diaRulesTop[i]);
    		diaRulesBottomBDD = diaRulesBottomBDD.and(diaRulesBottom[i]);
    	}
    	
    	fullBDD = rowRulesBDD.and(colRulesBDD).and(diaRulesTopBDD).and(diaRulesBottomBDD);
    }
    
    private void restrictBDD() {
    	BDD restriction = null;
    	for(int i = 0 ; i < x ; i++)
    		for (int j = 0 ; j < x ; j++) {
    			if(board[i][j] == 1) {
    				restriction = restriction.and(fact.ithVar((i * x) + j));
    			}
    			
    			currentRestriction = restriction;
    			fullBDD = fullBDD.restrict(restriction);
    		}
    }
    
    private int[][] calculateValidDomains() {
    	int[][] vector = new int[x*x][2];
    	for(int i = 0 ; i < x ; i++)
        	for(int j = 0 ; j < 2 ; j++)
        		vector[i][j] = 0;
    	
    	for(int i = 0 ; i < x*x ; i++) 	{
    		BDD restricted = fullBDD.restrict(currentRestriction.and(fact.ithVar(i)));
    		if(!restricted.isZero()) vector[i][0] = 1;
    		else vector[i][0] = 0;
    		
    		restricted = fullBDD.restrict(currentRestriction.and(fact.nithVar(i)));
    		if(!restricted.isZero()) vector[i][1] = 1;
    		else vector[i][1] = 0;
    	}
    	
    	return vector;
    }
    
    private void updateBoard(int[][] vector) {
    	for(int i = 0 ; i < x*x ; i++) {
    		if (vector[i][0] == 1 && vector[i][1] == 0)
    			board[i % x][(int) Math.floor(i / x)] = 1;
    		else if (vector[i][0] == 0 && vector[i][1] == 1)
    			board[i % x][(int) Math.floor(i / x)] = -1;
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
