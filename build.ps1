$projectRoot = $PSScriptRoot
$artifacts = "$projectRoot\out\artifacts\tower_defense_jar"

Remove-Item -Recurse -Force "$artifacts\TowerDefense" -ErrorAction SilentlyContinue
Set-Location $artifacts
jpackage --input . --name TowerDefense --main-jar tower-defense.jar --main-class towerdefence.Main --type app-image --dest .
Copy-Item -Recurse -Force "$projectRoot\config" ".\TowerDefense\config"
Write-Host "Klaar: $artifacts\TowerDefense\TowerDefense.exe"