level_name = "Level 1"
level_file = "Level1.lua"
lives = 20
resources = 10

main_path = {
    {x=-1, y=3},
    {x=8,  y=3},
    {x=8,  y=14},
    {x=21, y=14},
    {x=21, y=6},
    {x=29, y=6}
}

tower_spots = {
    {x=3,  y=2},  {x=3,  y=4},
    {x=6,  y=2},  {x=6,  y=4},
    {x=7,  y=8},  {x=9,  y=8},
    {x=7,  y=12}, {x=9,  y=12},
    {x=14, y=13}, {x=14, y=15},
    {x=18, y=13}, {x=18, y=15},
    {x=20, y=4},  {x=22, y=4},
    {x=20, y=8},  {x=22, y=8},
    {x=25, y=5},  {x=25, y=7},
}

waves = {
    { count = 5, interval = 1.5, startDelay = 3.0,  enemyType = "goblin" },
    { count = 8, interval = 0.8, startDelay = 10.0, enemyType = "bat"    },
    { count = 3, interval = 2.0, startDelay = 20.0, enemyType = "goblin" },
    { count = 6, interval = 0.5, startDelay = 30.0, enemyType = "bat"    },
}