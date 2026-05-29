level_name = "Level 2"
lives = 20
resources = 20

main_path = {
    {x = -10,  y = 300},
    {x = 300, y = 300},
    {x = 300, y = 150},
    {x = 200, y = 150},
    {x = 200, y = 420},
    {x = 750, y = 420}
}

tower_spots = {
    {x = 120, y = 230},
    {x = 300, y = 230},
    {x = 400, y = 320},
    {x = 600, y = 330}
}

waves = {
    { count = 5, interval = 1.5, startDelay = 10.0,  enemyType = "goblin" },
    { count = 8, interval = 0.8, startDelay = 3.0, enemyType = "bat"    },
    { count = 3, interval = 2.0, startDelay = 20.0, enemyType = "goblin" },
    { count = 6, interval = 0.5, startDelay = 30.0, enemyType = "bat"    },
}