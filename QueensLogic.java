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

    private void constructBDD()
    {
    	this.fact = JFactory.init(2000000,200000);
		fact.setVarNum(x*x);
		
//		BDD True = fact.one();
//		BDD False = fact.zero();
    }
    
    private void addNQueensRules()
    {
    	
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
    	
    }
    
    
}
