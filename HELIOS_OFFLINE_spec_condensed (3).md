# HELIOS//OFFLINE

**Group Members:**

Aman P | Rishi B | Pranay B | Eshaan S

---

## I. Game Overview

### Plot

Two government officials infiltrate a lab to find out why it recently shut down. Text-based adventure coded fully in Java, no external libraries or images. Built-in libraries like ArrayList are fine.

### Players

- **Medic** — 75 HP
- **Mercenary (Merc)** — 150 HP
- **Droid** — 50 HP, acquired in the Storage Room

Characters in the same room can move together or individually. Each group moves one room per turn. The player gets one input per character per turn.

#### Medic
- Starts with 10 bandages (heal target 20 HP) and 3 medkits (heal target to full).
- Healing costs their move that turn.

#### Merc
- Has a rifle. Shooting costs their move that turn.
- Starts with 30 bullets total. Unlimited reloading — just tracks bullet count.
- Only character who can carry `heavy = true` items.

#### Droid
- Activated early in the Storage Room; then acts like any other character.
- Can interact with `tech = true` doors, permanently unlocking them for all characters.
- **Drill** — found in the Storage Room. Lets the Droid clear `blockage = true` paths (one action).

---

### Movement
- Game runs in turns. Each character makes one action per turn: move, shoot, heal, search, or interact.

### Fights
- Entering a room with an enemy starts a fight.
- Each turn the enemy attacks one character in the room for its damage value.
- Only the Merc can shoot enemies. Shooting costs the Merc their move that turn.
- Enemy dies at 0 HP.

### Inputs
- Natural language. Game detects keywords: "shoot", "move", "heal", "search", "interact".
- One input per character per turn. Active character name printed before each prompt.
- Room descriptions shown on first entry and after state changes.

---

## II. Class Hierarchy

### Main
- `roomsPassed` (ArrayList\<Room\>) — visited rooms
- Manages turn loop, keyword detection, room description display.

### Character *(Abstract)*
- `health`, `maxHealth` (int); `carrying` (ArrayList\<Item\>); `currentRoom` (String)
- `move(Room destination)`, `collect(Item)`, `trade(Item, Character)`

### Mercenary *(extends Character)*
- `bullets` (int, starts 30), `canMoveThisTurn` (boolean)
- `shoot(Enemy)` — deals 10 damage, costs a bullet, sets `canMoveThisTurn = false`
- Can carry `heavy = true` items

### Medic *(extends Character)*
- `bandages` (int, starts 10), `medkits` (int, starts 3), `canMoveThisTurn` (boolean)
- `useBandage(Character)` — heals 20 HP, sets `canMoveThisTurn = false`
- `useMedkit(Character)` — full heal, sets `canMoveThisTurn = false`

### Droid *(extends Character)*
- `collected`, `hasDrill` (boolean)
- `interactTechLock(Room)` — permanently unlocks a `tech = true` door
- `clearBlockage(Room)` — requires `hasDrill == true`; permanently opens a `blockage = true` path

### Enemy *(Base)*
- `health`, `damage` (int); `currentRoom` (String)
- `attack(Character)` — deals damage each turn to one character in the room
- `takeDamage(int)`

#### DroneCluster — 60 HP, 15 damage/turn. Room: Lab. Drops Decryption Key on death.
#### ProtectorBot — 120 HP, 10 damage/turn. Spawns in Key Room after ConnectionsGame is solved.
#### Opus64 *(Boss Phase 1)* — 200 HP, 20 damage/turn. Standard fight; Merc shoots it down.
#### DroidBoss *(Boss Phase 2)* — Cannot be shot down. Merc uses "upload" action to win.

---

### Minigame: ConnectionsGame

Triggered in the Key Room. Player solves a NYT Connections-style puzzle to get the Decryption Key.

- 16 words in a 4×4 grid; player groups them into 4 categories of 4.
- `guessesRemaining` starts at 4. Prints "One away..." if 3 of 4 submitted words are correct.
- On success: awards Decryption Key, spawns Protector Bot. Retries allowed on failure.

```
[ CENTRIFUGE ] [ RELAY ]     [ OPUS ]   [ COLD ]
[ BEAKER ]     [ CAPACITOR ] [ HELIOS ] [ DARK ]
[ PIPETTE ]    [ RESISTOR ]  [ R3D3 ]   [ SILENT ]
[ BURETTE ]    [ DIODE ]     [ NEXUS ]  [ STILL ]

Guesses remaining: 4
Enter four words separated by commas:
```

Categories: Yellow — Lab glassware | Green — Circuit components | Blue — Facility codenames | Purple — Words describing the lab on arrival

---

## III. Rooms

**Path types:** normal (anyone) | `tech` — Droid must interact to open | `blockage` — Droid with Drill must clear

---

### Entrance
Two agents outside a sealed door. Radio: *"The lab went dark three days ago. Get in, find out what happened, get out."*

**Exits:** East → Lobby

---

### Lobby
Fluorescent lights. Receptionist dead at the desk. A locked door leads south.

**Exits:**
- West → Entrance
- South -!> Lab (tech — Droid must interact)
- East → Storage Room

---

### Storage Room
A dented robot labeled "R3D3" slumped in the corner.

**State Change — Droid Activated:** R3D3 powers on. New character available.

**Exits:** West → Lobby

**Items:** Battery Replacement (activates Droid when used on R3D3) | Drill (Droid picks up)

---

### Lab
Overturned tables, shattered glass. A drone swarm patrols the room.

**Exits:**
- North → Lobby
- West → Key Room
- South -!> Vault (blockage — Droid with Drill must clear)

**Enemy:** Drone Cluster — 60 HP, 15 damage/turn. Drops Decryption Key on death.

**Items:** Decryption Key (dropped by Drone Cluster)

---

### Key Room
A terminal displays a colorful word grid. A heavy magnet sits in the corner.

**State Change — Puzzle Solved:** Decryption Key awarded. Protector Bot spawns.

**Exits:** East → Lab

**Enemy:** Protector Bot — 120 HP, 10 damage/turn. Spawns after ConnectionsGame.

**Items:** Magnet (`heavy` — Merc only; needed to power generator)

---

### Generator Room
A dead turbine array. One component bay sits empty.

**State Change — Generator Powered:** Turbines roar. Control Room door unlocks.

**Exits:** South → Lab

**Notes:** Merc installs the Magnet (one action) to power the generator.

---

### Vault
A torn note: *"It was the AI. It locked us in. The kill key is here — use it."* A kill key behind glass.

**Exits:** North -!> Lab (blockage — Droid with Drill)

**Items:** Kill Key | Note (reveals Opus 6.4 caused the shutdown)

---

### Control Room
Every screen reads `HELIOS//ONLINE`. A server cluster hums at the center.

**Exits:** -!> Generator Room (requires generator powered + Decryption Key in Droid's inventory + Kill Key in party)

#### Phase 1 — Opus 6.4 (200 HP, 20 damage/turn)
Standard fight. Merc shoots, Medic heals. Opus 6.4 attacks one character each turn. Defeated at 0 HP.

#### Phase 2 — Droid Takeover
Immediately after Opus 6.4 dies, it uploads into the Droid. Droid turns hostile (20 damage/turn). Cannot be shot down. Merc uses "upload" while in the same room to transmit the Kill Code and win.

```
HELIOS//OFFLINE
```

---

## IV. Items

| Item | Carried By | Notes |
|------|------------|-------|
| Bandage ×10 | Medic | Heals 20 HP |
| Medkit ×3 | Medic | Full heal |
| Battery Replacement | Any | Activates Droid; Storage Room |
| Drill | Droid | Storage Room; clears blockages |
| Magnet | Merc only | `heavy`; Key Room; powers generator |
| Decryption Key | Droid | Dropped by Drone Cluster |
| Kill Key | Any | Found in Vault |

---

## V. Map & Flow

```
[Entrance] → [Lobby] → [Storage Room]
                |
             (tech)
                |
[Generator Room] ← [Lab] → [Key Room]
                     |
                 (blockage)
                     |
                   [Vault]
                     |
              [Control Room]
```

**Flow:**
1. Entrance → Lobby → Storage Room. Activate Droid, pick up Drill.
2. Droid opens Lab tech lock. All enter Lab.
3. Droid goes west to Key Room. Solve ConnectionsGame → Decryption Key. Fight Protector Bot. Merc picks up Magnet.
4. Merc takes Magnet north to Generator Room. Install → generator on.
5. Fight Drone Cluster in Lab → Decryption Key drops.
6. Droid clears blockage south → enter Vault, collect Kill Key.
7. Enter Control Room. Shoot Opus 6.4 to 0 HP. Then Merc uploads Kill Code to Droid.

```
HELIOS//OFFLINE
```
