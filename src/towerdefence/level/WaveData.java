package towerdefence.level;

public class WaveData {
    private final int count;
    private final double intervalSeconds;
    private final double startDelaySeconds;

    public WaveData(int count, double intervalSeconds, double startDelaySeconds) {
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

    @Override
    public String toString() {
        return "WaveData{" +
                "count=" + count +
                ", intervalSeconds=" + intervalSeconds +
                ", startDelaySeconds=" + startDelaySeconds +
                '}';
    }
}
