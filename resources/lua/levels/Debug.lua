level_name = "Debug"
level_file = "Debug.lua"
lives = 2
resources = 2000

path0 = {
    {x=-1,  y=10},
    {x=27,  y=10}
}

path1 = {
    {x=-1, y=15},
    {x=18, y=15},
    {x=18, y=10},
    {x=27, y=10}
}

path2 = {
    {x= -1, y=13},
    {x= 18, y=13},
    {x= 18, y=9},
    {x= 27, y=9}
}

path3 = {
    {x=-1,  y=10},
    {x=18,  y=10},
    {x=18,  y=14},
    {x=27,  y=14}
}

tower_spots = {
    {x=3,  y=8},  {x=3,  y=11},
    {x=8,  y=8},  {x=8,  y=11},
    {x=14, y=8},  {x=14, y=11},
    {x=20, y=8},  {x=20, y=11},
    {x=26, y=8},  {x=26, y=11},
}

waves = {
    { count = 2, interval = 1.5, startDelay = 0,  enemyType = "bat", pathIndex = 0 },
    { count = 2, interval = 1.5, startDelay = 0.5,  enemyType = "bat", pathIndex = 1 },
}