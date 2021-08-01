package swen221.tetris.moves;

import swen221.tetris.logic.Board;
import swen221.tetris.logic.Rectangle;
import swen221.tetris.tetromino.ActiveTetromino;
import swen221.tetris.tetromino.Tetromino;

/**
 * Move the active tetromino one square to the right.
 *
 * @author David J. Pearce
 *
 */

public class MoveRight extends AbstractTranslation {

	/**
	 * Move the active tetromino one square to the right.
	 */
	public MoveRight() {
		super(1,0);
	}
	
	@Override
	public boolean isValid(Board board) {
		ActiveTetromino activeTetro = board.getActiveTetromino();
		if(activeTetro == null) {return false; }
		// is valid when the squares on the right side of the fulfilled squares of the active tetromino are all null
		Rectangle r = activeTetro.getBoundingBox();
		if(r.getMaxX() == board.getWidth() - 1) {return false; }
		for(int i = r.getMinY(); i <= r.getMaxY(); i++) {
			for(int j = r.getMaxX(); j >= r.getMinX(); j--) {
				Tetromino t = board.getTetrominoAt(j, i);
				if(t != null && t.equals(activeTetro)) {
					Tetromino rightSquare = board.getPlacedTetrominoAt(j + 1, i);
					if(rightSquare != null) {return false; }
					break;
				}
			}
		}
		return true;
	}
}
