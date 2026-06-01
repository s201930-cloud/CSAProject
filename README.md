# HELIOS//OFFLINE

> *A text-based Java adventure game.*

---

## Story

A government lab went dark three days ago. No contact, no explanation. You've been sent in to find out why.

---

## How to Play

Type what you want your characters to do. The game reads natural language and detects action keywords:

| Keyword | Action |
|---------|--------|
| `move` | Move to an adjacent room |
| `shoot` | Merc fires at an enemy |
| `heal` | Medic uses a bandage or medkit |
| `search` | Search the room for items |
| `interact` | Interact with a door or object |
| `upload` | Upload the Kill Code *(final boss only)* |

You control **3 characters**. Each gets one action per turn. The active character's name is shown before each input prompt.

---

## Characters

| Character | HP | Role |
|-----------|----|------|
| **Medic** | 75 | Keeps the team alive. 10 bandages (+20 HP), 3 medkits (full heal). Healing costs their move. |
| **Merc** | 150 | The only one who can shoot enemies. 30 bullets. Shooting costs their move. |
| **Droid** | 50 | Unlocks tech doors and clears blockages. Found early in the Storage Room. |

---

## Rooms

```
[Storage Room]
      |
   [Lobby]
      |
    [Lab] ————————— [Key Room]
      |                  
[Generator Room]        
      |                  
   [Vault]              
      |                  
[Control Room]          
```

- **Storage Room** — Activate the Droid here. Grab the Drill.
- **Lobby** — The Lab door is tech-locked. Droid must open it.
- **Lab** — Fight the Drone Cluster. Droid clears the blockage south.
- **Key Room** — Droid solves the Connections puzzle → Decryption Key. Protector Bot spawns.
- **Generator Room** — Merc installs the Magnet to power the facility.
- **Vault** — Find the Kill Key and the truth about what happened.
- **Control Room** — Final boss. Needs the generator on, the Decryption Key, and the Kill Key to enter.

---

## Enemies

| Enemy | HP | Damage | Notes |
|-------|----|--------|-------|
| Drone Cluster | 60 | 15/turn | Lab. Drops Decryption Key. |
| Protector Bot | 120 | 10/turn | Key Room. Spawns after puzzle. |
| Opus 6.4 | 200 | 20/turn | Boss. Shoot it to 0 HP. |
| Droid *(possessed)* | — | 20/turn | Can't be shot. Merc must `upload`. |

---

## Minigame — Connections

Triggered in the Key Room. Group 16 words into 4 categories of 4.

- 4 guesses total
- "One away..." hint if 3 out of 4 words are right
- Solve it to get the Decryption Key (and wake up a very angry security bot)

---

## Win Condition

1. Activate the Droid → get the Drill
2. Open the Lab → fight the Drone Cluster → get the Decryption Key
3. Solve Connections in the Key Room → get the Magnet
4. Power the generator
5. Clear the blockage → reach the Vault → get the Kill Key
6. Enter the Control Room → shoot Opus 6.4 → `upload` to the Droid

```
HELIOS//OFFLINE
```

---

*Built in Java. No external libraries.*
