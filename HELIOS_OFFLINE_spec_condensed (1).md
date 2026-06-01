# HELIOS//OFFLINE

**Group Members:**

Aman P | Rishi B | Pranay B | Eshaan S

---

## I. Game Overview

### Plot

Two government officials infiltrate a lab to find out why it recently shut down. The game is a text-based adventure coded fully in Java without external libraries or images. Built-in libraries like ArrayList are fine. The game is stylized with text only; ASCII art is okay for minigame displays.

### Players

Players have access to two, eventually three characters:

- **Medic** — 75 HP
- **Mercenary (Merc)** — 150 HP
- **Droid** — 50 HP, acquired in the Storage Room

Characters in the same room can be moved together or individually. Each collection can only move one room per turn.

#### Medic
- Cannot move through paths marked `loud = true`.
- Starts with 10 bandages (heal target 20 HP) and 3 medkits (heal target to full HP).
- Healing costs the Medic their move that turn.

#### Merc
- Has a rifle. Shooting costs the Merc their move that turn.
- Starts with 5 magazines of 9 bullets each. Running out of bullets requires a full turn to reload.
- Cannot move through paths marked `tunnel = true`.
- Only character who can carry items marked `heavy = true`.

#### Droid
- Collected early in the game; once activated, acts like any other character.
- Can interact with tech locks (`tech = true`), permanently opening that path for all characters.
- **Arm Attachment** — found as a single item in the Storage Room. Equipping it (one turn) allows the Droid to move through normal doors without another character accompanying it.
- **Drill** — crafted from the Blade (found in Storage Room) once the Arm Attachment is equipped (one turn). Allows the Droid to clear paths marked `blockage = true`.
- **Decryption Keys** — the Droid must collect 2 keys to unlock the boss door. The door cannot be entered without both.
- Without the Arm Attachment, the Droid cannot move through normal doors alone.
- The Droid and Medic can both pass through `tunnel = true` paths.

---

### Movement

- The game runs in turns. Each character makes one action per turn.
- Radiation affects specific rooms. Staying more than 5 turns in a radiation room deals 10 HP per turn until the character leaves.

---

### Fights

- Entering a room with an enemy starts a fight. Radiation pauses during fights.
- Characters can move between rooms during a fight. Enemies follow the nearest character and can only move one room every two turns.
- Only the Merc can deal damage to enemies.

---

### Inputs

- Natural language input. The game detects keywords ("shoot", "move", "heal", "search", "interact").
- 3 inputs per turn — one per available character.
- The active character's name is printed before each input prompt.
- Room descriptions display on first entry, and again whenever the room's state changes.

---

## II. Class Hierarchy

### Main
Controls the game loop and player input.
- `roomsPassed` (ArrayList\<Room\>) — rooms visited.
- Tracks 3 actions per turn, prints active character name before each prompt.

---

### Character *(Abstract)*
- `health`, `maxHealth` (int)
- `carrying` (ArrayList\<Item\>)
- `currentRoom` (String)
- `move(Path path)` — returns false if restricted. Overridden by subclasses.
- `collect(Item item)`, `trade(Item item, Character target)`

---

### Mercenary *(extends Character)*
- `magazines` (int, starts 5), `bulletsInCurrentMag` (int, starts 9), `canMoveThisTurn` (boolean)
- `shoot(Enemy enemy)` — consumes a bullet, sets `canMoveThisTurn = false`.
- `reload()` — costs full turn.
- `move(Path path)` — blocked if `path.tunnel == true`.
- Can carry `heavy = true` items.

---

### Medic *(extends Character)*
- `bandages` (int, starts 10), `medkits` (int, starts 3), `canMoveThisTurn` (boolean)
- `useBandage(Character target)` — heals 20 HP, sets `canMoveThisTurn = false`.
- `useMedkit(Character target)` — heals to full, sets `canMoveThisTurn = false`.
- `move(Path path)` — blocked if `path.loud == true`.

---

### Droid *(extends Character)*
- `collected` (boolean), `hasArmAttachment` (boolean), `hasDrill` (boolean), `decryptionKeys` (int)
- `interactTechLock(Path path)` — permanently unlocks a `tech = true` path.
- `equipArm()` — equips the Arm Attachment from inventory. One turn. Sets `hasArmAttachment = true`.
- `craftDrill()` — requires Arm Attachment + Blade in inventory. One turn. Sets `hasDrill = true`.
- `clearBlockage(Path path)` — requires `hasDrill == true`.
- `move(Path path)` — blocked by normal doors if `!hasArmAttachment` and no other character is present.

---

### Enemy *(Base Class)*
- `health`, `maxHealth`, `damage` (int), `currentRoom` (String), `turnCounter` (int), `active` (boolean)
- `move()` — one room toward nearest character; once every two turns.
- `attack(Character target)`, `takeDamage(int amount)`

#### MiterSaw *(extends Enemy)* — 50 HP, 10 damage. Starting room: Lab. Deals damage if any character stays in its room more than 1 consecutive turn.
#### DroneCluster *(extends Enemy)* — 100 HP, 20 damage. Starting room: Walkway. Drops Decryption Key #2 on death.
#### ProtectorBot *(extends Enemy)* — 200 HP, 10 damage. Spawns in Key Room after ConnectionsGame is completed.
#### Opus64 *(extends Enemy — Boss Phase 1)* — 100 HP, 30 damage. Weak spot opens after 5 turns; Merc hits it from lanes 2–3 for 20 damage.
#### DroidBoss *(extends Enemy — Boss Phase 2)* — 50 HP, 40 damage. Defeated by Kill Code upload, not HP.

---

### Minigame Classes

#### ConnectFour
Triggered in the Walkway after the Drone Cluster is defeated. Player beats a bot at Connect 4 to unlock the Vault path.
- `board` (char\[\]\[\], 6×7), `currentTurn`, `gameOver`, `winner`
- `displayBoard()`, `dropPiece(int col, char piece)`, `checkWin(char piece)`, `checkDraw()`, `botMove()`, `playGame()` — returns true on player win. Player may retry on loss.

```
1 2 3 4 5 6 7
| . . . . . . . |
| . . . . . . . |
| . . . X . . . |
| . . O X . . . |
| . X O O X . . |
```

#### ConnectionsGame
Triggered in the Key Room. Droid solves a NYT Connections-style puzzle to retrieve Decryption Key #1.
- 16 words, 4 categories of 4. `guessesRemaining` starts at 4.
- `displayGrid()`, `submitGuess(String[] fourWords)`, `checkOneAway()`, `playGame()` — returns true on success, spawns Protector Bot.

```
[ CENTRIFUGE ] [ RELAY ] [ OPUS ] [ COLD ]
[ BEAKER ] [ CAPACITOR ] [ HELIOS ] [ DARK ]
[ PIPETTE ] [ RESISTOR ] [ R3D3 ] [ SILENT ]
[ BURETTE ] [ DIODE ] [ NEXUS ] [ STILL ]

Guesses remaining: 4
Enter four words separated by commas:
```

Categories: Yellow — *Lab glassware* | Green — *Circuit components* | Blue — *Facility codenames* | Purple — *Words describing the lab on arrival*

---

## III. Room Definitions

**Path flags:**
- `->` Open.
- `-!>` Restricted (type noted in parentheses).
- `loud = true` — Medic blocked.
- `tunnel = true` — Merc blocked. Droid and Medic can pass.
- `tech = true` — Droid must interact to permanently open for all.
- `blockage = true` — Droid with Drill required.

`[Characters Selected]` is replaced at runtime with the names of characters being controlled.

---

### Entrance

The two agents stand outside a large sealed door with a glowing keypad. Their radio crackles: *"Three days ago this lab went dark. Get in, find out what happened, get out. Good luck."* The radio cuts out.

**Connections:** East → Lobby

---

### Lobby

Buzzing fluorescent lights. A receptionist is slumped dead at the desk. A post-it note on the monitor reads "Key" — followed by the tech lock panel on the south door.

**Connections:**
- West → Entrance
- South -!> Lab (tech = true — Droid must interact with keypad to unlock)
- East -!> Storage Room (tunnel = true — Droid and Medic only)

**Items:** None

**Notes:** The Droid is needed to open the Lab door, which incentivizes retrieving it from the Storage Room first.

---

### Storage Room

Emergency lighting flickers. A small dented robot sits slumped against a crate — label reads "R3D3". It just needs power.

**State Change — R3D3 Activated:** The robot shudders online. A new character is now available.

**Connections:** West -!> Lobby (tunnel = true)

**Items:**
- Battery Replacement (visible on shelf — insert into R3D3 to activate Droid)
- Arm Attachment (on a tool rack — Droid can equip it; one turn)
- Blade (hidden under collapsed shelf — found by searching)

---

### Lab

Examination tables overturned, containment units shattered. Equipment is still running, logging data nobody will ever read. A miter saw on the central worktable spins on its own, trailing sparks.

**State Change — Miter Saw Defeated:** The blade grinds to a halt. Something glints in the debris near the far wall.

**Connections:**
- North -!> Lobby (tech = true — unlocked when Droid first opened the south door in Lobby)
- East -!> Generator Room (loud = true — Medic blocked)
- South -!> Walkway (blockage = true — Droid with Drill required)
- West -!> Key Room (tunnel = true — Droid only)

**Enemy:** Miter Saw — 50 HP, 10 damage. Deals damage if any character stays more than 1 turn.

**Items:** Decryption Key #2 (found by searching after Miter Saw is defeated)

---

### Generator Room

A massive turbine array, dead and still. Component bays sit empty.

**State Change — Generator Powered:** Turbines roar to life. Something unlocks deeper in the facility.

**Connections:** West -!> Lab (loud = true — Medic blocked)

**Items:** None

**Notes:** The Merc brings the Magnet (heavy = true) from the Key Room and installs it here (one action) to power the generator, unlocking the boss door. This is the only required task in this room.

---

### Key Room

A cramped room. A terminal displays a colorful word grid. A heavy industrial magnet sits in a reinforced cradle — far too heavy for most to move.

**State Change — Puzzle Solved / Protector Bot Spawned:** The compartment clicks open revealing a metallic card. A panel splits open and a security bot steps out, optical sensors locking on.

**Connections:** East -!> Lab (tunnel = true — Droid only)

**Enemy:** Protector Bot — 200 HP, 10 damage. Spawns after ConnectionsGame is completed.

**Items:**
- Decryption Key #1 (behind ConnectionsGame puzzle)
- Magnet (heavy = true — Merc only; needed to power generator)

---

### Walkway

A narrow suspended walkway over a dark shaft. Ozone in the air, something pulsing blue far below. A drone swarm hovers in the center, red sensors sweeping.

**State Change — Drone Cluster Defeated:** Drones crash into the shaft. A terminal at the south end flickers to life.

**Connections:**
- North -!> Lab (blockage = true — Droid with Drill)
- South -!> Vault (Drone Cluster defeated + player wins ConnectFour minigame)

**Enemy:** Drone Cluster — 100 HP, 20 damage.

**Items:** Decryption Key #2 (drops from Drone Cluster on defeat)

**Notes:** After the Drone Cluster is defeated, the ConnectFour terminal activates. Player must win to unlock the Vault path. Retries allowed.

---

### Vault

Reinforced walls. A cracked case holds a torn note: *"It was the AI. It locked us in. We had no way out. If you're reading this — stop it. The kill key is in here."* A glowing kill key sits behind a glass panel.

**Connections:** North → Walkway

**Items:**
- Kill Key (required to enter the Control Room)
- Note (mission update: Opus 6.4 caused the shutdown — destroy it to win)

---

### Control Room

Every screen cycles the same text:
```
HELIOS//ONLINE
HELIOS//ONLINE
HELIOS//ONLINE
```
A towering server cluster pulses red. Four lanes are marked on the floor in yellow paint.

**Connections:** West -!> Lab (requires: generator powered AND Droid has both Decryption Keys AND Kill Key in party inventory)

#### Boss — Phase 1: Opus 6.4 (100 HP)
- Fight takes place across 4 lanes. Characters move one adjacent lane per turn.
- Staying in the same lane 3+ turns triggers radiation: 10 HP/turn.
- Each turn, Opus 6.4 primes a lane. Next turn, it fires a beam (30 damage) at that lane. Primed lane is announced to the player.
- After 5 turns, a weak spot opens. Merc can shoot it from lanes 2 or 3 only — 20 damage per shot.

#### Boss — Phase 2: Droid Takeover
Opus 6.4 uploads into the Droid immediately on defeat. Fight continues in the same arena.
- Droid deals 40 damage per beam.
- Cannot be reduced to 0 HP — Merc must be in the same lane as the Droid and use "upload" to win.

**Win Sequence:** The Droid goes still. Screens go dark one by one.
```
HELIOS//OFFLINE
```
Mission complete.

---

## IV. Item Reference

| Item | Carried By | Notes |
|------|------------|-------|
| Bandage (×10) | Medic only | Heals 20 HP |
| Medkit (×3) | Medic only | Heals to full |
| Magazine (×5) | Merc only | 9 bullets each |
| Battery Replacement | Any | Activates R3D3; found in Storage Room |
| Arm Attachment | Droid only | Found in Storage Room; equip costs 1 turn |
| Blade | Any | Found in Storage Room; used to craft Drill |
| Drill | Droid only | Crafted from Blade + Arm Attachment; costs 1 turn |
| Magnet | Merc only | `heavy = true`; found in Key Room; powers generator |
| Decryption Key #1 | Droid only | Awarded after ConnectionsGame in Key Room |
| Decryption Key #2 | Droid only | Drops from Drone Cluster in Walkway |
| Kill Key | Any | Found in Vault |

---

## V. Room Map

```
[Entrance] -> [Lobby] -tunnel-> [Storage Room]
                  |
               (tech)
                  |
[Generator Room] <-loud- [Lab] -tunnel-> [Key Room]
                           |
                        (blockage)
                           |
                        [Walkway]
                           |
                       (ConnectFour)
                           |
                         [Vault]
                           |
                    (keys + generator)
                           |
                     [Control Room]
```

**Flow:**
1. Entrance → Lobby. Medic/Droid go east through tunnel to Storage Room.
2. Activate Droid. Collect Arm Attachment, Blade. Equip Arm Attachment. Craft Drill.
3. Droid opens Lab tech lock from Lobby. All characters enter Lab.
4. Fight Miter Saw. Search for Decryption Key #2.
5. Droid goes west (tunnel) to Key Room. Solve ConnectionsGame → Decryption Key #1. Fight Protector Bot. Merc picks up Magnet.
6. Merc takes Magnet east through Lab to Generator Room. Install Magnet → generator powered.
7. Droid clears blockage south of Lab. Enter Walkway. Fight Drone Cluster → Decryption Key #2. Win ConnectFour → Vault.
8. Collect Kill Key from Vault.
9. Enter Control Room. Defeat Opus 6.4 (Phase 1), then upload Kill Code to Droid (Phase 2).
```
HELIOS//OFFLINE
```
