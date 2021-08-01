// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a SWEN221 assignment.
// You may not distribute it in any other way without permission.
package swen221.tetris.moves;

import swen221.tetris.logic.Board;
import swen221.tetris.logic.Rectangle;
import swen221.tetris.tetromino.ActiveTetromino;
import swen221.tetris.tetromino.Tetromino;

/**
 * Implements a rotation move which is either clockwise or anti-clockwise.
 *
 * @author David J. Pearce
 * @author Marco Servetto
 *
 */
public class ClockwiseRotation extends AbstractMove implements Move {
	
	
	@Override
	public boolean isValid(Board board) {
		ActiveTetromino activeTetro = board.getActiveTetromino();
		if(activeTetro == null) {return false; }
		// only valid when the rectangle of the rotated tetromino is not out of the bound of the board
		Tetromino t = activeTetro.rotate(1);
		Rectangle r = t.getBoundingBox();
		// out of the bound of the board
		if(r.getMinX() < 0 || r.getMaxX() >= board.getWidth() || r.getMinY() < 0 || r.getMaxY() >= board.getHeight()) {
			return false;
		}
		// overlap with other tetromino after rotation
		for(int row = r.getMinY(); row <= r.getMaxY(); row++) {
			for(int col = r.getMinX(); col <= r.getMaxX(); col++) {
				if(t.isWithin(col, row)) {
					if(board.getTetrominoAt(col, row) != null && !board.getTetrominoAt(col, row).equals(activeTetro)) {
						return false;
					}
				}
			}
		}
		
		
		
		return true;
	}

	@Override
	public Board apply(Board board) {
		// Create copy of the board to prevent modifying its previous state.
		board = new Board(board);
		// Create a copy of this board which will be updated.
		ActiveTetromino tetromino = board.getActiveTetromino().rotate(1);
		// Apply the move to the new board, rather than to this board.
		board.setActiveTetromino(tetromino);
		// Return updated version of this board.
		return board;
	}
}
