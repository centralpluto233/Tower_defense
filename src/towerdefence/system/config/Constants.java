package towerdefence.system.config;

public class Constants {

    public static class Paths {
        public static final String LEVELS = "/lua/levels/";
        public static final String CONFIG = "/config.properties";
        public static final String LOG_FILE = "logs/game.log";
    }
    
    public static class TowerConstants {
        public static final class Archer {
            public static final double RANGE = 120.0;
            public static final int DAMAGE = 5;
            public static final double COOLDOWN = 0.7;
            public static final double WIDTH = 16.0*2;
            public static final double HEIGHT = 23.0*2;
            public static final int COST = 10;
        }
        
        public static final class CrossBow {
            public static final double RANGE = 150.0;
            public static final int DAMAGE = 10;
            public static final double COOLDOWN = 0.5;
            public static final double WIDTH = 18.0*2;
            public static final double HEIGHT = 33.0*2;
            public static final int COST = 20;
        }
    }

    public static class ProjectileConstants {
        public static final class Crossbow {
            public static final double HEIGHT=6.0*0.8;
            public static final double WIDTH=13.0*0.8;
            public static final double SPEED=90.0;
        }
    }
    
    public static class EnemyConstants {
        public static final class Goblin {
            public static final double WIDTH = 11.0*2;
            public static final double HEIGHT = 8.0*2;
            public static final double SPEED = 30.0;
            public static final int HEALTH = 30;
            public static final int SCORE_VALUE = 10;
            public static final int BASE_DAMAGE = 1;
        }
        
        public static final class Bat {
            public static final double WIDTH = 10.0*2;
            public static final double HEIGHT = 7.0*2;
            public static final double SPEED = 50.0;
            public static final int HEALTH = 10;
            public static final int SCORE_VALUE = 5;
            public static final int BASE_DAMAGE = 1;
        }
    }
}