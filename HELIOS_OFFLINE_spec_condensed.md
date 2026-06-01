
# HELIOS//OFFLINE

**Group Members:**

Aman P | Rishi B | Pranay B | Eshaan S

---

## I. Game Overview

### Plot

You will write a game about 2 government officials who infiltrate a lab to find out why it recently shut down. The game is a text based adventure game, coded fully in Java without using any external libraries or images. It's fine to import in-built libraries like ArrayList, however. The game should be stylized with text only, and for puzzle minigames that will be described later, ASCII art is okay.

### Players

Players have access to two, eventually three characters:

- Medic
- Mercenary (Merc for short)
- Droid (Acquired in the Storage Room)

Each character has a different amount of HP:

- Medic: 75 HP
- Merc: 150 HP
- Droid: 50 HP

The player controls one group per turn. Characters in the same room can be moved together or individually.

- Each collection of entities can only move one room per turn.

Each character has different abilities and weaknesses, which forces the player to split up.

#### Medic

- The Medic cannot move through loud or "whirring" paths, marked with `loud = true`.
- The Medic is able to heal characters:
  - Starts with 10 bandages. Using a bandage heals the target for 20 HP.
  - Starts with 3 medkits. Using a medkit heals a character to full HP.
- When the Medic heals, they lose the ability to move that turn.

#### Merc

- The Merc has a rifle, which they can use to fight enemies.
- Using the rifle prevents the Merc from moving that turn.
- The Merc starts with 5 magazines of 9 bullets each. When a magazine runs out, reloading costs a full turn.
- The Merc cannot move through tunnel paths, marked with `tunnel = true`.
- The Merc is the only character who can carry items marked `heavy = true`.

#### Droid

- The Droid is collected shortly after the start of the game, but once collected, acts like any other character.
- The Droid can interact with any tech lock it is adjacent to. Tech locks are marked `tech = true` on a path. When the Droid interacts with a tech lock, that path becomes permanently open for all characters.
- The Droid can collect and create different attachments:
  - **Arm Attachment**
    - Consists of two separate pieces (Arm Piece #1 and Arm Piece #2) found around the map.
    - Once both pieces are in the Droid's inventory, it can assemble them (costs one turn).
    - The Arm Attachment allows the Droid to move through normal-sized doors without another character accompanying it.
  - **Drill**
    - Crafted from the Blade item (found in Storage Room) after the Arm Attachment has been assembled.
    - Requires both the Blade and the Arm Attachment in the Droid's inventory to craft (costs one turn).
    - Allows the Droid to clear preset blockages marked `blockage = true`.
  - **Decryption Keys**
    - The Droid must collect 3 Decryption Keys to unlock the boss door into the Control Room.
    - The boss door cannot be entered without all 3 keys in the Droid's inventory.
- Until the Arm Attachment is acquired, the Droid cannot enter areas through normal doors without another character accompanying it.
- The Droid and the Medic can both pass through tunnel paths.

---

### Movement

- The game is characterized into "turns". Every turn, each character can make one move.
- Radiation leaks through specific rooms. If a character stays in the same room for more than 5 turns, they begin losing 10 HP per turn until they leave.

---

### Fights

- When a character enters a room containing an enemy, a fight sequence begins. Radiation mechanics pause during fights.
- Characters in the fight are free to move between rooms. The enemy follows the nearest character. Enemies can only move one room every two turns.
- Only the Merc can deal damage to enemies.
- When the fight ends in a win, radiation resumes.

---

### Inputs

- The player controls characters with natural language. There are no explicit option menus — the game detects keywords (e.g., "shoot", "move", "heal", "search", "interact").
- The player has 3 input strings per turn, one for each available character.
- Before each input, the game prints which character is being controlled.
- When a character enters a room no character has visited before, the room description is displayed.
- Any time a room's state changes, the updated description is shown the next time a character enters or is already present.

---

## II. Class Hierarchy

### Main

Controls player input and the game loop.

**Responsibilities:**
- Scanner input from the player, with keyword detection.
- Tracks actions per turn — 3 actions total, one per character.
- Prints the active character's name before each input prompt.
- Tracks which rooms have been visited and displays descriptions on first entry or after state changes.

**Variables:**
- `roomsPassed` (ArrayList\<Room\>) — Rooms the player has visited.

---

### Character *(Abstract Base Class)*

All playable characters extend this class.

#### Attributes
- `health` (int) — Current HP.
- `maxHealth` (int) — Maximum HP.
- `carrying` (ArrayList\<Item\>) — Items currently held by this character.
- `currentRoom` (String) — The room the character is currently in.

#### Behaviors
- `move(Path path)` — Moves the character along an accessible path. Returns false if locked or restricted. Overridden by subclasses.
- `collect(Item item)` — Adds a collectible Item to the character's inventory.
- `trade(Item item, Character target)` — Transfers an Item to another character's inventory.

---

### Mercenary *(extends Character)*

#### Additional Attributes
- `magazines` (int) — Number of magazines remaining. Starts at 5.
- `bulletsInCurrentMag` (int) — Bullets left in the current magazine. Starts at 9.
- `canMoveThisTurn` (boolean) — Set to false when the Merc shoots or reloads.

#### Additional Behaviors
- `shoot(Enemy enemy)` — Fires at the target enemy. Consumes one bullet. Sets `canMoveThisTurn = false`.
- `reload()` — Reloads the current magazine. Costs the Merc their move and shoot action for that turn.
- `move(Path path)` — Overrides base move. Returns false if `path.tunnel == true`.
- Can carry items where `item.heavy == true`. Other characters cannot.

---

### Medic *(extends Character)*

#### Additional Attributes
- `bandages` (int) — Number of bandages remaining. Starts at 10.
- `medkits` (int) — Number of medkits remaining. Starts at 3.
- `canMoveThisTurn` (boolean) — Set to false after healing.

#### Additional Behaviors
- `useBandage(Character target)` — Heals target for 20 HP. Decrements `bandages`. Sets `canMoveThisTurn = false`.
- `useMedkit(Character target)` — Heals target to max HP. Decrements `medkits`. Sets `canMoveThisTurn = false`.
- `move(Path path)` — Overrides base move. Returns false if `path.loud == true`.

---

### Droid *(extends Character)*

#### Additional Attributes
- `collected` (boolean) — Whether the Droid has been found and activated. Starts as false.
- `hasArmAttachment` (boolean) — Whether the Arm Attachment has been assembled.
- `hasDrill` (boolean) — Whether the Drill has been crafted.
- `armPieces` (int) — Number of Arm Pieces collected (0, 1, or 2).
- `decryptionKeys` (int) — Number of Decryption Keys collected. Boss door requires 3.

#### Additional Behaviors
- `interactTechLock(Path path)` — If `path.tech == true`, permanently unlocks that path for all characters.
- `assembleArm()` — If `armPieces == 2`, assembles the Arm Attachment. Costs one turn. Sets `hasArmAttachment = true`.
- `craftDrill()` — If `hasArmAttachment == true` and Blade is in inventory, crafts the Drill. Costs one turn. Sets `hasDrill = true`.
- `clearBlockage(Path path)` — If `hasDrill == true` and `path.blockage == true`, permanently clears the blockage.
- `move(Path path)` — Overrides base move. If `!hasArmAttachment`, Droid cannot move through a normal door unless another character is in the same room.

---

### Enemy *(Base Class)*

#### Attributes
- `health` (int) — Current HP.
- `maxHealth` (int) — Maximum HP.
- `damage` (int) — HP dealt to a character per attack.
- `currentRoom` (String) — Room the enemy currently occupies.
- `turnCounter` (int) — Tracks turns to enforce the one-move-per-two-turns rule.
- `active` (boolean) — Whether this enemy has been triggered.

#### Behaviors
- `move()` — Moves the enemy one room toward the nearest character. Can only be called once every two turns.
- `attack(Character target)` — Deals `damage` to the target character.
- `takeDamage(int amount)` — Reduces `health` by amount. If `health <= 0`, triggers death behavior.

---

### Subclasses of Enemy

#### MiterSaw *(extends Enemy)*
- `health`: 50
- `damage`: 10
- Special: Deals damage if a character stays in its room for more than 1 consecutive turn, even outside a formal fight trigger.
- Starting room: Lab

#### ProtectorBot *(extends Enemy)*
- `health`: 200
- `damage`: 10
- Spawns in Key Room immediately after the ConnectionsGame minigame is completed.
- Starting room: Key Room

#### Opus64 *(extends Enemy — Boss Phase 1)*
- `health`: 100
- `damage`: 30 (beam attack, see boss fight mechanics in Control Room)
- Special: After 5 turns, weak spot opens. Merc can shoot from lanes 2 or 3 only for 20 damage.
- Starting room: Control Room

#### DroidBoss *(extends Enemy — Boss Phase 2)*
- `health`: 50 (Droid's base HP)
- `damage`: 40
- Special: Cannot be reduced to 0 HP — must be defeated by uploading the Kill Code.
- Starting room: Control Room

---

### Minigame Classes

#### CaesarCipher *(Minigame)*

Used at the start of the game. The post-it note in the Lobby contains a Caesar-ciphered string. The player must decode it to pass through the Lab door.

**Attributes:**
- `cipherText` (String) — The encoded string displayed to the player.
- `shift` (int) — The shift value used to encode. Generated randomly each playthrough (range 1–10).
- `solution` (String) — The decoded plaintext.

**Behaviors:**
- `encode(String plaintext, int shift)` — Encodes a string using Caesar cipher logic.
- `decode(String input, int shift)` — Tests input against the solution. Returns true if it matches.
- `displayCipher()` — Prints the ciphered string to the player.
- `checkInput(String playerInput)` — Compares player input against `solution`. Returns true on a match and unlocks the Lab south path.

**Notes:**
- The shift and solution are fixed per playthrough, generated at game start.
- The player is not told the shift — they must figure it out by trial and error.
- The cipher only wraps alphabetic characters.

---

#### ConnectFour *(Minigame)*

Triggered in the Walkway after the Drone Cluster is defeated. The player must beat a bot at Connect 4 to unlock the Vault.

**Attributes:**
- `board` (char\[\]\[\]) — A 6-row × 7-column grid. Empty = `'.'`, player = `'X'`, bot = `'O'`.
- `currentTurn` (String) — `"player"` or `"bot"`.
- `gameOver` (boolean)
- `winner` (String) — `"player"`, `"bot"`, or `"draw"`.

**Behaviors:**
- `displayBoard()` — Renders the board as ASCII art.
- `dropPiece(int column, char piece)` — Places a piece in the lowest available row.
- `checkWin(char piece)` — Checks all win conditions. Returns true if found.
- `checkDraw()` — Returns true if the board is full with no winner.
- `botMove()` — Basic bot logic: win if possible, block player win, otherwise center-weighted random.
- `playGame()` — Main loop. Returns true if player wins (unlocking the Vault path). Player may retry on loss.

**Display format:**
```
1 2 3 4 5 6 7
| . . . . . . . |
| . . . . . . . |
| . . . . . . . |
| . . . X . . . |
| . . O X . . . |
| . X O O X . . |
```

---

#### ConnectionsGame *(Minigame)*

Triggered in the Key Room. The Droid must solve a NYT Connections-style puzzle to retrieve Decryption Key #3.

**Attributes:**
- `words` (String\[\]) — Array of 16 words (hardcoded, thematic to the game).
- `categories` (String\[\]) — Array of 4 category labels.
- `categoryMap` (HashMap\<String, String\>) — Maps each word to its correct category.
- `solvedCategories` (ArrayList\<String\>) — Categories correctly identified so far.
- `guessesRemaining` (int) — Starts at 4.
- `gameOver` (boolean)

**Behaviors:**
- `displayGrid()` — Renders the 16 unsolved words in a 4×4 ASCII grid.
- `displaySolved()` — Shows correctly solved categories with tier labels (Yellow / Green / Blue / Purple).
- `submitGuess(String[] fourWords)` — Checks if the submitted group belongs to one category. On success, removes words. On failure, decrements guesses and checks "one away."
- `checkOneAway(String[] guess)` — Returns true if exactly 3 of 4 submitted words share a correct category.
- `playGame()` — Main loop. Returns true if all categories solved (awards Decryption Key #3, spawns Protector Bot). Player may retry on failure.

**Hardcoded puzzle:**
- Yellow (easiest): CENTRIFUGE, BEAKER, PIPETTE, BURETTE — *Lab glassware*
- Green: RELAY, CAPACITOR, RESISTOR, DIODE — *Circuit components*
- Blue: OPUS, HELIOS, R3D3, NEXUS — *Facility codenames*
- Purple (hardest): COLD, DARK, SILENT, STILL — *Words that describe the lab on arrival*

**Display format:**
```
[ CENTRIFUGE ] [ RELAY ] [ OPUS ] [ COLD ]
[ BEAKER ] [ CAPACITOR ] [ HELIOS ] [ DARK ]
[ PIPETTE ] [ RESISTOR ] [ R3D3 ] [ SILENT ]
[ BURETTE ] [ DIODE ] [ NEXUS ] [ STILL ]

Guesses remaining: 4
Enter four words separated by commas:
```

---

## III. Room Definitions

Each room and its links to the overall map are defined below. Every time a character enters a new room for the first time, its description is read aloud. Every time a room's state changes, the description is re-read the next time a character enters or is already present.

**Path flag legend:**
- `->` Open path, no restriction.
- `-!>` Restricted path. Restriction type noted in parentheses.
- `loud = true` — Medic cannot enter.
- `tunnel = true` — Merc cannot enter. Droid and Medic can.
- `tech = true` — Requires Droid to interact with the tech lock to permanently open for all characters.
- `heavy = true` (item flag) — Only Merc can carry.
- `blockage = true` — Requires Droid with Drill to clear.

Whenever `[Characters Selected]` appears in a description, it is replaced at runtime with the names of the characters the player is controlling.

---

### Entrance

#### Description

A mercenary and medic are standing outside a large, black door. It has a glowing keypad on it. They receive a call on their radios. *"You two have just landed outside the base. We don't know what happened here, but your goal is to get in, find people if there are any, figure out what happened, and get out. Their site shut down and lost all contact 3 days ago. May God bless you and good luck."* The radio cuts out.

Player, you are in control of these two characters. Whatever you type, they will follow. Complete the mission directive.

#### Connections
- East -> Lobby

#### Items
None

---

### Lobby

#### Description

Lights are buzzing above you. There's a receptionist desk with a person slumped over. You check for a pulse — there's none. A post-it note is on the person's computer. The title says "Key" followed by a string of scrambled characters.

The string on the post-it note is a Caesar cipher. The player must decode it to obtain the passphrase for the Lab door.

#### Connections
- West -> Entrance
- South -!> Lab (tech = true — Droid must interact with keypad, OR player submits the correct Caesar cipher solution)
- East -!> Storage Room (tunnel = true — Droid and Medic only)

#### Items
- Post-it Note (contains the Caesar-ciphered passphrase — triggers the CaesarCipher minigame when examined)

#### Notes
The Caesar cipher is generated at game start with a random shift value. If the Droid is available, it can bypass the cipher entirely by interacting with the tech lock.

---

### Storage Room

#### Description

Dim emergency lighting flickers overhead. Metal shelving units line the walls, most of them toppled or stripped bare. In the center of the room, a small robot sits slumped against a crate, its chassis dented and one optical sensor dark. A faded label on its side reads "R3D3". It looks like it just needs power.

#### State Change — R3D3 Activated

The robot shudders, its optical sensor flickers on, and it lets out a low mechanical chirp. R3D3 is online. A new character is now available.

#### Connections
- West -!> Lobby (tunnel = true — Droid and Medic only)

#### Items
- Blade (hidden under a collapsed shelf — found by searching)
- Battery Replacement (visible on a nearby shelf)
- R3D3 — inserting the Battery Replacement into R3D3 (one action) activates the Droid as a playable character

---

### Lab

#### Description

A long room lined with examination tables, most of them overturned. Glass from shattered containment units crunches underfoot. Equipment is still running — readings scroll across monitors, logging data that no one will ever read. A miter saw mounted to a central worktable spins slowly on its own, trailing sparks across a scorched arc on the floor.

#### State Change — Miter Saw Defeated

The miter saw grinds to a halt, its blade spinning down with a long metallic whine. The room is quiet for the first time. Something glints under the debris near the far wall.

#### Connections
- North -!> Lobby (tech = true — Droid interacts with the tech lock to open permanently)
- East -> Generator Room (loud = true — Medic cannot enter)
- South -!> Walkway (requires Droid to have Drill — `blockage = true`)
- West -!> Key Room (tunnel = true — Droid only)

#### Enemy
**Miter Saw** — 50 HP, 10 damage. Deals damage if a character stays in its room for more than 1 consecutive turn. Must be defeated to safely search the room.

#### Items
- Arm Piece #1 (found by searching after Miter Saw is defeated)
- Decryption Key #1 (found by searching after Miter Saw is defeated)
- Coil (crafted here — bring Copper Wire and perform the "machine" action after the Miter Saw is defeated; costs one turn)

#### Notes
The Copper Wire for crafting the Coil is found in the Storage Room (see items added below to Storage Room). The Coil is a generator component needed later.

---

### Generator Room

#### Description

The room shakes with a deep, rhythmic whirring from a massive turbine array bolted to the floor. Banks of dead gauges line the walls. The generator itself is partially disassembled — several component bays sit empty, waiting for parts.

#### State Change — Generator Powered

The turbines roar to life. The gauges across the walls leap to full. A low hum spreads through the building — something has unlocked.

#### Connections
- West -!> Lab (loud = true — Medic cannot enter)

#### Items
None

#### Notes
The generator requires a Transmission, a Coil, and a Magnet to be brought here and installed. Installing all three (one action per item) powers the generator and permanently unlocks the boss door into the Control Room. The Transmission is found in the Key Room. The Magnet (`heavy = true`) is found in the Key Room as well (see below).

---

### Key Room

#### Description

A cramped access room lined with old filing cabinets. A single terminal sits on a shelf, its screen displaying a colorful word grid. A sealed compartment beneath it waits for the puzzle to be solved. A heavy industrial magnet sits in a reinforced cradle in the corner. A mechanical transmission lies disassembled on a workbench nearby.

#### State Change — Puzzle Solved / Protector Bot Spawned

The compartment clicks open. Inside is a small metallic card. Before `[Characters Selected]` can reach for it, a panel in the wall splits open and a hulking security bot steps out, its red optical sensors locking on immediately.

#### Connections
- East -!> Lab (tunnel = true — Droid only entrance)

#### Enemy
**Protector Bot** — 200 HP, 10 damage. Spawns immediately after the ConnectionsGame minigame is completed.

#### Items
- Decryption Key #2 (locked behind the ConnectionsGame minigame — Droid must solve the puzzle to retrieve it)
- Magnet (`heavy = true` — only the Merc can carry; generator component)
- Transmission (generator component)

---

### Walkway

#### Description

A narrow suspended walkway stretches across a dark shaft. The air tastes like ozone. Far below, something pulses with a faint blue light. At the center of the walkway, a swarm of small drones hovers in a loose cluster, their red targeting sensors sweeping back and forth.

#### State Change — Drone Cluster Defeated

The drones scatter and crash into the shaft below. The walkway is clear. A terminal at the south end flickers to life. Something metallic clatters to the floor near the wreckage.

#### Connections
- North -!> Lab (requires Droid to have Drill — `blockage = true`)
- South -!> Vault (Drone Cluster must be defeated first; then player must win the Connect 4 minigame at the terminal)

#### Enemy
**Drone Cluster** — 100 HP, 20 damage. Patrols the walkway.

#### Items
- Decryption Key #3 (drops from the Drone Cluster on defeat)

#### Notes
After the Drone Cluster is defeated, a Connect 4 terminal activates at the south end of the walkway. The player must win the ConnectFour minigame to unlock the path south into the Vault. If the player loses, they may try again.

---

### Vault

#### Description

Reinforced walls, a heavy blast door now hanging open. The room is sparse. A cracked case on the floor holds a torn note scrawled in hasty handwriting: *"It was the AI. It shut everything down — killed the power, locked the doors, locked us in. We had no way out. If you're reading this, you have to stop it. The kill key is in here. Use it."* A security panel on the far wall holds a glowing kill key behind a glass cover.

#### Connections
- North -> Walkway

#### Items
- Kill Key (required item — must be in party inventory to enter the Control Room and start the final sequence)
- Note (triggers mission objective update: Opus 6.4 is the cause of the shutdown and deaths — it must be destroyed to win)

---

### Control Room

#### Description

The heart of the facility. Screens cover every wall, all of them cycling the same scrolling text:

```
HELIOS//ONLINE
HELIOS//ONLINE
HELIOS//ONLINE
```

In the center, a towering server cluster hums with unnatural intensity. Red lights pulse in a slow, deliberate rhythm. Four lanes are marked on the floor in faded yellow paint, each leading toward the central core.

#### Connections
- West -!> Lab (requires: generator fully powered AND Droid has all 3 Decryption Keys AND Kill Key is in party inventory)

#### Boss Fight — Phase 1: Opus 6.4

The fight takes place across 4 lanes. Each character occupies one lane at a time.

**Lane rules:**
- Characters can move between adjacent lanes each turn. Movement does not wrap.
- Staying in the same lane for more than 3 consecutive turns triggers radiation damage: 10 HP per turn.

**Attack pattern:**
- Each turn, Opus 6.4 selects a lane and marks it as primed. On the following turn, it fires a beam at that lane for 30 damage to any character in it.
- The primed lane is announced to the player at the end of the turn it is selected.

**Weak spot:**
- After 5 turns, Opus 6.4's weak spot opens on its central core.
- The Merc can shoot the weak spot from lanes 2 or 3 only, dealing 20 damage per shot.
- Opus 6.4 has 100 HP. It is defeated when reduced to 0.

---

#### Boss Fight — Phase 2: Droid Takeover

Immediately after Opus 6.4 is destroyed, it uploads itself into the Droid. The Droid becomes a hostile boss unit and the fight continues in the same 4-lane arena.

**Rules are identical to Phase 1, with these changes:**
- The Droid deals 40 damage per beam.
- The Droid cannot be reduced to 0 HP — it must be defeated by uploading the Kill Code.
- To upload the Kill Code, the Merc must be in the same lane as the Droid when it is the Merc's turn, and use the "upload" action.
- Successfully uploading the Kill Code ends the fight and triggers the win sequence.

**Win Sequence:**

The Droid goes still. Its optical sensor dims. The screens across the room go dark, one by one. The scrolling text changes for the last time:

```
HELIOS//OFFLINE
```

The mission is complete.

---

## IV. Item Reference

| Item | Description | Carried By | Notes |
|------|-------------|------------|-------|
| Bandage | Heals target for 20 HP | Medic only | Medic starts with 10 |
| Medkit | Heals target to full HP | Medic only | Medic starts with 3 |
| Magazine | 9-bullet rifle magazine | Merc only | Merc starts with 5 |
| Post-it Note | Contains Caesar-ciphered passphrase | Any | Found in Lobby |
| Copper Wire | Raw material for Coil | Any | Found in Storage Room |
| Blade | Component for Drill | Any | Found in Storage Room |
| Battery Replacement | Activates R3D3 | Any | Found in Storage Room |
| Arm Piece #1 | Component for Arm Attachment | Any | Found in Lab |
| Arm Piece #2 | Component for Arm Attachment | Any | Found in Storage Room |
| Arm Attachment | Assembled from Arm Pieces #1 and #2 | Droid only | Assembled by Droid (1 turn) |
| Drill | Crafted from Blade + Arm Attachment | Droid only | Crafted by Droid (1 turn) |
| Coil | Generator component | Any | Machined from Copper Wire in Lab |
| Transmission | Generator component | Any | Found in Key Room |
| Magnet | Generator component | Merc only | `heavy = true` — found in Key Room |
| Decryption Key #1 | Required for boss door | Droid only | Found in Lab (search after Miter Saw defeated) |
| Decryption Key #2 | Required for boss door | Droid only | Awarded after ConnectionsGame in Key Room |
| Decryption Key #3 | Required for boss door | Droid only | Drops from Drone Cluster on defeat |
| Kill Key | Required to enter Control Room | Any | Found in Vault |

---

## V. Room Map (9 Rooms)

```
[Entrance] -> [Lobby] -tunnel-> [Storage Room]
                  |
              (cipher/tech)
                  |
               [Lab] ----loud----> [Generator Room]
                  |  \
           blockage  tunnel
                  |       \
            [Walkway]   [Key Room]
                  |
             (ConnectFour)
                  |
              [Vault]
                  |
           (boss door: keys + generator)
                  |
           [Control Room]
```

**Flow summary:**
1. Start at Entrance → Lobby. Find Post-it Note (Caesar cipher).
2. Send Medic or Droid through tunnel to Storage Room. Activate R3D3 (Droid), collect Blade, Copper Wire, and Arm Piece #2.
3. Solve Caesar cipher (or use Droid tech lock) to open Lab door.
4. Fight Miter Saw in Lab. Collect Arm Piece #1 and Decryption Key #1. Craft Coil.
5. Assemble Arm Attachment on Droid. Craft Drill. Send Droid west to Key Room.
6. Solve ConnectionsGame in Key Room → get Decryption Key #2. Fight Protector Bot. Collect Magnet and Transmission.
7. Merc carries Magnet + Transmission to Generator Room (through Lab — loud path, Medic stays behind). Install components to power generator.
8. Droid clears blockage south of Lab. Enter Walkway, fight Drone Cluster → get Decryption Key #3. Win ConnectFour to unlock Vault.
9. Collect Kill Key from Vault.
10. Enter Control Room with all 3 Decryption Keys + Kill Key + powered generator. Fight Opus 6.4 (Phase 1) then Droid Boss (Phase 2). Upload Kill Code to win.
```
HELIOS//OFFLINE
```
