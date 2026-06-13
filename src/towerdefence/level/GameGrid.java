package towerdefence.level;

import towerdefence.util.Vector2D;

import java.util.List;
import java.util.Optional;

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
            Optional<Vector2D> prev = i > 0             ? Optional.of(path.get(i-1)) : Optional.empty();
            Optional<Vector2D> next = i < path.size()-1 ? Optional.of(path.get(i+1)) : Optional.empty();

            prev.ifPresent(vector2D -> fillSegment(vector2D, cur));

            int col = (int) cur.x(), row = (int) cur.y();
            if (col >= 0 && col < cols && row >= 0 && row < rows) {
                TileType incoming = determineTileType(prev, cur, next);
                TileType merged = mergeTile(tiles[row][col], incoming);
                tiles[row][col] = merged;
            }
        }
    }

    private void fillSegment(Vector2D from, Vector2D to) {
        int x0 = (int) from.x(), y0 = (int) from.y();
        int x1 = (int) to.x(),   y1 = (int) to.y();
        int dx = (int) Math.signum(x1 - x0);
        int dy = (int) Math.signum(y1 - y0);
        TileType straight = dx != 0 ? TileType.PATH_H : TileType.PATH_V;

        int x = x0 + dx, y = y0 + dy;
        while (x != x1 || y != y1) {
            if (x >= 0 && x < cols && y >= 0 && y < rows)
                tiles[y][x] = mergeTile(tiles[y][x], straight);
            x += dx; y += dy;
        }
    }

    private TileType mergeTile(TileType existing, TileType incoming) {
        int mask = toBitmask(existing) | toBitmask(incoming);
        return fromBitmask(mask);
    }

    private int toBitmask(TileType t) {
        return switch (t) {
            case PATH_H    -> 0b1010; // W+E
            case PATH_V    -> 0b0101; // N+S
            case CORNER_NW -> 0b1001; // W+N
            case CORNER_SW -> 0b1100; // W+S  (let op: S=bit2, W=bit3)
            case CORNER_NE -> 0b0011; // E+N
            case CORNER_SE -> 0b0110; // E+S
            case T_WNS     -> 0b1101; // W+N+S
            case T_WNE     -> 0b1011; // W+N+E
            case T_WES     -> 0b1110; // W+E+S
            case T_NES     -> 0b0111; // N+E+S
            case CROSS     -> 0b1111;
            default        -> 0b0000;
        };
    }

    private TileType fromBitmask(int mask) {
        return switch (mask) {
            case 0b1010 -> TileType.PATH_H;
            case 0b0101 -> TileType.PATH_V;
            case 0b1001 -> TileType.CORNER_NW;
            case 0b1100 -> TileType.CORNER_SW;
            case 0b0011 -> TileType.CORNER_NE;
            case 0b0110 -> TileType.CORNER_SE;
            case 0b1101 -> TileType.T_WNS;
            case 0b1011 -> TileType.T_WNE;
            case 0b1110 -> TileType.T_WES;
            case 0b0111 -> TileType.T_NES;
            case 0b1111 -> TileType.CROSS;
            default     -> TileType.GRASS;
        };
    }

    private TileType determineTileType(Optional<Vector2D> prev, Vector2D cur, Optional<Vector2D> next) {
        int dxIn  = prev.map(v -> (int) Math.signum(cur.x() - v.x())).orElse(0);
        int dyIn  = prev.map(v -> (int) Math.signum(cur.y() - v.y())).orElse(0);
        int dxOut = next.map(v -> (int) Math.signum(v.x() - cur.x())).orElse(0);
        int dyOut = next.map(v -> (int) Math.signum(v.y() - cur.y())).orElse(0);

        // doorlopend rechtdoor
        if (dxIn != 0 && dxOut != 0) return TileType.PATH_H;
        if (dyIn != 0 && dyOut != 0) return TileType.PATH_V;

        // startpunt
        if (prev.isEmpty()) return dxOut != 0 ? TileType.PATH_H : TileType.PATH_V;

        // eindpunt: geef alleen de inkomende richting terug als bitmask-fragment
        // de merge in buildFromPath combineert dit met wat er al ligt
        if (next.isEmpty()) return dxIn != 0 ? TileType.PATH_H : TileType.PATH_V;

        // bochten
        if (dxIn > 0 && dyOut > 0) return TileType.CORNER_SW;
        if (dxIn > 0 && dyOut < 0) return TileType.CORNER_NW;
        if (dxIn < 0 && dyOut > 0) return TileType.CORNER_SE;
        if (dxIn < 0 && dyOut < 0) return TileType.CORNER_NE;
        if (dyIn > 0 && dxOut > 0) return TileType.CORNER_NE;
        if (dyIn > 0 && dxOut < 0) return TileType.CORNER_NW;
        if (dyIn < 0 && dxOut > 0) return TileType.CORNER_SE;
        if (dyIn < 0 && dxOut < 0) return TileType.CORNER_SW;

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
                    case CORNER_SE -> "SE ";
                    case CORNER_SW -> "SW ";
                    case CORNER_NE -> "NE ";
                    case CORNER_NW -> "NW ";
                    case T_NES     -> "TNES ";
                    case T_WES     -> "TWES ";
                    case T_WNE     -> "TWNE ";
                    case T_WNS     -> "TWNS ";
                    case CROSS     -> " X ";
                    case TOWER_SPOT -> "[] ";
                    case GRASS     -> " . ";
                });
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public void reset() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                tiles[row][col] = TileType.GRASS;
            }
        }
    }
}
