/**
 * This class implements the logic behind the BDD for the n-queens problem
 * You should implement all the missing methods
 * 
 * @author Stavros Amanatidis
 *
 */
import java.util.*;

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
    	BDD rowRules = fact.one();			//TODO: Find out how to 
    	BDD columnRules = fact.one();
    	BDD diagonalRules = fact.one();
    	int noOfVars = x*x;
    	
    	for(int i = 0 ; i < noOfVars ; i++) { // iteration over all variables from x0 to x_(n-1)    		
    		//row rules
    		int col = (int) Math.floor(i / x);
    		BDD rowRule = fact.one();
    		for(int j = 0 ; j < x ; j++) {
    			int varNo = i + (j - col);
    			
    			if (varNo == i)
    				rowRule = rowRule.and(fact.ithVar(varNo));
    			else rowRule = rowRule.and(fact.nithVar(varNo));
    		}
    		
    		if((i % x) == (x - 1)) {
    			rowRules = rowRules.and(rowRule);
    			rowRule = fact.one();
    		}
    		
    		//column rules ???
    		
    		//diagonal rules ???
    		
    	}   
    	
    	BDD full = rowRules.and(columnRules).and(diagonalRules);
    	return full;
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
