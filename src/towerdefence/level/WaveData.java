package towerdefence.level;

public record WaveData(int count, double intervalSeconds, double startDelaySeconds, String enemyType) {
    public WaveData {
        if (count < 1) {
            throw new IllegalArgumentException("Wave count must be >=1");
        }
        if (intervalSeconds <= 0.0) {
            throw new IllegalArgumentException("Wave interval seconds must be >0");
        }
        if (startDelaySeconds < 0.0) {
            throw new IllegalArgumentException("Wave start delay seconds must be >= 0");
        }

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
