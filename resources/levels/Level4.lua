level_name = "The Catacombs"
level_file = "Level4.lua"
lives = 20
resources = 120

-- Slingerend pad door de catacomben
path0 = {
    {x=-1, y=3},
    {x=8,  y=3},
    {x=8,  y=10},
    {x=16, y=10},
    {x=16, y=5},
    {x=24, y=5},
    {x=24, y=14},
    {x=27, y=14}
}

-- Tower spots: bewust weinig ruimte, skeletten zijn immuun voor poison
tower_spots = {
    {x=4,  y=1},  {x=4,  y=5},   -- begin van het pad
    {x=6,  y=8},  {x=10, y=8},   -- eerste bocht
    {x=14, y=12}, {x=18, y=12},  -- midden
    {x=20, y=3},  {x=22, y=7},   -- tweede bocht
    {x=26, y=11},                 -- voor uitgang
}

waves = {
    -- Wave 1: paar goblins om te wennen
    { count = 6,  interval = 1.2, startDelay = 5.0,  enemyType = "goblin",   pathIndex = 0 },

    -- Wave 2: eerste skeletten — poison torens zijn nutteloos!
    { count = 5,  interval = 1.5, startDelay = 22.0, enemyType = "skeleton", pathIndex = 0 },
    { count = 4,  interval = 1.0, startDelay = 24.0, enemyType = "goblin",   pathIndex = 0 },

    -- Wave 3: mix — skeletten en bats tegelijk
    { count = 8,  interval = 1.0, startDelay = 44.0, enemyType = "skeleton", pathIndex = 0 },
    { count = 10, interval = 0.4, startDelay = 46.0, enemyType = "bat",      pathIndex = 0 },

    -- Wave 4: zware skeletten + bigslimes
    { count = 10, interval = 0.8, startDelay = 68.0, enemyType = "skeleton", pathIndex = 0 },
    { count = 2,  interval = 3.0, startDelay = 72.0, enemyType = "bigslime", pathIndex = 0 },

    -- Wave 5: eindgolf — volledig skeletten
    { count = 15, interval = 0.6, startDelay = 95.0, enemyType = "skeleton", pathIndex = 0 },
    { count = 4,  interval = 2.0, startDelay = 98.0, enemyType = "bigslime", pathIndex = 0 },
}