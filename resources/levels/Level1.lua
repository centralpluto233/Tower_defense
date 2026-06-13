level_name = "The Crossing"
level_file = "Level1.lua"
lives = 20
resources = 60

path0 = {
    {x=-1, y=5},
    {x=8,  y=5},
    {x=8,  y=13},
    {x=20, y=13},
    {x=20, y=6},
    {x=29, y=6}
}

-- 8 spots, strategisch langs de bochten
tower_spots = {
    {x=5,  y=3},  {x=5,  y=7},   -- voor eerste bocht
    {x=6,  y=10}, {x=10, y=10},  -- langs verticaal stuk
    {x=14, y=11}, {x=14, y=15},  -- langs horizontaal stuk
    {x=22, y=4},  {x=22, y=8},   -- voor laatste bocht
}

waves = {
    { count = 6,  interval = 1.5, startDelay = 5.0,  enemyType = "goblin", pathIndex = 0 },
    { count = 10, interval = 0.8, startDelay = 18.0, enemyType = "bat",    pathIndex = 0 },
    { count = 4,  interval = 2.0, startDelay = 35.0, enemyType = "goblin", pathIndex = 0 },
    { count = 5,  interval = 0.5, startDelay = 35.0, enemyType = "bat",    pathIndex = 0 },
    { count = 3,  interval = 3.0, startDelay = 55.0, enemyType = "bigslime", pathIndex = 0 },
}