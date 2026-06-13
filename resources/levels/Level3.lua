level_name = "Two Fronts"
level_file = "Level3.lua"
lives = 20
resources = 100

-- Bovenpad
path0 = {
    {x=-1, y=4},
    {x=12, y=4},
    {x=12, y=13},
    {x=27, y=13}
}

-- Onderpad
path1 = {
    {x=-1,  y=17},
    {x=15,  y=17},
    {x=15,  y=13},
    {x=27,  y=13}
}

-- Slechts 8 spots voor 2 paden → elke keuze is cruciaal
tower_spots = {
    {x=5,  y=6},  {x=9,  y=6},   -- boven pad0
    {x=12,  y=15},  -- tussen de paden
    {x=5,  y=19}, {x=10, y=19},  -- onder pad1
    {x=19, y=11}, {x=23, y=11},  -- voor gedeelde uitgang
}

waves = {
    -- Wave 1: goblins op beide paden tegelijk
    { count = 6,  interval = 1.0, startDelay = 5.0,  enemyType = "goblin",   pathIndex = 0 },
    { count = 6,  interval = 1.0, startDelay = 5.0,  enemyType = "goblin",   pathIndex = 1 },
    -- Wave 2: bats snel op één pad
    { count = 12, interval = 0.4, startDelay = 22.0, enemyType = "bat",      pathIndex = 0 },
    { count = 4,  interval = 2.0, startDelay = 22.0, enemyType = "bigslime", pathIndex = 1 },
    -- Wave 3: bigslimes op beide paden
    { count = 4,  interval = 2.5, startDelay = 42.0, enemyType = "bigslime", pathIndex = 0 },
    { count = 4,  interval = 2.5, startDelay = 42.0, enemyType = "bigslime", pathIndex = 1 },
    -- Wave 4: alles tegelijk
    { count = 10, interval = 0.4, startDelay = 65.0, enemyType = "bat",      pathIndex = 0 },
    { count = 10, interval = 0.4, startDelay = 65.0, enemyType = "bat",      pathIndex = 1 },
    { count = 3,  interval = 3.0, startDelay = 68.0, enemyType = "bigslime", pathIndex = 0 },
    { count = 3,  interval = 3.0, startDelay = 68.0, enemyType = "bigslime", pathIndex = 1 },
}