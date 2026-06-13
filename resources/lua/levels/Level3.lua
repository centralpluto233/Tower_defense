level_name = "The Fork"
level_file = "Level3.lua"
lives = 20
resources = 175

-- Bovenste vork: links binnen op y=4, bocht naar beneden, uitgang y=14
path0 = {
    {x=-1,  y=4},
    {x=13,  y=4},
    {x=13,  y=14},
    {x=27,  y=14}
}

-- Onderste vork: links binnen op y=16, bocht naar boven, uitgang y=14
path1 = {
    {x=-1,  y=16},
    {x=13,  y=16},
    {x=13,  y=14},
    {x=27,  y=14}
}

tower_spots = {
    {x=3,  y=2},  {x=7,  y=2},  {x=11, y=2},   -- boven top pad
    {x=3,  y=10}, {x=7,  y=10}, {x=11, y=10},   -- tussen de twee paden
    {x=3,  y=18}, {x=7,  y=18}, {x=11, y=18},   -- onder bottom pad
    {x=17, y=12}, {x=17, y=16},
    {x=22, y=12}, {x=22, y=16},
    {x=15, y=6},  {x=15, y=18},
}

waves = {
    { count = 5,  interval = 1.2, startDelay = 5.0,  enemyType = "goblin",   pathIndex = 0 },
    { count = 5,  interval = 1.2, startDelay = 5.0,  enemyType = "goblin",   pathIndex = 1 },
    { count = 8,  interval = 0.6, startDelay = 20.0, enemyType = "bat",      pathIndex = 0 },
    { count = 8,  interval = 0.6, startDelay = 20.0, enemyType = "bat",      pathIndex = 1 },
    { count = 3,  interval = 2.0, startDelay = 38.0, enemyType = "bigslime", pathIndex = 0 },
    { count = 6,  interval = 0.8, startDelay = 38.0, enemyType = "goblin",   pathIndex = 1 },
    { count = 5,  interval = 1.0, startDelay = 55.0, enemyType = "bigslime", pathIndex = 0 },
    { count = 5,  interval = 1.0, startDelay = 55.0, enemyType = "bigslime", pathIndex = 1 },
    { count = 10, interval = 0.4, startDelay = 58.0, enemyType = "bat",      pathIndex = 0 },
    { count = 10, interval = 0.4, startDelay = 58.0, enemyType = "bat",      pathIndex = 1 },
}