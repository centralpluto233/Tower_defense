level_name = "The Gauntlet"
level_file = "Level2.lua"
lives = 20
resources = 80

-- Slangend pad dwars door de map
path0 = {
    {x=-1, y=3},
    {x=10, y=3},
    {x=10, y=11},
    {x=3,  y=11},
    {x=3,  y=17},
    {x=18, y=17},
    {x=18, y=8},
    {x=27, y=8}
}

-- 9 spots, maar de slang maakt het lastig om alles te dekken
tower_spots = {
    {x=5,  y=1},  {x=8,  y=5},   -- langs eerste stuk
    {x=12, y=8},  {x=8,  y=13},  -- langs eerste bocht
    {x=1,  y=14}, {x=5,  y=19},  -- langs tweede bocht
    {x=13, y=15}, {x=20, y=11},  -- langs derde bocht
    {x=23, y=6},                  -- voor uitgang
}

waves = {
    { count = 8,  interval = 1.2, startDelay = 5.0,  enemyType = "goblin",   pathIndex = 0 },
    { count = 12, interval = 0.5, startDelay = 20.0, enemyType = "bat",      pathIndex = 0 },
    { count = 3,  interval = 2.5, startDelay = 38.0, enemyType = "bigslime", pathIndex = 0 },
    { count = 6,  interval = 0.7, startDelay = 42.0, enemyType = "goblin",   pathIndex = 0 },
    { count = 5,  interval = 1.5, startDelay = 60.0, enemyType = "bigslime", pathIndex = 0 },
    { count = 15, interval = 0.3, startDelay = 63.0, enemyType = "bat",      pathIndex = 0 },
}