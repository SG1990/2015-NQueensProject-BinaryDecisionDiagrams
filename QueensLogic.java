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

    public QueensLogic() {
       //constructor
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
        calculateValidDomains();
    }

    private void constructBDD() {
    	this.fact = JFactory.init(2000000,200000);
		fact.setVarNum(x*x);
    }
    
    private void addNQueensRules() {
    	int noOfVars = x*x;
    	
    	BDD rowRulesBDD = fact.one();			//TODO: Find out how to initialise empty expressions !!! NULL OR TRUE (fact.one()) ???
    	BDD colRulesBDD = fact.one();
    	BDD diaRulesBDD = fact.one();
    	
    	BDD rowRules[] = new BDD[x];
    	BDD colRules[] = new BDD[x];
    	BDD diaRulesTop[] = new BDD[2*x - 1];
    	BDD diaRulesBottom[] = new BDD[2*x - 1];
    	   	
    	for(int i = 0 ; i < noOfVars ; i++) { // iteration over all variables from x0 to x_(n-1)    		
    		int row = (int) Math.floor(i / x);
    		int col = i % x ;
    		int minDimension = Math.min(col, row);
    		BDD rowVarRule = fact.one();;
    		BDD colVarRule = fact.one();;
    		BDD diaVarRuleTop = fact.one();;
    		BDD diaVarRuleBottom = fact.one();;
    		
    		// **ROW AND COL RULES** //
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
    			varNo = i + (j - minDimension) - (x * (j - minDimension)); 
    			if(col - (j - minDimension) >= 0 && row + (j - minDimension) >= 0 && col - (j - minDimension) < x && row + (j - minDimension) < x) {
    				if (varNo == i)
    					diaVarRuleBottom = diaVarRuleBottom.and(fact.ithVar(varNo));
        			else diaVarRuleBottom = diaVarRuleBottom.and(fact.nithVar(varNo));
    			}
    		}   
    		
    		rowRules[row] = rowRules[row].or(rowVarRule);   		
    		colRules[col] = colRules[col].or(colVarRule);  
    		diaRulesTop[row - col + (2*x - 1)] = diaRulesTop[row - col + (2*x - 1)].or(diaVarRuleTop);   
    		diaRulesBottom[row + col] = diaRulesBottom[row + col].or(diaVarRuleBottom);   
    	}   
    	
    	for(int i = 0 ; i < x ; i++) {
    		rowRulesBDD = rowRulesBDD.and(rowRules[i]);
    		colRulesBDD = colRulesBDD.and(colRules[i]);
    	}
    	
    	fullBDD = rowRulesBDD.and(colRulesBDD).and(diaRulesBDD);
    }
    
    private void calculateValidDomains()
    {
    	
    }
   
    public int[][] getGameBoard() {
        return board;
    }

    public boolean insertQueen(int column, int row) {

        if (board[column][row] == -1 || board[column][row] == 1) {
            return true;
        }
        
        board[column][row] = 1;
        
        recalculateBoard(column, row);
      
        return true;
    }
    
    private void recalculateBoard(int column, int row)
    {
    	//restrict
    }
    
    
}
