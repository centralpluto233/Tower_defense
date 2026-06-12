level_name = "Level 2"
level_file = "Level2.lua"
lives = 20
resources = 150

main_path = {
    {x=-1, y=4},
    {x=25, y=4},
    {x=25, y=8},
    {x=5,  y=8},
    {x=5,  y=14},
    {x=27, y=14},
    {x=27, y=14}
}

tower_spots = {
    {x=8,  y=5},  {x=8,  y=2},
    {x=15, y=5},  {x=15, y=2},
    {x=22, y=5},  {x=22, y=2},
    {x=24, y=5},  {x=26, y=2},
    {x=24, y=9},  {x=26, y=9},
    {x=10, y=7},  {x=10, y=9},
    {x=16, y=7},  {x=16, y=9},
    {x=6,  y=11}, {x=4,  y=11},
    {x=12, y=13}, {x=12, y=15},
    {x=20, y=13}, {x=20, y=15},
}

waves = {
    { count = 6,  interval = 1.2, startDelay = 5.0, enemyType = "goblin" },
    { count = 10, interval = 0.7, startDelay = 5.0, enemyType = "bat"    },
    { count = 5,  interval = 1.0, startDelay = 15.0, enemyType = "goblin" },
    { count = 8,  interval = 0.5, startDelay = 17.0, enemyType = "bat"    },
    { count = 4,  interval = 1.5, startDelay = 20.0, enemyType = "bigslime" },
    { count = 4,  interval = 1.5, startDelay = 20.0, enemyType = "goblin" },
}