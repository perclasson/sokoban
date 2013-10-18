public class Constants {
	public static final char SPACE = ' ', WALL = '#', GOAL = '.', PLAYER = '@', PLAYER_ON_GOAL = '+', BOX = '$', BOX_ON_GOAL = '*', DEADLOCK = 'D';
	public static final int[] dx = { 1, -1, 0, 0 };
	public static final int[] dy = { 0, 0, -1, 1 };
	public static final int[] bigdx = { -1, -1, -1, 0, 0, 0, 1, 1, 1 };
	public static final int[] bigdy = { -1, 0, 1, -1, 0, 1, -1, 0, 1 };
	public static final int GOAL_COST_SCALE = 10;
}
