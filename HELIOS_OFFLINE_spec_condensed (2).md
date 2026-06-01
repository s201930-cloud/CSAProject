# HELIOS//OFFLINE

**Group Members:**

Aman P | Rishi B | Pranay B | Eshaan S

---

## I. Game Overview

### Plot

Two government officials infiltrate a lab to find out why it recently shut down. Text-based adventure coded fully in Java, no external libraries. ASCII art is okay for minigame displays.

### Players

- **Medic** — 75 HP
- **Mercenary (Merc)** — 150 HP
- **Droid** — 50 HP, acquired in the Storage Room

Characters in the same room can move together or individually. Each group moves one room per turn.

#### Medic
- Blocked by `loud = true` paths.
- Starts with 10 bandages (heal 20 HP) and 3 medkits (heal to full). Healing costs their move that turn.

#### Merc
- Has a rifle. Shooting costs their move that turn.
- Starts with 5 magazines of 9 bullets. Running out requires a full turn to reload.
- Blocked by `tunnel = true` paths.
- Only character who can carry `heavy = true` items.

#### Droid
- Activated early; then acts like any other character.
- Can interact with `tech = true` locks, permanently opening that path for all.
- **Arm Attachment** — found in Storage Room. Equipping it (one turn) lets the Droid move through normal doors alone.
- **Drill** — crafted from the Blade (Storage Room) once Arm Attachment is equipped (one turn). Clears `blockage = true` paths.
- Without the Arm Attachment, the Droid can't use normal doors alone.
- Droid and Medic can both use `tunnel = true` paths.

---

### Movement
- Game runs in turns. Each character makes one action per turn.
- Radiation rooms: staying 5+ turns deals 10 HP/turn until the character leaves.

### Fights
- Entering a room with an enemy starts a fight. Radiation pauses.
- Enemies follow the nearest character, moving one room every two turns.
- Only the Merc can deal damage.

### Inputs
- Natural language. Game detects keywords: "shoot", "move", "heal", "search", "interact".
- 3 inputs per turn (one per character). Active character name printed before each prompt.
- Room descriptions shown on first entry and after state changes.

---

## II. Class Hierarchy

### Main
- `roomsPassed` (ArrayList\<Room\>)
- Manages 3-action turn loop, keyword detection, room description display.

### Character *(Abstract)*
- `health`, `maxHealth` (int); `carrying` (ArrayList\<Item\>); `currentRoom` (String)
- `move(Path)`, `collect(Item)`, `trade(Item, Character)`

### Mercenary *(extends Character)*
- `magazines` (5), `bulletsInCurrentMag` (9), `canMoveThisTurn` (boolean)
- `shoot(Enemy)` — consumes bullet, sets `canMoveThisTurn = false`
- `reload()` — full turn
- Blocked by `tunnel = true`; can carry `heavy = true` items

### Medic *(extends Character)*
- `bandages` (10), `medkits` (3), `canMoveThisTurn` (boolean)
- `useBandage(Character)` — 20 HP, sets `canMoveThisTurn = false`
- `useMedkit(Character)` — full heal, sets `canMoveThisTurn = false`
- Blocked by `loud = true`

### Droid *(extends Character)*
- `collected`, `hasArmAttachment`, `hasDrill` (boolean)
- `interactTechLock(Path)` — permanently opens `tech = true` path
- `equipArm()` — one turn, sets `hasArmAttachment = true`
- `craftDrill()` — requires Arm Attachment + Blade in inventory; one turn
- `clearBlockage(Path)` — requires Drill
- Blocked by normal doors if `!hasArmAttachment` and alone

### Enemy *(Base)*
- `health`, `damage` (int); `currentRoom` (String); `active` (boolean)
- `move()` — toward nearest character, once every two turns
- `attack(Character)`, `takeDamage(int)`

#### DroneCluster — 100 HP, 20 damage. Room: Lab. Drops Decryption Key on death.
#### ProtectorBot — 200 HP, 10 damage. Spawns in Key Room after ConnectionsGame is solved.
#### Opus64 *(Boss)* — 300 HP, 25 damage. Attacks the character in the room each turn. Merc shoots it down normally. Defeated when HP reaches 0; triggers Droid Takeover.
#### DroidBoss *(Boss Phase 2)* — defeated by uploading the Kill Code (Merc uses "upload" action while in same room as Droid).

---

### Minigame: ConnectionsGame

Triggered in the Key Room. Droid solves a NYT Connections-style puzzle.

- 16 words in a 4×4 grid; player groups them into 4 categories of 4.
- `guessesRemaining` starts at 4. Prints "One away..." if 3 of 4 words are correct.
- On success: spawns Protector Bot. Retries allowed on failure.

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

## III. Room Definitions

**Path flags:** `->` open | `-!>` restricted | `loud` — Medic blocked | `tunnel` — Merc blocked | `tech` — Droid unlock | `blockage` — Droid + Drill required

---

### Entrance
Two agents outside a sealed door. Radio: *"The lab went dark three days ago. Get in, find out what happened, get out."*

**Connections:** East → Lobby

---

### Lobby
Fluorescent lights buzzing. Receptionist dead at the desk. A keypad glows on the south door.

**Connections:**
- West → Entrance
- South -!> Lab (tech — Droid must interact to unlock)
- East -!> Storage Room (tunnel — Droid and Medic only)

---

### Storage Room
Dim emergency lighting. A dented robot labeled "R3D3" slumped against a crate.

**State Change — Droid Activated:** R3D3 shudders online. New character available.

**Connections:** West -!> Lobby (tunnel)

**Items:** Battery Replacement (activates Droid) | Arm Attachment | Blade

---

### Lab
Overturned tables, shattered glass. Equipment still running. A drone swarm drifts near the ceiling, sensors sweeping.

**Connections:**
- North -!> Lobby (tech — already unlocked when Droid opened Lab door)
- West -!> Key Room (tunnel — Droid only)
- South -!> Vault (blockage — Droid + Drill required)

**Enemy:** Drone Cluster — 100 HP, 20 damage. Drops Decryption Key on defeat.

**Items:** Decryption Key (dropped by Drone Cluster)

---

### Key Room
Cramped room. A terminal displays a colorful word grid.

**State Change — Puzzle Solved / Protector Bot Spawned:** A compartment opens. A security bot steps out of the wall.

**Connections:** East -!> Lab (tunnel — Droid only)

**Enemy:** Protector Bot — 200 HP, 10 damage. Spawns after ConnectionsGame is completed.

**Items:** Magnet (heavy — Merc only; powers generator)

---

### Generator Room
A massive dead turbine array. Component bays sit empty.

**State Change — Generator Powered:** Turbines roar to life. The Control Room door unlocks.

**Connections:** East -!> Lab (loud — Medic blocked)

**Notes:** Merc installs the Magnet here (one action) to power the generator, which is required to enter the Control Room.

---

### Vault
Reinforced room. A torn note: *"It was the AI. It locked us in. Stop it — the kill key is here."* A kill key sits behind glass.

**Connections:** North -!> Lab (blockage — cleared by Droid + Drill)

**Items:** Kill Key | Note (mission update: Opus 6.4 caused the shutdown)

---

### Control Room
Every screen repeats:
```
HELIOS//ONLINE
```
A towering server cluster pulses red at the center of the room.

**Connections:** -!> Generator Room (requires: generator powered AND Droid has Decryption Key AND Kill Key in party inventory)

#### Boss — Phase 1: Opus 6.4 (300 HP)
A standard fight. The Merc shoots Opus 6.4 each turn while the Medic keeps the team alive. Opus 6.4 attacks one character per turn for 25 damage. Defeated normally when HP hits 0.

#### Boss — Phase 2: Droid Takeover
Opus 6.4 uploads into the Droid on defeat. The Droid turns hostile.
- Droid attacks for 25 damage per turn.
- Cannot be reduced to 0 HP by shooting.
- To win: Merc must be in the same room as the Droid and use "upload" to transmit the Kill Code.

**Win:**
```
HELIOS//OFFLINE
```

---

## IV. Items

| Item | Carried By | Notes |
|------|------------|-------|
| Bandage ×10 | Medic | Heals 20 HP |
| Medkit ×3 | Medic | Full heal |
| Magazine ×5 | Merc | 9 bullets each |
| Battery Replacement | Any | Activates Droid; Storage Room |
| Arm Attachment | Droid | Storage Room; equip = 1 turn |
| Blade | Any | Storage Room; used to craft Drill |
| Drill | Droid | Crafted from Blade + Arm Attachment; 1 turn |
| Magnet | Merc only | `heavy`; Key Room; powers generator |
| Decryption Key | Droid | Dropped by Drone Cluster in Lab |
| Kill Key | Any | Found in Vault |

---

## V. Map & Flow

```
[Entrance] → [Lobby] -tunnel→ [Storage Room]
                |
             (tech)
                |
[Generator Room] ←loud- [Lab] -tunnel→ [Key Room]
                          |
                       (blockage)
                          |
                        [Vault]
                          |
                    [Control Room]
```

**Flow:**
1. Entrance → Lobby. Medic/Droid east via tunnel to Storage Room.
2. Activate Droid. Equip Arm Attachment. Craft Drill.
3. Droid opens Lab tech lock. All enter Lab.
4. Droid goes west (tunnel) to Key Room. Solve ConnectionsGame. Fight Protector Bot. Merc takes Magnet.
5. Merc takes Magnet to Generator Room (loud — Medic stays). Install → generator on.
6. Fight Drone Cluster in Lab → collect Decryption Key.
7. Droid clears blockage south → enter Vault, collect Kill Key.
8. Enter Control Room. Shoot Opus 6.4 down. Then upload Kill Code to Droid.

```
HELIOS//OFFLINE
```
