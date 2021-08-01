// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a SWEN221 assignment.
// You may not distribute it in any other way without permission.
package swen221.tetris.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import swen221.tetris.moves.DropMove;
import swen221.tetris.moves.Move;
import swen221.tetris.moves.ClockwiseRotation;
import swen221.tetris.moves.AbstractTranslation;
import swen221.tetris.tetromino.*;

/**
 * A Game instance represents a running game of Tetris. It accepts game moves
 * and updates the board accordingly. Likewise, it provides an API to access the
 * board itself. Finally, it determines when the game is over (i.e. because the
 * board is full).
 *
 * @author David J. Pearce
 * @author Marco Servetto
 */
public class Game {
	/**
	 * An (infinite) sequence of tetrominos to be used to determine the next tetromino.
	 */
	private final Iterator<Tetromino> tetrominoSequence;

	/**
	 * The next tetromnino to be issued. This is useful, amongst other things, for
	 * showing the user what is coming next.
	 */
	private ActiveTetromino nextTetromino;

	/**
	 * The current state of the game board.
	 */
	private Board board;

	/**
	 * Records the number of lines which have been removed.
	 */
	private int lines = 0;
	
	/**
	 * Records the current score which is determined as a function of the number of
	 * lines removed.
	 */
	private int score = 0;
	/**
	 * If the active tetromino lands and no full lines to remove, 
	 * wait for a clock as the user may move the active tetromino before place it
	 */
	private int count = 0;

	/**
	 * Constructor 
	 * 
	 * @param sequence  next tetrominos that will appear
	 * @param width     number of squares for width
	 * @param height    number of squares for height
	 * 
	 */
	public Game(Iterator<Tetromino> sequence, int width, int height) {
		this.tetrominoSequence = sequence;
		// Initial boards list with an empty board.
		this.board = new Board(sequence, width, height);
		// Initialise next tetromino
		this.nextTetromino = nextActiveTetromino();
	}

	/**
	 * Get number of lines removed
	 *
	 * @return the number of lines removed
	 */
	public int getLines() {
		return lines;
	}

	/**
	 * Get the current score.
	 *
	 * @return the current score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Get the current board being acted upon.
	 *
	 * @return the current active board
	 */
	public Board getActiveBoard() {
		return board;
	}

	/**
	 * Get the next tetromino which will be issued.
	 *
	 * @return the next tetromino in the sequence
	 */
	public Tetromino getNextTetromino() {
		return nextTetromino.getUnderlyingTetromino();
	}

	/**
	 * Check whether the game is over. This happens when we can no longer place the
	 * next tetromino.
	 *
	 * @return boolean
	 *         true if game is over;
	 *         false if the game is still continuing
	 */
	public boolean isGameOver() {
		// Game is over when can no longer place next tetromino
		return !board.canPlaceTetromino(nextTetromino);
	}

	/**
	 * Reset the game so another can be played.
	 */
	public void reset() {
		// reset the line count
		this.lines = 0;
		// reset the score
		this.score = 0;
		// reset the board
		this.board = new Board(tetrominoSequence, board.getWidth(), board.getHeight());
		
		this.count = 0;
	}
	
	/**
	 * Apply a given move to the board. This will update the board if the move is
	 * valid, otherwise it will be ignored.
	 *
	 * @param move
	 *            ClockwiseRotation, DropMove, MoveDown, MoveLeft, MoveRight
	 * 
	 * @return boolean
	 *          true if the move is applied
	 *          otherwise false
	 */
	public boolean apply(Move move) {
		// has stayed for one clock at the bottom already, can't move it
		if(count > 0) {return false; } 
		// Check whether the move was valid as, if not, then it's ignored.
		if (move.isValid(board)) {
			// Yes, move is valid therefore apply it for real.
			board = move.apply(board);
			return true;
		} else {
			// This move was ignored.
			return false;
		}
	}
	
	/**
	 * Clock the game for another cycle. This will apply gravity to the board and
	 * check whether or not the active tetromino has landed. If the piece has
	 * landed, then we will remove full rows, etc.
	 */
	public void clock() {
		if(isGameOver()) {return; }
		
		ActiveTetromino activeTetromino = board.getActiveTetromino();
		
		// place a new tetromino and make it active
		if(activeTetromino == null && board.canPlaceTetromino(nextTetromino)) { 
		    activeTetromino = nextTetromino;
			board.setActiveTetromino(activeTetromino);
			nextTetromino = nextActiveTetromino();
		} 
		
		// apply gravity
		else if(activeTetromino != null && canGoNextLine(activeTetromino) == true) {
			activeTetromino = activeTetromino.translate(0, -1); 
			board.setActiveTetromino(activeTetromino);
		}
		
		// the active tetromino cannot move to next line
		else if(activeTetromino != null && canGoNextLine(activeTetromino) == false) {
			// if there is at least one full row, place the active tetromino and remove all full rows
			if(fullLine(board)>= 0) {
				count = 0;
				board.placeTetromino(activeTetromino);
				board.setActiveTetromino(null);
				removeLines(board);
				// set the top line all null
				for(int i = 0; i < board.getWidth(); i++) {
					board.setPlacedTetrominoAt(i, board.getHeight() - 1, null);
				} 
			}else {
				if(count == 0) {
					count++;
					return;
				}else {
					count = 0;
					board.placeTetromino(activeTetromino);
				    if(board.canPlaceTetromino(nextTetromino)) {
					    // promote next tetromino to be active
					    activeTetromino = nextTetromino;
					    board.setActiveTetromino(activeTetromino);
					    nextTetromino = nextActiveTetromino();
				    }
				}
			}	
		}
	}
	
	/**
	 * Check if the active tetromino can move to the next line:
	 * checking whether the squares under the four squares of the active tetromino has tetromino placed
	 * 
	 * @param  a 
	 *         current active tetromino
	 *         
	 * @return boolean
	 *          true if the active tetromino can move to the next line
	 *          other false
	 */
	private boolean canGoNextLine(ActiveTetromino a) {
		Rectangle r = a.getBoundingBox();
		if(r.getMinY() == 0) {return false; }
		for(int i = r.getMinX(); i <= r.getMaxX(); i++) {
			for(int j = r.getMinY(); j <= r.getMaxY(); j++) {
				Tetromino t = board.getTetrominoAt(i, j);
				if(t != null && t.equals(a)) {
					Tetromino placedTetromino = board.getPlacedTetrominoAt(i, j - 1);
					if(placedTetromino == null) {
						break; 
					} else {
						return false; 
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * Remove all full lines from the board (line by line), and move the lines on those lines down
	 * 
	 * @param board
	 *         current active board
	 * 
	 */
	private void removeLines(Board board) {
		int totalLinesRemoved  = 0;
		while(true) {
			int removeRowNumber = fullLine(board);
			if(removeRowNumber == -1) {break; }
			for(int i = removeRowNumber; i < board.getHeight() - 1; i++) {
				for(int j = 0; j < board.getWidth(); j++) {
					// set the line of tetrominos above the removed line to the removed line 
					board.setPlacedTetrominoAt(j, i, board.getPlacedTetrominoAt(j, i+1));
				}
			}
			lines++;
			totalLinesRemoved++;
		}
		setScore(totalLinesRemoved); 
	}
	
	/**
	 * Calculate the score:
	 * remove one line: 10 mark
	 * remove two lines: 30 mark
	 * remove three lines: 60 mark
	 * remove four lines: 100 mark
	 * 
	 * @param numLinesRemoved number of lines removed
	 */
	public void setScore(int numLinesRemoved) {
		if(numLinesRemoved == 1) {
			score = score + 10;
		}else if(numLinesRemoved == 2) {
			score = score + 30;
		}else if(numLinesRemoved == 3) {
			score = score + 60;
		}else if(numLinesRemoved == 4) {
			score = score + 100;
		}
	}
	
	/**
	 * Get the row index of the last full line (closest to the bottom of the board) and return it.
	 * 
	 * @param board
	 *                   current active board
	 *  
	 * @return index of row (y) of the full line closest to the bottom;
	 *         if no full lines, return -1.
	 *         
	 * 
	 */
	private int fullLine(Board board) {
		for(int row = 0; row < board.getHeight(); row++) {
			int countCol = 0;
			for(int col = 0; col < board.getWidth(); col++) {
				if(board.getTetrominoAt(col, row) != null) {
					countCol++;
				}
			}
			// if all tetrominos on this line is not null:
			if(countCol == board.getWidth()) {
				return row;
			}
		}
		return -1;
	}

	// ======================================================================
	// Helper methods
	// ======================================================================

	/**
	 * Determine the next active tetromino for the board.
	 *
	 * @return the next activeTetromino
	 */
	private ActiveTetromino nextActiveTetromino() {
		// Determine center for next tetromino
		int cx = board.getWidth() / 2;
		int cy = board.getHeight() - 2;
		// set next tetromino
		if(tetrominoSequence.hasNext()) {
			return new ActiveTetromino(cx, cy, tetrominoSequence.next());
		}else {
			return null;
		}	
	}
}
