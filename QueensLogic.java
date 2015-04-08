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
    
    private BDD addNQueensRules() {
    	int noOfVars = x*x;
    	
    	BDD rowRulesBDD = fact.one();			//TODO: Find out how to initialise empty expressions !!! NULL OR TRUE (fact.one()) ???
    	BDD columnRulesBDD = fact.one();
    	BDD diagonalRulesBDD = fact.one();
    	
    	BDD rowRules[] = new BDD[x];
    	BDD colRules[] = new BDD[x];
    	BDD diaRules[] = new BDD[x];
    	   	
    	for(int i = 0 ; i < noOfVars ; i++) { // iteration over all variables from x0 to x_(n-1)    		
    		int row = (int) Math.floor(i / x);
    		int col = i % x ;
    		BDD rowVarRule = null;
    		BDD colVarRule = null;
    		
    		// **ROW AND COL RULES** //
    		for(int j = 0 ; j < x ; j++) {
    			
    			// row rules
    			int rowVarNo = i + (j - col);     			
    			if (rowVarNo == i)
    				rowVarRule = rowVarRule.and(fact.ithVar(rowVarNo));
    			else rowVarRule = rowVarRule.and(fact.nithVar(rowVarNo));
    			
    			// col rules
    			int colVarNo = i + (x * (j - row)); 	    			
    			if (colVarNo == i)
    				colVarRule = colVarRule.and(fact.ithVar(colVarNo));
    			else colVarRule = colVarRule.and(fact.nithVar(colVarNo));
    		}    		
    		rowRules[row] = rowRules[row].or(rowVarRule);   		
    		colRules[col] = colRules[col].or(colVarRule);
    		
    		// **DIAGONALS RULES** //
    		
    	}   
    	
    	for(BDD rule : rowRules) {
    		rowRulesBDD = rowRulesBDD.and(rule);
    	}
    	
    	BDD fullRules = rowRulesBDD.and(columnRulesBDD).and(diagonalRulesBDD);
    	return fullRules;
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
