package towerdefence.level;

import towerdefence.util.Vector2D;

import java.util.List;

public class GameGrid {
    private final int cols;
    private final int rows;
    private final int tileSize;
    private final TileType[][] tiles;

    public GameGrid(int rows, int cols, int tileSize) {
        this.cols = cols;
        this.rows = rows;
        this.tileSize = tileSize;
        this.tiles = new TileType[rows][cols];

        for (int r=0; r<rows; r++) {
            for (int c=0; c<cols; c++) {
                tiles[r][c]= TileType.GRASS;
            }
        }
    }

    public void buildFromPath(List<Vector2D> path) {
        for (int i = 0; i < path.size(); i++) {
            int col = (int) path.get(i).x();
            int row = (int) path.get(i).y();

            if (col < 0 || col >= cols || row < 0 || row >= rows) continue;

            Vector2D prev = i > 0 ? path.get(i - 1) : null;
            Vector2D next = i < path.size() - 1 ? path.get(i + 1) : null;
            tiles[row][col] = determineTileType(prev, path.get(i), next);
        }
    }

    private TileType determineTileType(Vector2D prev, Vector2D cur, Vector2D next) {
        int dxIn  = prev == null ? 0 : (int) Math.signum(cur.x() - prev.x());
        int dyIn  = prev == null ? 0 : (int) Math.signum(cur.y() - prev.y());
        int dxOut = next == null ? 0 : (int) Math.signum(next.x() - cur.x());
        int dyOut = next == null ? 0 : (int) Math.signum(next.y() - cur.y());

        // rechtdoor
        if (dxIn != 0 && dxOut != 0) return TileType.PATH_H;
        if (dyIn != 0 && dyOut != 0) return TileType.PATH_V;

        // start/einde
        if (prev == null) return dxOut != 0 ? TileType.PATH_H : TileType.PATH_V;
        if (next == null) return dxIn  != 0 ? TileType.PATH_H : TileType.PATH_V;

        if ((dxIn > 0 || dxOut > 0) && (dyIn > 0 || dyOut > 0)) return TileType.CORNER_RD; // rechts+onder
        if ((dxIn < 0 || dxOut < 0) && (dyIn > 0 || dyOut > 0)) return TileType.CORNER_LD; // links+onder
        if ((dxIn > 0 || dxOut > 0) && (dyIn < 0 || dyOut < 0)) return TileType.CORNER_RU; // rechts+boven
        if ((dxIn < 0 || dxOut < 0) && (dyIn < 0 || dyOut < 0)) return TileType.CORNER_LU; // links+boven

        return TileType.PATH_H; // fallback
    }

    public int getRows() {
        return rows;
    }
    public int getCols() {
        return cols;
    }
    public int getTileSize() {
        return tileSize;
    }
    public TileType getTile(int row, int col) {
        if (col < 0 || col >= cols || row < 0 || row >= rows) return TileType.GRASS;
        return tiles[row][col];
    }
}
