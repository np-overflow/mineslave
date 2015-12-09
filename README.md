# mineslave
This is a Bukkit plugin meant to run on Bukkit/Spigot servers. It receives commands from `minecraft-commander`.

## Installation
1. Clone the project
2. Open it in IntelliJ
3. Build the artifacts
4. Place the `lib/` folder and the JAR file in the `plugins/` directory of your Bukkit server

## In-game Commands
`/revert`  
Revert a player's changes.
_Supports block placements and entity spawns._

`/replay`  
Replay a player's changes.
_Supports block placements only._

## Libraries
This project depends on the following libraries:
- [Google GSON](https://github.com/google/gson)
- [Websockets](https://github.com/TooTallNate/Java-WebSocket)
