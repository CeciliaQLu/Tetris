package swen221.tetris.moves;

import swen221.tetris.logic.Board;
import swen221.tetris.logic.Rectangle;
import swen221.tetris.tetromino.ActiveTetromino;
import swen221.tetris.tetromino.Tetromino;

/**
 * Move the active tetromino one square to the left.
 *
 * @author David J. Pearce
 *
 */
public class MoveLeft extends AbstractTranslation {

	/**
	 * Move the active tetromino one square to the left.
	 */
	public MoveLeft() {
		super(-1,0);
	}
	
	@Override
	public boolean isValid(Board board) {
		ActiveTetromino activeTetro = board.getActiveTetromino();
		if(activeTetro == null) {return false; }
		// is valid when the squares on the left of the fulfilled squares of the active tetromino are all null
		Rectangle r = activeTetro.getBoundingBox();
		if(r.getMinX() == 0) {return false; }
		for(int i = r.getMinY(); i <= r.getMaxY(); i++) {
			for(int j = r.getMinX(); j <= r.getMaxX(); j++) {
				Tetromino t = board.getTetrominoAt(j, i);
				if(t != null && t.equals(activeTetro)) {
					Tetromino leftSquare = board.getPlacedTetrominoAt(j - 1, i);
					if(leftSquare != null) {return false; }
					break; 
				}
			}
		}
		return true;
	}
}
