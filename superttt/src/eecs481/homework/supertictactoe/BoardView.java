package eecs481.homework.supertictactoe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class BoardView extends View {

	private static final int P1COLOR = Color.BLUE;
	private static final int P2COLOR = Color.RED;
	
	private int[][] board = new int[9][9];
	private int[][] bigboard = new int[3][3];
	private int[][] movecount = new int[3][3];
	private boolean[][] active = new boolean[3][3];
	private int bigmovecount = 0;
	private int turn = 1;
	private boolean gameover = false;
	private Paint paint = new Paint();
	
	public BoardView(Context context) {
		super(context);
	}

	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BoardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void reset() {
		for (int x = 0; x < 3; ++x) {
			for (int y = 0; y < 3; ++y) {
				bigboard[x][y] = 0;
				movecount[x][y] = 0;
				active[x][y] = true;
			}
		}
		for (int x = 0; x < 9; ++x) {
			for (int y = 0; y < 9; ++y) {
				board[x][y] = 0;
			}
		}
		turn = 1;
		bigmovecount = 0;
		gameover = false;
		invalidate();
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_UP)
			return true;
		for (int row = 0; row < 9; ++row) {
			for (int col = 0; col < 9; ++col) {
				Rect r = new Rect(col * (getWidth() / 9), row
						* (getWidth() / 9), (col + 1) * (getWidth() / 9),
						(row + 1) * (getWidth() / 9));
				if (r.contains((int) event.getX(), (int) event.getY())) {
					move(col, row);
					invalidate();
					return true;
				}
			}
		}
		return true;
	}

	private void move(int col, int row) {
		if (!gameover && active[col / 3][row / 3]) {
			if (board[col][row] == 0) {
				board[col][row] = turn;

				for (int i = 0; i < 3; ++i) {
					for (int j = 0; j < 3; ++j) {
						active[i][j] = false;
					}
				}
				active[col % 3][row % 3] = true;
				++movecount[col / 3][row / 3];

				boolean smallwin = false;

				// check diag (top left to bottom right)
				if ((col % 3) == (row % 3)) {
					for (int i = 0; i < 3; ++i) {
						if (board[(col / 3) * 3 + i][(row / 3) * 3 + i] != turn)
							break;
						if (i == 2) {
							smallwin = true;
						}
					}
				}

				// check diag (bottom left to top right)
				for (int i = 0; i < 3; ++i) {
					if (board[(col / 3) * 3 + i][(row / 3) * 3 + 2 - i] != turn)
						break;
					if (i == 2) {
						smallwin = true;
					}
				}

				// check column
				for (int i = 0; i < 3; ++i) {
					if (board[col][(row / 3) * 3 + i] != turn)
						break;
					if (i == 2) {
						smallwin = true;
					}
				}

				// check row
				for (int i = 0; i < 3; ++i) {
					if (board[(col / 3) * 3 + i][row] != turn)
						break;
					if (i == 2) {
						smallwin = true;
					}
				}

				// check draw
				if (movecount[col / 3][row / 3] == 9) {
					bigboard[col / 3][row / 3] = -1;
				}

				if (smallwin) {
					if (bigboard[col / 3][row / 3] == 0) {
						bigboard[col / 3][row / 3] = turn;
						++bigmovecount;
						checkBigWin(col / 3, row / 3);
					}
				}

				turn = (turn == 1) ? 2 : 1;
			}
		}
	}
	
	private void checkBigWin(int col, int row) {
		// check diag (top left to bottom right)
		int win = 0;
		if (col == row) {
			for (int i = 0; i < 3; ++i) {
				if (bigboard[i][i] != turn)
					break;
				if (i == 2) {
					win = 1;
				}
			}
		}

		// check diag (bottom left to top right)
		for (int i = 0; i < 3; ++i) {
			if (bigboard[i][2 - i] != turn)
				break;
			if (i == 2) {
				win = 1;
			}
		}

		// check column
		for (int i = 0; i < 3; ++i) {
			if (bigboard[col][i] != turn)
				break;
			if (i == 2) {
				win = 1;
			}
		}

		// check row
		for (int i = 0; i < 3; ++i) {
			if (bigboard[i][row] != turn)
				break;
			if (i == 2) {
				win = 1;
			}
		}

		if (bigmovecount == 9) {
			win = 2;
		}

		if (win > 0) {
			for (int i = 0; i < 3; ++i) {
				for (int j = 0; j < 3; ++j) {
					active[i][j] = false;
					gameover = true;
				}
			}
		}
	}

	protected void onDraw(Canvas canvas) {
		paint.setAntiAlias(true);
		paint.setColor(Color.GRAY);
		paint.setStyle(Style.FILL);
		canvas.drawRect(0, 0, getWidth(), getWidth(), paint);
		drawGrid(canvas);
		drawSmallOX(canvas);
		drawInactiveRect(canvas);
		drawBigOX(canvas);
	}
	
	private void drawGrid(Canvas canvas) {
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(3);
		canvas.drawRect(0, 0, getWidth(), getWidth(), paint);
		paint.setStrokeWidth(1);
		for (int col = 0; col < 8; ++col) {
			canvas.drawLine((col + 1) * getWidth() / 9, 0, (col + 1)
					* getWidth() / 9, getWidth(), paint);
		}
		for (int row = 0; row < 8; ++row) {
			canvas.drawLine(0, (row + 1) * getWidth() / 9, getWidth(),
					(row + 1) * getWidth() / 9, paint);
		}
		paint.setStrokeWidth(3);
		for (int col = 0; col < 2; ++col) {
			canvas.drawLine((col + 1) * getWidth() / 3, 0, (col + 1)
					* getWidth() / 3, getWidth(), paint);
		}
		for (int row = 0; row < 2; ++row) {
			canvas.drawLine(0, (row + 1) * getWidth() / 3, getWidth(),
					(row + 1) * getWidth() / 3, paint);
		}
	}
	
	private void drawInactiveRect(Canvas canvas) {
		paint.setStyle(Style.FILL);
		paint.setARGB(128, 255, 255, 255);
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				if (!active[i][j]) {
					canvas.drawRect(i * (getWidth() / 3), j * (getWidth() / 3),
							(i + 1) * (getWidth() / 3), (j + 1)
									* (getWidth() / 3), paint);
				}
			}
		}
	}
	
	private void drawSmallOX(Canvas canvas) {
		paint.setStrokeWidth(10);
		paint.setStyle(Style.STROKE);
		for (int row = 0; row < 9; ++row) {
			for (int col = 0; col < 9; ++col) {
				if (board[col][row] != 0) {
					int cx1 = col * getWidth() / 9;
					int cy1 = row * getWidth() / 9;
					int cx2 = (col + 1) * getWidth() / 9;
					int cy2 = (row + 1) * getWidth() / 9;
					int color = (board[col][row] == 1) ? P1COLOR : P2COLOR;
					paint.setColor(color);
					if (board[col][row] == 1) {
						canvas.drawLine(cx1 + getWidth() / 54, cy1 + getWidth()
								/ 54, cx2 - getWidth() / 54, cy2 - getWidth()
								/ 54, paint);
						canvas.drawLine(cx1 + getWidth() / 54, cy1 + getWidth()
								/ 9 - getWidth() / 54, cx2 - getWidth() / 54,
								cy2 - getWidth() / 9 + getWidth() / 54, paint);
					} else {
						canvas.drawCircle((cx1 + cx2) / 2, (cy1 + cy2) / 2,
								getWidth() / 27, paint);
					}
				}
			}
		}
	}
	
	private void drawBigOX(Canvas canvas) {
		paint.setStrokeWidth(10);
		paint.setStyle(Style.STROKE);
		for (int row = 0; row < 3; ++row) {
			for (int col = 0; col < 3; ++col) {
				if (bigboard[col][row] != 0) {
					int cx1 = col * getWidth() / 3;
					int cy1 = row * getWidth() / 3;
					int cx2 = (col + 1) * getWidth() / 3;
					int cy2 = (row + 1) * getWidth() / 3;
					int color = (bigboard[col][row] == 1) ? P1COLOR : P2COLOR;
					paint.setColor(color);
					if (bigboard[col][row] == 1) {
						canvas.drawLine(cx1 + getWidth() / 18, cy1 + getWidth()
								/ 18, cx2 - getWidth() / 18, cy2 - getWidth()
								/ 18, paint);
						canvas.drawLine(cx1 + getWidth() / 18, cy1 + getWidth()
								/ 3 - getWidth() / 18, cx2 - getWidth() / 18,
								cy2 - getWidth() / 3 + getWidth() / 18, paint);
					} else {
						canvas.drawCircle((cx1 + cx2) / 2, (cy1 + cy2) / 2,
								getWidth() / 9, paint);
					}
				}
			}
		}
	}
}
