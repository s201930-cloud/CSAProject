
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

- Droid (Acquired later)

  

Each character has a different amount of HP:

  

- Medic: 75 HP

- Merc: 150 HP

- Droid: 50 HP

  

The player will have two modes of control, where they can send a single entity down a room or multiple entities, IF the collection of entities is already in the same room.

  

- If Merc and Medic are both in the lobby, the player will be able to move either the Merc and Medic into an adjacent room, or both of them together.

- If Merc and Medic are in the lobby, and Droid is in the server room, the player can't send all three of them into the adjacent machine shop. They will have to move the Merc and Medic together into the machine shop, then the Droid in the next turn.

- Each collection of entities can only move one room per turn.

  

Each character has different abilities and weaknesses, which forces the player to split them up.

  

#### Medic

- The Medic cannot move through loud or "whirring" paths, marked with `loud = true`.

- The Medic is able to heal characters:

- Starts the game with 10 bandages. Using a bandage provides the target character with 20 HP.

- Starts the game with 3 medkits. Using a medkit on a character heals them fully.

- When the Medic heals another character or themself, they lose the ability to move that turn.

  

#### Merc

- The Merc has a rifle, which they can use to fight enemies.

- Using the rifle in a turn during a fight prevents the Merc from moving that turn.

- The Merc starts with 5 magazines of 9 bullets each. When a magazine runs out, the Merc must use a full turn to reload — they cannot move or shoot during that turn.

- The Merc cannot move through tunnel paths, marked with `tunnel = true`.

- The Merc is the only character who can carry items marked `heavy = true`.

  

#### Droid

- The Droid is collected shortly after the start of the game, but once collected, acts like any other character.

- The Droid can interact with any tech lock it is adjacent to. Tech locks are marked `tech = true` on a path. When the Droid interacts with a tech lock, that path becomes permanently open for all characters.

- The Droid can collect and create different attachments:

- **Arm Attachment**

- Consists of two separate pieces (Arm Piece #1 and Arm Piece #2) found around the map.

- Once both pieces are in the Droid's inventory, it can assemble them into the Arm Attachment (costs one turn).

- The Arm Attachment allows the Droid to move through normal-sized doors without another character accompanying it.

- The Arm Attachment also allows the Droid to reach high connection points above doorframes that are otherwise inaccessible.

- **Drill**

- Crafted from the Blade item in the Machining Room after the Arm Attachment has been assembled.

- Requires both the Blade and the Arm Attachment to be in the Droid's inventory to craft (costs one turn).

- Allows the Droid to clear preset blockages marked `blockage = true`, creating full-sized pathways between rooms.

- Also required to unlock certain paths marked in room definitions.

- **Decryption Keys**

- The Droid must collect 4 Decryption Keys to unlock the boss door into the Control Room.

- These are items and provide no combat value.

- The boss door cannot be entered without all 4 keys in the Droid's inventory.

- Until the Arm Attachment is acquired, the Droid cannot enter areas through normal doors without another character accompanying it.

- The Droid and the Medic can both pass through tunnel paths.

  

---

  

### Movement

  

- The game is characterized into "turns". Every turn, each character can make one move. Character-specific moves are defined above.

- The AI (Opus 6.4) is constantly leaking radiation throughout specific rooms. If a character stays in the same room for more than 5 turns, they begin losing 10 HP per turn in all subsequent turns until they leave that room.

  

---

  

### Fights

  

- When a character enters a room containing an enemy, a fight sequence begins. Radiation mechanics pause completely during fights.

- Characters in the fight are free to move between rooms. The enemy that's currently being fought will follow the nearest character. Enemies are "clunky" and can only move one room every two turns.

- Only the Merc can deal damage to enemies.

- When the fight ends in a win (enemy reaches 0 HP), radiation resumes.

  

---

  

### Inputs

  

- The player controls characters with natural language. There are no explicit option menus — the game detects keywords in the player's input string (e.g., "shoot", "move", "heal", "search", "interact").

- The player has 3 input strings per turn, one for each available character.

- Before each input, the game prints which character is being controlled.

- When a character enters a room no character has visited before, the room description is displayed.

- Any time a room's state changes, the updated description is shown the next time a character enters or is already present in that room.

  

---

  

## II. Class Hierarchy

  

### Main

  

Controls player input and the game loop.

  

**Responsibilities:**

- Scanner input from the player, with keyword detection in the input string.

- Tracks actions per turn — 3 actions total, one per character.

- Prints the active character's name before each input prompt.

- Tracks which rooms have been visited and displays descriptions on first entry or after state changes.

  

**Variables:**

-  `roomsPassed` (ArrayList\<Room\>) — Rooms the player has visited.

  

---

  

### Character *(Abstract Base Class)*

  

All playable characters extend this class.

  

#### Attributes

-  `health` (int) — Current HP.

-  `maxHealth` (int) — Maximum HP.

-  `carrying` (ArrayList\<Item\>) — Items currently held by this character.

-  `currentRoom` (String) — The room the character is currently in.

  

#### Behaviors

-  `move(Path path)` — Moves the character along an accessible path. Returns false and keeps the character in place if the path is locked or restricted. Overridden by subclasses.

-  `collect(Item item)` — Adds a collectible Item to the character's inventory.

-  `trade(Item item, Character target)` — Transfers an Item from this character's inventory to another character's inventory.

  

---

  

### Mercenary *(extends Character)*

  

#### Additional Attributes

-  `magazines` (int) — Number of magazines remaining. Starts at 5.

-  `bulletsInCurrentMag` (int) — Bullets left in the current magazine. Starts at 9.

-  `canMoveThisTurn` (boolean) — Set to false when the Merc shoots or reloads.

  

#### Additional Behaviors

-  `shoot(Enemy enemy)` — Fires at the target enemy. Consumes one bullet. Sets `canMoveThisTurn = false`.

-  `reload()` — Reloads the current magazine. Costs the Merc their move and shoot action for that turn.

-  `move(Path path)` — Overrides base move. Returns false if `path.tunnel == true`.

- Can carry items where `item.heavy == true`. Other characters cannot.

  

---

  

### Medic *(extends Character)*

  

#### Additional Attributes

-  `bandages` (int) — Number of bandages remaining. Starts at 10.

-  `medkits` (int) — Number of medkits remaining. Starts at 3.

-  `canMoveThisTurn` (boolean) — Set to false after healing.

  

#### Additional Behaviors

-  `useBandage(Character target)` — Heals target for 20 HP. Decrements `bandages`. Sets `canMoveThisTurn = false`.

-  `useMedkit(Character target)` — Heals target to max HP. Decrements `medkits`. Sets `canMoveThisTurn = false`.

-  `move(Path path)` — Overrides base move. Returns false if `path.loud == true`.

  

---

  

### Droid *(extends Character)*

  

#### Additional Attributes

-  `collected` (boolean) — Whether the Droid has been found and activated. Starts as false.

-  `hasArmAttachment` (boolean) — Whether the Arm Attachment has been assembled.

-  `hasDrill` (boolean) — Whether the Drill has been crafted.

-  `armPieces` (int) — Number of Arm Pieces collected (0, 1, or 2).

-  `decryptionKeys` (int) — Number of Decryption Keys collected. Boss door requires 4.

  

#### Additional Behaviors

-  `interactTechLock(Path path)` — If `path.tech == true`, permanently unlocks that path for all characters.

-  `assemblArm()` — If `armPieces == 2`, assembles the Arm Attachment. Costs one turn. Sets `hasArmAttachment = true`.

-  `craftDrill()` — If `hasArmAttachment == true` and Blade is in inventory, crafts the Drill. Costs one turn. Sets `hasDrill = true`.

-  `clearBlockage(Path path)` — If `hasDrill == true` and `path.blockage == true`, permanently clears the blockage and opens the path for all characters.

-  `move(Path path)` — Overrides base move. If `!hasArmAttachment`, Droid cannot move through a normal door unless another character is in the same room.

  

---

  

### Enemy *(Base Class)*

  

#### Attributes

-  `health` (int) — Current HP.

-  `maxHealth` (int) — Maximum HP.

-  `damage` (int) — HP dealt to a character per attack.

-  `currentRoom` (String) — Room the enemy currently occupies.

-  `turnCounter` (int) — Tracks turns to enforce the one-move-per-two-turns rule during combat.

-  `active` (boolean) — Whether this enemy has been triggered by a character entering its room.

  

#### Behaviors

-  `move()` — Moves the enemy one room toward the nearest character. Can only be called once every two turns (enforced by `turnCounter`).

-  `attack(Character target)` — Deals `damage` to the target character.

-  `takeDamage(int amount)` — Reduces `health` by amount. If `health <= 0`, triggers death behavior.

  

---

  

### Subclasses of Enemy

  

#### MiterSaw *(extends Enemy)*

-  `health`: 50

-  `damage`: 10

- Special rule: If a character remains in the same room as the Miter Saw for more than 1 turn (even outside of a formal "fight" trigger), it deals its damage value.

- Starting room: Machining Room

  

#### DroneCluster *(extends Enemy)*

-  `health`: 100

-  `damage`: 20

- Starting room: Walkway

- On death: Drops Decryption Key #4.

  

#### ProtectorBot *(extends Enemy)*

-  `health`: 200

-  `damage`: 10

- Spawns in Key Room immediately after the NYT Connections minigame is completed.

- Starting room: Key Room

  

#### Opus64 *(extends Enemy — Boss Phase 1)*

-  `health`: 100

-  `damage`: 30 (beam attack, see boss fight mechanics in Control Room definition)

- Special: After 5 turns, weak spot opens. Merc can shoot it from lanes 2 or 3 for 20 damage.

- Starting room: Control Room

  

#### DroidBoss *(extends Enemy — Boss Phase 2)*

-  `health`: 50 (Droid's base HP)

-  `damage`: 40 (10 more than Opus 6.4)

- Special: To defeat, the Merc must end up in the same lane as the Droid and use the Kill Code. Defeated by uploading the Kill Code, not by reducing HP to 0.

- Starting room: Control Room

  

---

  

### Minigame Classes

  

#### CaesarCipher *(Minigame)*

  

Used at the start of the game. The post-it note in the Lobby contains a Caesar-ciphered string. The player must decode it to pass through the Hallway door.

  

**Attributes:**

-  `cipherText` (String) — The encoded string displayed to the player.

-  `shift` (int) — The shift value used to encode the string. Generated randomly each playthrough within a fixed range (e.g., 1–10).

-  `solution` (String) — The decoded plaintext, derived from `cipherText` and `shift`.

  

**Behaviors:**

-  `encode(String plaintext, int shift)` — Encodes a string using Caesar cipher logic. Used at game initialization to generate `cipherText`.

-  `decode(String input, int shift)` — Takes the player's input and tests it against the solution. Returns true if it matches.

-  `displayCipher()` — Prints the ciphered string to the player in the game's text style.

-  `checkInput(String playerInput)` — Strips and compares the player's input string against `solution`. Returns true on a match and unlocks the Hallway south path.

  

**Notes:**

- The shift value and solution are fixed per playthrough and generated at game start.

- The player is not told the shift value — they must figure it out by trial and error or reasoning.

- The cipher only wraps alphabetic characters; numbers and symbols pass through unchanged.

  

---

  

#### ConnectFour *(Minigame)*

  

Triggered at the south end of the Walkway after the Drone Cluster is defeated. The player must beat an automated bot at Connect 4 to unlock the path south into the Vault.

  

**Attributes:**

-  `board` (char\[\]\[\]) — A 6-row × 7-column 2D array representing the game grid. Empty cells are `'.'`, player pieces are `'X'`, bot pieces are `'O'`.

-  `currentTurn` (String) — Either `"player"` or `"bot"`.

-  `gameOver` (boolean) — Set to true when a win or draw is detected.

-  `winner` (String) — Set to `"player"`, `"bot"`, or `"draw"` when `gameOver` is true.

  

**Behaviors:**

-  `displayBoard()` — Renders the board as ASCII art, with column numbers displayed above each column.

-  `dropPiece(int column, char piece)` — Places a piece in the lowest available row of the given column. Returns false if column is full.

-  `checkWin(char piece)` — Checks all horizontal, vertical, and diagonal sequences for four matching pieces. Returns true if a win is found.

-  `checkDraw()` — Returns true if all cells are filled and no win condition is met.

-  `botMove()` — The bot's decision logic. Should implement a basic strategy: first check if the bot can win in one move, then block the player from winning in one move, then play center-weighted random otherwise.

-  `playGame()` — Main game loop. Alternates between player input and `botMove()` until `gameOver` is true. Returns true if the player wins (unlocking the Vault path), false otherwise. If the player loses, they can try again.

  

**Display format (example):**

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

  

Triggered in the Key Room. The Droid must solve a NYT Connections-style puzzle to retrieve Decryption Key #3. The puzzle presents 16 words arranged in a 4×4 grid, and the player must group them into four categories of four.

  

**Attributes:**

-  `words` (String\[\]) — Array of 16 words used in the puzzle. Hardcoded per playthrough (thematic to the game's setting — e.g., lab equipment, sci-fi terminology).

-  `categories` (String\[\]) — Array of 4 category labels, each describing one group of 4 words.

-  `categoryMap` (HashMap\<String,  String\>) — Maps each word to its correct category label.

-  `solvedCategories` (ArrayList\<String\>) — Categories the player has correctly identified so far.

-  `guessesRemaining` (int) — Number of incorrect guesses the player is allowed. Starts at 4.

-  `gameOver` (boolean) — True when all categories are solved or guesses run out.

  

**Behaviors:**

-  `displayGrid()` — Renders the 16 remaining unsolved words in a 4×4 ASCII grid layout.

-  `displaySolved()` — Displays categories that have already been correctly identified, with their color tier label (Yellow / Green / Blue / Purple, in ascending difficulty).

-  `submitGuess(String\[\] fourWords)` — Takes the player's submitted group of 4 words. Checks if they all belong to the same category. If correct, adds to `solvedCategories` and removes words from the grid. If incorrect, decrements `guessesRemaining` and checks for "one away" to display a hint.

-  `checkOneAway(String\[\] guess)` — Returns true if exactly 3 of the 4 submitted words belong to the same correct category. Used to print "One away..." hint to the player.

-  `playGame()` — Main game loop. Displays the grid, takes player input as a comma-separated list of four words, calls `submitGuess()`, and continues until `gameOver`. Returns true if all categories are solved (awarding Decryption Key #3 and spawning the Protector Bot), false if guesses run out (player can retry).

  

**Hardcoded puzzle (example — replace with final version):**

- Yellow (easiest): CENTRIFUGE, BEAKER, PIPETTE, BURETTE — *Lab glassware*

- Green: RELAY, CAPACITOR, RESISTOR, DIODE — *Circuit components*

- Blue: OPUS, HELIOS, R3D3, NEXUS — *Facility codenames*

- Purple (hardest): COLD, DARK, SILENT, STILL — *Words that describe the lab on arrival*

  

**Display format (example):**

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

  

Each room and its links to the overall map are defined below. Every time a character enters a new room for the first time, its description is read aloud. Every time a room's state changes, the description is re-read the next time a character enters or is already present in that room.

  

The game has three floor levels: Ground Floor, Basement, and Boiler Level. Floors are linked by stairwells. Stairwells act like transition rooms — characters pass through them in a single move and cannot remain in them.

  

**Path flag legend:**

-  `->` Open path, no restriction.

-  `-!>` Restricted path. Restriction type noted in parentheses.

-  `loud = true` — Medic cannot enter.

-  `tunnel = true` — Merc cannot enter. Droid and Medic can.

-  `tech = true` — Requires Droid to interact with the tech lock to permanently open for all characters.

-  `heavy = true` (item flag) — Only Merc can carry.

-  `blockage = true` — Requires Droid with Drill to clear.

  

Whenever `[Characters Selected]` appears in a description, it is replaced at runtime with the names of the characters the player is controlling.

  

---

  

## Ground Floor

  

---

  

### Entrance

  

#### Description

A mercenary and medic are standing outside a large, black door. It has a large, glowing keypad on it. They receive a call on their radios. *"You two have just landed outside base. We don't know what happened in here, but your goal is to get in, find people, if there are any, figure out what happened, and get out. What they were doing in this lab is classified, so we have no idea what could've gone wrong. All we know is that their site shut down and lost all contact 3 days ago. We trust you two are prepared and ready. Your stats are available on the comms watch. May God bless you and good luck."* The radio cuts out.

  

Player, you are in control of these two characters. Whatever you type in, they will follow. Complete the mission directive.

  

#### Connections

- East -> Lobby

- South -> Shed

  

#### Items

None

  

---

  

### Shed

  

#### Description

There's a dusty shed a few feet away from the base. `[Characters Selected]` enter the shed. Immediately, they're hit with the smell of rotting wood. `[Characters Selected]` notice something glinting in the corner. Perhaps searching or looking around would help?

  

#### Connections

- North -> Entrance

- South -> Cabin

  

#### Items

- 3 Bandages (if searched for — only the Medic can carry bandages)

  

---

  

### Cabin

  

#### Description

Huge copper blocks fill up a cabin. Assortments of rods and wires are also scattered around. A mouse chitters by your toes, then scurries away into a hole.

  

#### Connections

- North -> Shed

  

#### Items

- Copper Wire

  

---

  

### Lobby

  

#### Description

Lights are buzzing above you. There's a receptionist desk with a person slumped over. You check for a pulse — there's none. A post-it note is on the person's computer. The title says "Key" followed by a string of scrambled characters.

  

The string on the post-it note is a Caesar cipher. The player must decode it to obtain the passphrase for the Hallway door. Decoding is done by trial and error — entering the correct decoded string into the south path prompt unlocks the Hallway.

  

#### Connections

- West -> Entrance

- South -!> Hallway (tech = true — Droid must interact with keypad, OR player submits the correct Caesar cipher solution)

  

#### Items

- Post-it Note (contains the Caesar-ciphered passphrase — triggers the CaesarCipher minigame when examined)

  

#### Notes

The Caesar cipher is generated at game start with a random shift value. The post-it note displays the encoded string. The player must decode it and submit the answer to pass south. If the Droid is available, it can bypass the cipher entirely by interacting with the tech lock.

  

---

  

### Hallway

  

#### Description

The keypad blinks green and opens into a hall. You see a slot for a connector — maybe some sort of communication wire — next to a door facing east. Wind rushes through a stair entrance at the south. A note flutters down from the ceiling fan above.

  

#### Connections

- North -> Lobby

- East -!> Test Lab (tech = true — Droid must interact with the tech lock on the east door)

- South -> Stairwell #1

- West -!> Generator Room (loud = true — Medic cannot enter; additionally locked until generator is fully powered)

  

#### Items

None

  

---

  

## Basement

  

---

  

### Stairwell #1

  

#### Description

A narrow stairwell carved into the concrete. The air is heavy with the smell of rust and old metal. `[Characters Selected]` descend into the lower level.

  

#### Connections

- North -> Hallway

- South -> Storage Room

  

#### Notes

Stairwells cannot be occupied — characters pass through in a single move.

  

---

  

### Storage Room

  

#### Description

Dim emergency lighting flickers overhead. Metal shelving units line the walls, most of them toppled or stripped bare. In the center of the room, a small robot sits slumped against a crate, its chassis dented and one optical sensor dark. A faded label on its side reads "R3D3". It looks like it just needs power.

  

#### State Change — R3D3 Activated

The robot shudders, its optical sensor flickers on, and it lets out a low mechanical chirp. R3D3 is online. A new character is now available.

  

#### Connections

- North -> Stairwell #1

  

#### Items

- Blade (hidden under a collapsed shelf — found by searching)

- Battery Replacement (visible on a nearby shelf)

- R3D3 — inserting the Battery Replacement into R3D3 (one action) activates the Droid as a playable character

  

---

  

### Generator Room

  

#### Description

The room shakes with a deep, rhythmic whirring from a massive turbine array bolted to the floor. Banks of dead gauges line the walls. The generator itself is partially disassembled — several component bays sit empty, waiting for parts.

  

#### State Change — Generator Powered

The turbines roar to life. The gauges across the walls leap to full. A low hum spreads through the building — something has unlocked.

  

#### Connections

- East -!> Hallway (loud = true — Medic cannot enter)

- West -> Stairwell #2

  

#### Items

None

  

#### Notes

The generator requires a Transmission, a Coil, and a Magnet to be brought here and installed. Installing all three (one action per item) powers the generator and permanently unlocks the boss door on the east wall of the Control Room. This path is `loud = true` and impassable for the Medic.

  

---

  

### Stairwell #2

  

#### Description

An industrial staircase descending into the basement. A sign reading "B1 — MACHINING" is bolted above the archway. The sound of something dragging echoes faintly from below.

  

#### Connections

- East -> Generator Room

- West -> Machining Room

  

#### Notes

Stairwells cannot be occupied — characters pass through in a single move.

  

---

  

### Machining Room

  

#### Description

Heavy equipment fills the room — lathes, drill presses, and a large miter saw mounted to a central worktable. The saw's blade is spinning on its own, slowly, as if something is still running current through it. Sparks have scorched the floor in a wide arc around it.

  

#### State Change — Miter Saw Defeated

The miter saw grinds to a halt, its blade spinning down with a long metallic whine. The room is quiet for the first time. Something glints under the debris near the far wall.

  

#### Connections

- East -> Stairwell #2

- South -> Server Room

  

#### Enemy

**Miter Saw** — 50 HP, 10 damage. If a character remains in the same room for more than 1 consecutive turn, it deals its damage value. Must be defeated to safely search the room.

  

#### Items

- Arm Piece #1 (found by searching after the Miter Saw is defeated)

- Coil (can be crafted here — bring the Copper Wire from the Cabin and perform the "machine" action after the Miter Saw is defeated; costs one turn)

  

---

  

### Server Room

  

#### Description

Racks of servers fill the room floor to ceiling, all of them dark and silent. Cooling fans that should be spinning sit still. Without power, this room is just dead weight. Maybe the Droid can interface with the hardware directly.

  

#### State Change — Servers Online

The racks shudder awake. Drives spin up, status lights blink in cascading rows, and the temperature in the room drops a few degrees. A pathway to the northwest shimmers open as an access connection establishes itself.

  

#### Connections

- North -> Machining Room

- West -> Stairwell #4

- South -!> Boiler Level 2 (requires Droid to have Drill — `blockage = true`)

- Northwest -!> Library (locked until Droid powers on the servers)

  

#### Items

None

  

#### Notes

The Droid must be present in the Server Room to power on the servers (one action). The northwest path to the Library is locked until the servers are online. The south path to Boiler Level 2 requires the Droid to have the Drill attachment.

  

---

  

### Stairwell #4

  

#### Description

A maintenance stairwell, narrower than the others. Peeling paint and exposed conduit run along the walls.

  

#### Connections

- West -> Server Room

- North -!> Materials Room (one-way hatch — initially cannot be opened from this side; Droid with Arm Attachment can reach the connection point above the hatch to open it permanently)

  

#### Notes

On first entry from the Materials Room, the Droid notes the hatch but cannot open it without the Arm Attachment. Once the Arm Attachment is acquired, the Droid can open the hatch from either side, making this a permanent two-way connection.

  

---

  

### Test Lab

  

#### Description

A long room lined with examination tables, most of them overturned. Glass from shattered containment units crunches underfoot. Whatever was being tested here, someone left in a hurry. Equipment is still running — readings scroll across monitors, logging data that no one will ever read.

  

#### Connections

- West -!> Hallway (tech = true — Droid interacts with the tech lock on the west door to open permanently)

- Southwest -> Cubicles

- North -!> Stress Room (tunnel = true — Merc and Medic cannot enter)

- Southeast -!> Stress Room (tunnel = true — Merc and Medic cannot enter)

  

#### Items

- Decryption Key #1 (found by searching)

  

#### Notes

All 4 Decryption Keys must be in the Droid's inventory to unlock the boss door into the Control Room. Key #1 is the first one available and can be found early once the Test Lab is accessible.

  

---

  

### Cubicles

  

#### Description

Row after row of office cubicles, most of them ransacked. Papers are scattered everywhere. Whiteboards are covered in half-erased equations. A narrow gap in the western wall — barely wide enough for a small machine to squeeze through — leads somewhere further in.

  

#### Connections

- Northeast -> Test Lab

- West -!> Materials Room (tunnel = true — Droid only)

  

#### Items

None

  

---

  

### Materials Room

  

#### Description

A small, cluttered storage room packed with heavy equipment and raw materials. A large, industrial magnet sits on a reinforced platform in the corner. It's far too heavy for most people to budge. On the far wall, a one-way hatch sits recessed into the concrete.

  

#### Connections

- East -!> Cubicles (tunnel = true — Droid only entrance)

- South -> Stairwell #4 (one-way hatch — permanently unlocked after Droid opens it with Arm Attachment)

  

#### Items

- Magnet (`heavy = true` — only the Merc can carry this item)

  

#### Notes

The first time the Droid enters this room, it notes the one-way hatch to Stairwell #4 but cannot open it without the Arm Attachment. Once the Arm Attachment is acquired, the Droid can reach the connection point above the hatch frame to open it permanently.

  

---

  

### Library

  

#### Description

Tall shelves packed with binders, manuals, and printed reports stretch from floor to ceiling. A reference desk sits in the center, buried under stacked folders. The room feels preserved — untouched, as if nobody had any reason to come here in a hurry.

  

#### Connections

- Southeast -!> Server Room (only accessible when servers are online)

  

#### Items

- Arm Piece #2 (found by searching)

  

#### Notes

Once the Droid has both Arm Piece #1 and Arm Piece #2 in its inventory, it can assemble the Arm Attachment (costs one turn). Once the Droid has both the Arm Attachment and the Blade, it can craft the Drill (costs one turn).

  

---

  

## Boiler Level

  

---

  

### Boiler Level 2

  

#### Description

Steam pipes run overhead, some of them venting slow wisps of white vapor. Heavy machinery lines the walls, all of it cold and still. A metal case sits bolted to a support pillar in the center of the room.

  

#### Connections

- North -!> Server Room (requires Droid to have Drill — `blockage = true`)

- East -> Stairwell #3

- South -!> Walkway (requires Droid to have Drill — `blockage = true`)

  

#### Items

- Decryption Key #2 (inside the metal case)

  

---

  

### Stairwell #3

  

#### Description

A utilitarian stairwell leading upward into the boiler level. Rust streaks the concrete where water has seeped through the walls for years.

  

#### Connections

- West -> Boiler Level 2

- East -> Boilerworks

  

#### Notes

Stairwells cannot be occupied — characters pass through in a single move.

  

---

  

### Boilerworks

  

#### Description

An enormous chamber dominated by three massive boilers, their pipes snaking into the ceiling and walls. One of them has ruptured at some point — scorch marks fan out across the eastern wall. On a workbench to the side, a mechanical transmission sits, removed cleanly and deliberately from some larger machine.

  

#### Connections

- West -> Stairwell #3

- North -!> Stress Room (requires Droid to have Arm Attachment — connection point is above the doorframe, out of reach for all other characters)

  

#### Items

- Transmission

  

---

  

### Stress Room

  

#### Description

A reinforced chamber with padded walls and a thick observation window running along the north face. Testing equipment — most of it broken — is clamped to a central platform. Cables dangle from the ceiling.

  

#### Connections

- South -!> Boilerworks (requires Arm Attachment to open from Boilerworks side)

- North -!> Test Lab (tunnel = true — Merc and Medic cannot enter)

- Southeast -!> Test Lab (tunnel = true — Merc and Medic cannot enter)

  

#### Items

None

  

---

  

### Walkway

  

#### Description

A narrow suspended walkway stretches across a dark shaft. The air tastes like ozone. Far below, something pulses with a faint blue light. At the center of the walkway, a swarm of small drones hovers in a loose cluster, their red targeting sensors sweeping back and forth.

  

#### State Change — Drone Cluster Defeated

The drones scatter and crash, raining down into the shaft below. The walkway is clear. A terminal at the south end of the platform flickers to life. Something metallic clatters to the floor near the wreckage.

  

#### Connections

- North -!> Boiler Level 2 (requires Droid to have Drill — `blockage = true`)

- South -!> Vault (Drone Cluster must be defeated first; then player must win the Connect 4 minigame at the terminal)

  

#### Enemy

**Drone Cluster** — 100 HP, 20 damage. Patrols the walkway.

  

#### Items

- Decryption Key #4 (drops from the Drone Cluster on defeat)

  

#### Notes

After the Drone Cluster is defeated, a Connect 4 terminal activates at the south end of the walkway. The player must win the ConnectFour minigame to unlock the path south into the Vault. If the player loses, they may try again.

  

---

  

### Vault

  

#### Description

Reinforced walls, a heavy blast door now hanging open. The room is sparse. A cracked case on the floor holds a torn note scrawled in hasty handwriting: *"It was the AI. It shut everything down — killed the power, locked the doors, locked us in. We had no way out. If you're reading this, you have to stop it. The kill key is in here. Use it."* A security panel on the far wall holds a glowing kill key behind a glass cover.

  

#### Connections

- North -> Walkway

- West -!> Key Room (tunnel = true — Droid only)

  

#### Items

- Kill Key (required item — must be in party inventory to enter the Control Room and start the final sequence)

- Note (triggers mission objective update: Opus 6.4 is the cause of the shutdown and deaths — it must be destroyed to win)

  

---

  

### Key Room

  

#### Description

A tiny access room, barely large enough to turn around in. A single terminal sits on a shelf, its screen displaying a colorful word grid. A sealed compartment beneath it waits for the puzzle to be solved.

  

#### State Change — Puzzle Solved / Protector Bot Spawned

The compartment clicks open. Inside is a small metallic card. Before `[Characters Selected]` can reach for it, a panel in the wall splits open and a hulking security bot steps out, its red optical sensors locking on immediately.

  

#### Connections

- East -!> Vault (tunnel = true — Droid only)

  

#### Enemy

**Protector Bot** — 200 HP, 10 damage. Spawns immediately after the ConnectionsGame minigame is completed. Must be defeated to safely leave the room.

  

#### Items

- Decryption Key #3 (locked behind the ConnectionsGame minigame — Droid must solve the NYT Connections-style puzzle to retrieve it)

  

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

- West -!> Generator Room (requires: generator fully powered AND Droid has all 4 Decryption Keys AND Kill Key is in party inventory)

  

#### Boss Fight — Phase 1: Opus 6.4

  

The fight takes place across 4 lanes. Each character occupies one lane at a time.

  

**Lane rules:**

- Characters can move between adjacent lanes each turn (lane 1→2, 2→3, 3→4). Movement does **not** wrap — a character in lane 4 cannot move to lane 1.

- Staying in the same lane for more than 3 consecutive turns triggers radiation damage: 10 HP per turn.

  

**Attack pattern:**

- Each turn, Opus 6.4 selects a lane and marks it as primed. On the following turn, it fires a beam at that lane for 30 damage to any character in it.

- The primed lane is announced to the player at the end of the turn it is selected.

  

**Weak spot:**

- After 5 turns have passed, Opus 6.4's weak spot opens on its central core.

- The Merc can shoot the weak spot from lanes 2 or 3 only, dealing 20 damage per shot.

- Opus 6.4 has 100 HP. It is defeated when reduced to 0.

  

---

  

#### Boss Fight — Phase 2: Droid Takeover

  

Immediately after Opus 6.4 is destroyed, it uploads itself into the Droid. The Droid becomes a hostile boss unit and the fight continues in the same 4-lane arena.

  

**Rules are identical to Phase 1, with these changes:**

- The Droid deals 40 damage per beam (10 more than Opus 6.4).

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

| Copper Wire | Raw material for Coil | Any | Found in Cabin |

| Blade | Component for Drill | Any | Found in Storage Room |

| Battery Replacement | Activates R3D3 | Any | Found in Storage Room |

| Arm Piece #1 | Component for Arm Attachment | Any | Found in Machining Room |

| Arm Piece #2 | Component for Arm Attachment | Any | Found in Library |

| Arm Attachment | Assembled from Arm Pieces #1 and #2 | Droid only | Assembled by Droid (1 turn) |

| Drill | Crafted from Blade + Arm Attachment | Droid only | Crafted by Droid (1 turn) |

| Coil | Generator component | Any | Machined from Copper Wire in Machining Room |

| Transmission | Generator component | Any | Found in Boilerworks |

| Magnet | Generator component | Merc only | `heavy = true` — found in Materials Room |

| Decryption Key #1 | Required for boss door | Droid only | Found in Test Lab (search) |

| Decryption Key #2 | Required for boss door | Droid only | Found in Boiler Level 2 |

| Decryption Key #3 | Required for boss door | Droid only | Awarded after ConnectionsGame in Key Room |

| Decryption Key #4 | Required for boss door | Droid only | Drops from Drone Cluster on defeat |

| Kill Key | Required to enter Control Room | Any | Found in Vault |

  

---
<!--stackedit_data:
eyJoaXN0b3J5IjpbLTc2ODUzNjQxOV19
-->