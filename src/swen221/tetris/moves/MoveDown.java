package swen221.tetris.moves;

import swen221.tetris.logic.Board;
import swen221.tetris.logic.Rectangle;
import swen221.tetris.tetromino.ActiveTetromino;
import swen221.tetris.tetromino.Tetromino;

/**
 * Move the active tetromino one square downwards.
 *
 * @author David J. Pearce
 *
 */

public class MoveDown extends AbstractTranslation {

	/**
	 * Move the active tetromino to the next line.
	 */
	public MoveDown() {
		super(0,-1);
	}
	
	@Override
	public boolean isValid(Board board) {
		ActiveTetromino activeTetro = board.getActiveTetromino();
		if(activeTetro == null) {return false; }
		// is valid when the squares under the fulfilled squares of the active tetromino are all null
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
}
