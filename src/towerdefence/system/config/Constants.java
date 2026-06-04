package towerdefence.system.config;

import java.util.List;

public class Constants {

    public static class Paths {
        public static final String LEVELS = "/lua/levels/";
        public static final String CONFIG = "/config.properties";
        public static final String LOG_FILE = "logs/game.log";
    }

    public static class Gameplay {
        public static final int LUA_ARRAY_START_INDEX = 1;
        public static final int TOWER_SPOT_SEARCH_DISTANCE_MULTIPLIER = 2;
        public static final double PATH_POINT_EPSILON = 1e-9;
        public static final String DEFAULT_ENEMY_TYPE = "goblin";
        public static final int MIN_WAVE_COUNT = 1;
        public static final double MIN_WAVE_INTERVAL_SECONDS = 0.0;
        public static final double MIN_WAVE_START_DELAY_SECONDS = 0.0;
    }

    public static class Timing {
        public static final double NANOS_PER_SECOND = 1_000_000_000.0;
        public static final double DEBUG_SPEEDUP_MIN = 0.1;
        public static final double DEBUG_SPEEDUP_MAX = 20.0;
        public static final double TOWER_AIM_STEP_SECONDS = 0.05;
        public static final double TOWER_AIM_MAX_LOOKAHEAD_SECONDS = 3.0;
    }

    public static class TowerConstants {
        public static final class Archer {
            public static final int COST = 10;
            public static final double RANGE = 120.0;
            public static final int DAMAGE = 5;
            public static final double COOLDOWN = 1;
            // visual
            public static final double WIDTH = 16.0 * 2;
            public static final double HEIGHT = 23.0 * 2;
        }

        public static final class Cannon {
            public static final int COST = 30;
            public static final double RANGE = 135.0;
            public static final int DAMAGE = 18;
            public static final double SPLASHRADIUS = 35;
            public static final double COOLDOWN = 2.2;
            // visual
            public static final double WIDTH = 18.0 * 2;
            public static final double HEIGHT = 30.0 * 2;
        }

        public static final class CrossBow {
            public static final int COST = 20;
            public static final double RANGE = 150.0;
            public static final int DAMAGE = 10;
            public static final double COOLDOWN = 0.5;
            // visual
            public static final double WIDTH = 18.0 * 2;
            public static final double HEIGHT = 33.0 * 2;
        }
    }

    public static class ProjectileConstants {
        public static final class Crossbow {
            public static final double SPEED = 90.0;
            // visual
            public static final double HEIGHT = 6.0 * 0.8;
            public static final double WIDTH = 13.0 * 0.8;
        }

        public static final class Archer {
            public static final double SPEED = 70.0;
            public static final double ARC_HEIGHT = 60.0;
            // visual
            public static final double HEIGHT = 6.0 * 0.8;
            public static final double WIDTH = 13.0 * 0.8;
        }

        public static final class CannonBall {
            public static final double SPEED = 70.0;
            public static final double ARC_HEIGHT = 60.0;
            // visual
            public static final double HEIGHT = 8.0;
            public static final double WIDTH = 8.0;
        }
    }

    public static class EnemyConstants {
        public static final class Goblin {
            public static final double SPEED = 30.0;
            public static final int HEALTH = 30;
            public static final int SCORE_VALUE = 10;
            public static final int BASE_DAMAGE = 2;
            // visual
            public static final double WIDTH = 11.0 * 2;
            public static final double HEIGHT = 8.0 * 2;
        }

        public static final class Bat {
            public static final double SPEED = 50.0;
            public static final int HEALTH = 10;
            public static final int SCORE_VALUE = 5;
            public static final int BASE_DAMAGE = 1;
            // visual
            public static final double WIDTH = 10.0 * 2;
            public static final double HEIGHT = 7.0 * 2;
        }

        public static final class BigSlime {
            public static final double SPEED = 20.0;
            public static final int HEALTH = 90;
            public static final int SCORE_VALUE = 25;
            public static final int BASE_DAMAGE = 3;
            // visual
            public static final double WIDTH = 14.0 * 2;
            public static final double HEIGHT = 8.0 * 2;
        }
    }

    public static class LevelConstants {
        public static final List<String> LEVEL_NAMES = List.of(
                "Level1.lua",
                "Level2.lua"
        );
    }
}