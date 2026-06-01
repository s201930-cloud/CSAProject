# HELIOS//OFFLINE

**Group Members:** Aman P | Rishi B | Pranay B | Eshaan S

---

## Overview

Two agents infiltrate a dead lab. Text-based adventure in Java, no external libraries.

**Characters:**
- **Medic** (75 HP) — heals allies. Starts with 10 bandages (+20 HP) and 3 medkits (full heal). Healing costs their action that turn.
- **Merc** (150 HP) — only one who can shoot enemies. 30 bullets total. Shooting costs their move.
- **Droid** (50 HP) — unlocks tech doors and clears blockages. Found in Storage Room.

One action per character per turn. Input is natural language — detect keywords like "move", "shoot", "heal", "search", "interact". Print the active character's name before each input.

**Fights:** When a character enters a room with an enemy, a fight starts. The enemy deals damage to one character in the room each turn. Only the Merc can shoot. Enemy dies at 0 HP. Room description updates after a fight.

---

## Classes

**Character** *(abstract)* — `health`, `maxHealth`, `carrying` (ArrayList\<Item\>), `currentRoom`. Methods: `move()`, `collect()`, `trade()`.

**Merc** *(extends Character)* — `bullets` (30), `canMoveThisTurn`. `shoot(Enemy)` deals 10 damage, costs a bullet, sets `canMoveThisTurn = false`.

**Medic** *(extends Character)* — `bandages` (10), `medkits` (3), `canMoveThisTurn`. `useBandage()` heals 20 HP; `useMedkit()` heals to full. Both set `canMoveThisTurn = false`.

**Droid** *(extends Character)* — `hasDrill`. `unlockDoor(Room)` permanently opens a tech-locked door. `clearBlockage(Room)` requires `hasDrill`.

**Enemy** *(base)* — `health`, `damage`, `currentRoom`. `attack(Character)`, `takeDamage(int)`.
- **DroneCluster** — 60 HP, 15 dmg/turn. Drops Decryption Key on death.
- **ProtectorBot** — 120 HP, 10 dmg/turn. Spawns in Key Room after puzzle.
- **Opus64** *(boss)* — 200 HP, 20 dmg/turn. Normal fight. On death → Phase 2.
- **DroidBoss** *(boss phase 2)* — Droid turns hostile, 20 dmg/turn. Can't be shot. Merc types "upload" in same room to win.

**ConnectionsGame** *(minigame)* — 16 words, 4 categories of 4. Player submits groups of 4 comma-separated words. 4 guesses. "One away" hint if 3/4 correct. Awards Decryption Key on success; spawns ProtectorBot.

---

## Rooms

**Entrance** — Starting room. East → Lobby.

**Lobby** — South door is tech-locked (Droid opens it). East → Storage Room. South → Lab (tech).

**Storage Room** — R3D3 is here. Insert Battery Replacement to activate Droid. Also contains the Drill (Droid picks up). West → Lobby.

**Lab** — Drone Cluster patrols here. West → Key Room. South → Vault *(blockage — Droid + Drill)*. North → Lobby.

**Key Room** — Terminal with ConnectionsGame. Solve it for the Decryption Key; ProtectorBot spawns. Contains Magnet (heavy — Merc only). East → Lab.

**Generator Room** — Merc installs Magnet here (one action) to power the generator. South → Lab.

**Vault** — Contains Kill Key and a note (*"It was the AI — stop it"*). North → Lab *(blockage cleared)*.

**Control Room** — Requires: generator on + Decryption Key (Droid) + Kill Key. Boss fight here. Exit → Generator Room.

---

## Items

| Item | Who | Effect |
|------|-----|--------|
| Battery Replacement | Any | Activates Droid |
| Drill | Droid | Clears blockages |
| Magnet | Merc only | Powers generator (install = 1 action) |
| Decryption Key | Droid | Required for Control Room |
| Kill Key | Any | Required for Control Room |
| Bandage ×10 | Medic | +20 HP |
| Medkit ×3 | Medic | Full heal |

---

## Flow

1. Storage Room → activate Droid, grab Drill.
2. Droid opens Lab tech door from Lobby.
3. Droid → Key Room. Solve puzzle → Decryption Key. Fight ProtectorBot. Merc grabs Magnet.
4. Merc → Generator Room. Install Magnet.
5. Fight Drone Cluster in Lab → Decryption Key drops.
6. Droid clears blockage → Vault → Kill Key.
7. Enter Control Room. Shoot Opus 6.4. Then Merc uploads Kill Code to Droid.

```
HELIOS//OFFLINE
```
