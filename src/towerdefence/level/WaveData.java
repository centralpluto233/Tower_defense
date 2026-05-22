package towerdefence.level;

public class WaveData {
    private final int count;
    private final double intervalSeconds;
    private final double startDelaySeconds;
    private final String enemyType;

    public WaveData(int count, double intervalSeconds, double startDelaySeconds, String enemyType) {
        if (count < 1) {
            throw new IllegalArgumentException("Wave count must be >=1");
        }
        if (intervalSeconds <= 0.0) {
            throw new IllegalArgumentException("Wave interval seconds must be >0");
        }
        if (startDelaySeconds < 0.0) {
            throw new IllegalArgumentException("Wave start delay seconds must be >= 0");
        }

        this.count = count;
        this.intervalSeconds = intervalSeconds;
        this.startDelaySeconds = startDelaySeconds;
        this.enemyType = enemyType;
    }

    public int getCount() {
        return count;
    }

    public double getIntervalSeconds() {
        return intervalSeconds;
    }

    public double getStartDelaySeconds() {
        return startDelaySeconds;
    }

    public String getEnemyType() {
        return enemyType;
    }

    @Override
    public String toString() {
        return "WaveData{" +
                "count=" + count +
                ", intervalSeconds=" + intervalSeconds +
                ", startDelaySeconds=" + startDelaySeconds +
                '}';
    }
}
