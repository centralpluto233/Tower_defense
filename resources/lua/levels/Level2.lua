level_name = "Level 2"
lives = 20
resources = 150

main_path = {
    {x = -10,  y = 100},
    {x = 200,  y = 100},
    {x = 200,  y = 400},
    {x = 100,  y = 400},
    {x = 100,  y = 500},
    {x = 500,  y = 500},
    {x = 500,  y = 200},
    {x = 650,  y = 200},
    {x = 650,  y = 450},
    {x = 750,  y = 450},
}

tower_spots = {
    {x = 120,  y = 230},   -- inside first U-turn (between y=100 and y=400)
    {x = 320,  y = 430},   -- covers bottom stretch toward x=500
    {x = 320,  y = 570},   -- covers bottom stretch from below
    {x = 580,  y = 320},   -- inside second U-turn (between y=200 and y=500)
}

waves = {
    { count = 6,  interval = 1.2, startDelay = 5.0, enemyType = "goblin" },
    { count = 10, interval = 0.7, startDelay = 5.0, enemyType = "bat"    },
    { count = 5,  interval = 1.0, startDelay = 15.0, enemyType = "goblin" },
    { count = 8,  interval = 0.5, startDelay = 17.0, enemyType = "bat"    },
    { count = 4,  interval = 1.5, startDelay = 20.0, enemyType = "goblin" },
}