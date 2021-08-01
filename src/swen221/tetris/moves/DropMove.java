// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a SWEN221 assignment.
// You may not distribute it in any other way without permission.
package swen221.tetris.moves;

import swen221.tetris.logic.Board;
import swen221.tetris.logic.Rectangle;
import swen221.tetris.tetromino.ActiveTetromino;
import swen221.tetris.tetromino.Tetromino;

/**
 * Implements a "hard drop". That is, when the tetromino is immediately dropped
 * all the way down as far as it can go.
 *
 * @author David J. Pearce
 * @author Marco Servetto
 * @author q
 *
 */
public class DropMove implements Move {
	@Override
	public boolean isValid(Board board) {
		ActiveTetromino activeTetro = board.getActiveTetromino();
		if(activeTetro == null) {return false; }
		
		// is valid when the squares under the fulfilled squares of the active tetromino are all null
		// which means can dropmove at least one line
		Rectangle r = activeTetro.getBoundingBox();
		if(r.getMinY() == 0) {return false; }
		for(int i = r.getMinX(); i <= r.getMaxX(); i++) {
			for(int j = r.getMinY(); j <= r.getMaxY(); j++) {
				Tetromino t = board.getTetrominoAt(i, j);
				if(t != null && t.equals(activeTetro)) {
					Tetromino placedTetromino = board.getPlacedTetrominoAt(i, j - 1);
					if(placedTetromino != null) {return false; }
					break;
				}
			}
		}
		return true;	
	}

	@Override
	public Board apply(Board board) {
		ActiveTetromino activeTetro = board.getActiveTetromino();
		while(isValid(board)) {
			// translate the active tetromino to next line
			activeTetro = activeTetro.translate(0, -1);
			board.setActiveTetromino(activeTetro);
		}
		return board;
	}

	@Override
	public String toString() {
		return "drop";
	}
}
