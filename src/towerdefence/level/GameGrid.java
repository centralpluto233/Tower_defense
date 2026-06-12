package towerdefence.level;

import towerdefence.util.Vector2D;

import java.util.List;

public class GameGrid {
    private final int cols;
    private final int rows;
    private final TileType[][] tiles;

    public GameGrid(int rows, int cols) {
        this.cols = cols;
        this.rows = rows;
        this.tiles = new TileType[rows][cols];

        for (int r=0; r<rows; r++) {
            for (int c=0; c<cols; c++) {
                tiles[r][c]= TileType.GRASS;
            }
        }
    }

    public void buildFromPath(List<Vector2D> path) {
        for (int i = 0; i < path.size(); i++) {
            Vector2D cur  = path.get(i);
            Vector2D prev = i > 0 ? path.get(i - 1) : null;
            Vector2D next = i < path.size() - 1 ? path.get(i + 1) : null;

            // vul alle tiles tussen vorige waypoint en huidige in
            if (prev != null) {
                int x0 = (int) prev.x(), y0 = (int) prev.y();
                int x1 = (int) cur.x(),  y1 = (int) cur.y();

                int dx = (int) Math.signum(x1 - x0);
                int dy = (int) Math.signum(y1 - y0);

                int x = x0 + dx, y = y0 + dy;
                while (x != x1 || y != y1) {
                    if (x >= 0 && x < cols && y >= 0 && y < rows) {
                        // tussenliggende tile: rechtdoor
                        tiles[y][x] = (dx != 0) ? TileType.PATH_H : TileType.PATH_V;
                    }
                    x += dx;
                    y += dy;
                }
            }

            // vul de waypoint zelf in (bocht of start/einde)
            int col = (int) cur.x();
            int row = (int) cur.y();
            if (col >= 0 && col < cols && row >= 0 && row < rows) {
                tiles[row][col] = determineTileType(prev, cur, next);
            }
        }
    }

    private TileType determineTileType(Vector2D prev, Vector2D cur, Vector2D next) {
        int dxIn  = prev == null ? 0 : (int) Math.signum(cur.x() - prev.x());
        int dyIn  = prev == null ? 0 : (int) Math.signum(cur.y() - prev.y());
        int dxOut = next == null ? 0 : (int) Math.signum(next.x() - cur.x());
        int dyOut = next == null ? 0 : (int) Math.signum(next.y() - cur.y());

        if (dxIn != 0 && dxOut != 0) return TileType.PATH_H;
        if (dyIn != 0 && dyOut != 0) return TileType.PATH_V;

        // start/einde
        if (prev == null) return dxOut != 0 ? TileType.PATH_H : TileType.PATH_V;
        if (next == null) return dxIn  != 0 ? TileType.PATH_H : TileType.PATH_V;

        // bochten — "van X naar Y"
        if (dxIn > 0 && dyOut > 0) return TileType.CORNER_LD; // van links, naar onder
        if (dxIn > 0 && dyOut < 0) return TileType.CORNER_LU; // van links, naar boven
        if (dxIn < 0 && dyOut > 0) return TileType.CORNER_RD; // van rechts, naar onder
        if (dxIn < 0 && dyOut < 0) return TileType.CORNER_RU; // van rechts, naar boven
        if (dyIn > 0 && dxOut > 0) return TileType.CORNER_RU; // van boven, naar rechts
        if (dyIn > 0 && dxOut < 0) return TileType.CORNER_LU; // van boven, naar links
        if (dyIn < 0 && dxOut > 0) return TileType.CORNER_RD; // van onder, naar rechts
        if (dyIn < 0 && dxOut < 0) return TileType.CORNER_LD; // van onder, naar links

        return TileType.PATH_H;
    }

    public int getRows() {
        return rows;
    }
    public int getCols() {
        return cols;
    }
    public TileType getTile(int row, int col) {
        if (col < 0 || col >= cols || row < 0 || row >= rows) return TileType.GRASS;
        return tiles[row][col];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Grid ").append(cols).append("x").append(rows).append(":\n");
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                sb.append(switch (tiles[row][col]) {
                    case PATH_H    -> " H ";
                    case PATH_V    -> " V ";
                    case CORNER_RD -> "RD ";
                    case CORNER_LD -> "LD ";
                    case CORNER_RU -> "RU ";
                    case CORNER_LU -> "LU ";
                    case GRASS     -> " . ";
                    default        -> " ? ";
                });
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
