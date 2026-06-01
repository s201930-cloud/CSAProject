import java.util.*;

/**
 * HELIOS//OFFLINE - Text Adventure Game
 * Fully coded in Java with zero external dependencies.
 * * Group Members: Aman P | Rishi B | Pranay B | Eshaan S
 * * To play:
 * 1. Save this file as HeliosOffline.java
 * 2. Compile: javac HeliosOffline.java
 * 3. Run: java HeliosOffline
 */
public class Main {

    // --- GAME STATE VARIABLES ---
    private static final Scanner scanner = new Scanner(System.in);
    private static boolean gameRunning = true;

    // Character Stats
    private static int medicHp = 75;
    private static final int MEDIC_MAX_HP = 75;
    private static int medicBandages = 10;
    private static int medicMedkits = 3;
    private static String medicRoom = "Entrance";

    private static int mercHp = 150;
    private static final int MERC_MAX_HP = 150;
    private static int mercBullets = 30;
    private static String mercRoom = "Entrance";

    private static boolean droidActivated = false;
    private static int droidHp = 50;
    private static final int DROID_MAX_HP = 50;
    private static boolean droidHasDrill = false;
    private static String droidRoom = "Storage Room";

    // Global Progression Flags
    private static boolean labTechUnlocked = false;
    private static boolean vaultBlockageCleared = false;
    private static boolean generatorPowered = false;
    private static boolean connectionsSolved = false;

    // Room Inventories
    private static final List<String> storageRoomItems = new ArrayList<>(Arrays.asList("Battery Replacement", "Drill"));
    private static final List<String> keyRoomItems = new ArrayList<>(Arrays.asList("Magnet"));
    private static final List<String> vaultItems = new ArrayList<>(Arrays.asList("Kill Key", "Note"));

    // Character Inventories
    private static final List<String> medicInventory = new ArrayList<>();
    private static final List<String> mercInventory = new ArrayList<>();
    private static final List<String> droidInventory = new ArrayList<>();

    // Enemies Stats
    private static int droneClusterHp = 60;
    private static boolean droneClusterAlive = true;

    private static boolean protectorBotSpawned = false;
    private static int protectorBotHp = 120;
    private static boolean protectorBotAlive = true;

    private static int opusHp = 200;
    private static boolean opusAlive = true;
    private static boolean droidBossActive = false;

    // Tracking for Room Entry Messages
    private static final Set<String> visitedRooms = new HashSet<>();

    public static void main(String[] args) {
        printIntro();

        while (gameRunning) {
            // Check global loss condition
            if (medicHp <= 0 && mercHp <= 0 && (!droidActivated || droidHp <= 0)) {
                System.out.println("\n[GAME OVER] All active squad members have perished. HELIOS remains online.");
                gameRunning = false;
                break;
            }

            System.out.println("\n========================================================");
            System.out.println("                     NEW TURN");
            System.out.println("========================================================");
            printStatusSummary();

            // 1. Medic Action Phase
            if (medicHp > 0) {
                handleCharacterTurn("Medic");
                if (!gameRunning) break;
            }

            // 2. Merc Action Phase
            if (mercHp > 0) {
                handleCharacterTurn("Mercenary");
                if (!gameRunning) break;
            }

            // 3. Droid Action Phase
            if (droidActivated && droidHp > 0) {
                handleCharacterTurn("Droid");
                if (!gameRunning) break;
            }

            // 4. End-of-Turn Combat Phase
            processEnemyAttacks();
        }
        scanner.close();
    }

    private static void printIntro() {
        System.out.println("========================================================");
        System.out.println("                    HELIOS//OFFLINE                     ");
        System.out.println("========================================================");
        System.out.println("Two government officials infiltrate a classified lab complex.");
        System.out.println("Radio crackles: \"The lab went dark three days ago.");
        System.out.println("Get in, find out what happened, get out.\"\n");
        System.out.println("Available Actions: 'move [room]', 'shoot', 'heal', 'search', 'interact', 'upload'");
        System.out.println("========================================================");
    }

    private static void printStatusSummary() {
        System.out.print("[STATUS] Medic: " + medicHp + "/" + MEDIC_MAX_HP + " HP (Room: " + medicRoom + ")");
        System.out.print(" | Merc: " + mercHp + "/" + MERC_MAX_HP + " HP (Room: " + mercRoom + ")");
        if (droidActivated) {
            System.out.print(" | Droid: " + droidHp + "/" + DROID_MAX_HP + " HP (Room: " + droidRoom + ")");
        } else {
            System.out.print(" | Droid: INACTIVE (Storage Room)");
        }
        System.out.println();
    }

    private static String getRoomLocation(String charName) {
        if (charName.equals("Medic")) return medicRoom;
        if (charName.equals("Mercenary")) return mercRoom;
        return droidRoom;
    }

    private static void setRoomLocation(String charName, String newRoom) {
        if (charName.equals("Medic")) medicRoom = newRoom;
        else if (charName.equals("Mercenary")) mercRoom = newRoom;
        else droidRoom = newRoom;
    }

    private static List<String> getCharacterInventory(String charName) {
        if (charName.equals("Medic")) return medicInventory;
        if (charName.equals("Mercenary")) return mercInventory;
        return droidInventory;
    }

    private static void printRoomDescription(String roomName) {
        System.out.println("\n--- " + roomName.toUpperCase() + " ---");
        switch (roomName) {
            case "Entrance":
                System.out.println("Two agents stand outside a sealed facility door. The open path leads East.");
                break;
            case "Lobby":
                System.out.println("Fluorescent lights flicker lazily. A receptionist lies dead across the desk.");
                if (labTechUnlocked) {
                    System.out.println("The heavy titanium door leading South to the Lab stands open (Tech Lock Unlocked).");
                } else {
                    System.out.println("A security door leading South requires a terminal override (Tech Lock).");
                }
                System.out.println("An unmitigated hallway stretches East towards the Storage Room.");
                break;
            case "Storage Room":
                if (droidActivated) {
                    System.out.println("Dim emergency lighting casts long shadows. R3D3 is booted up, humming smoothly.");
                } else {
                    System.out.println("Dim emergency lighting. A dented security robot labeled 'R3D3' sits slumped in the corner.");
                }
                break;
            case "Lab":
                System.out.println("Overturned workstations and shattered glass litter the floor.");
                if (droneClusterAlive) {
                    System.out.println("[ALERT] A hostile Drone Cluster patrols near the ceiling, sweeping sensors!");
                } else {
                    System.out.println("The smoking remains of the Drone Cluster lay scattered on the ground.");
                }
                if (vaultBlockageCleared) {
                    System.out.println("The path South to the Vault has been completely cleared of debris.");
                } else {
                    System.out.println("The path South to the Vault is completely blocked by heavy steel columns.");
                }
                break;
            case "Key Room":
                System.out.println("A cramped monitoring station. A master configuration terminal stands in the middle.");
                if (!connectionsSolved) {
                    System.out.println("The terminal screen displays a colorful 4x4 matrix puzzle.");
                } else if (protectorBotAlive) {
                    System.out.println("[ALERT] A heavy automated Protector Bot blocks the terminal terminal panel!");
                } else {
                    System.out.println("The puzzle terminal is dark, and the Protector Bot is offline.");
                }
                break;
            case "Generator Room":
                System.out.println("A massive chamber housing industrial turbine systems.");
                if (generatorPowered) {
                    System.out.println("The turbines roar dynamically, casting deep power patterns across the room.");
                } else {
                    System.out.println("The turbine arrays are dark, missing a critical operational core component.");
                }
                break;
            case "Vault":
                System.out.println("A reinforced high-security room. A bloody note is attached to the glass security chassis.");
                break;
            case "Control Room":
                System.out.println("Every terminal monitor rhythmically blinks: HELIOS//ONLINE.");
                if (opusAlive) {
                    System.out.println("[BOSS] The towering server cluster pulses deep red. OPUS 6.4 AI Core detected!");
                } else if (droidBossActive) {
                    System.out.println("[BOSS PHASE 2] OPUS has hijacked the Droid! Crimson visual matrices activated!");
                }
                break;
        }
    }

    private static void handleCharacterTurn(String charName) {
        String currentRoom = getRoomLocation(charName);
        
        // Print description if it's the first time entry or after updates
        printRoomDescription(currentRoom);

        boolean actionTokenUsed = false;
        while (!actionTokenUsed) {
            System.out.print("\n[" + charName + "] Enter command: ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.contains("move")) {
                actionTokenUsed = processMoveCommand(charName, currentRoom, input);
            } else if (input.contains("shoot")) {
                actionTokenUsed = processShootCommand(charName, currentRoom);
            } else if (input.contains("heal")) {
                actionTokenUsed = processHealCommand(charName);
            } else if (input.contains("search")) {
                actionTokenUsed = processSearchCommand(charName, currentRoom);
            } else if (input.contains("interact")) {
                actionTokenUsed = processInteractCommand(charName, currentRoom);
            } else if (input.contains("upload")) {
                actionTokenUsed = processUploadCommand(charName, currentRoom);
            } else {
                System.out.println("Unknown command pattern. Please use: move, shoot, heal, search, interact, or upload.");
            }
        }
    }

    private static boolean processMoveCommand(String charName, String currentRoom, String input) {
        String destination = "";
        if (input.contains("entrance")) destination = "Entrance";
        else if (input.contains("lobby")) destination = "Lobby";
        else if (input.contains("storage")) destination = "Storage Room";
        else if (input.contains("lab")) destination = "Lab";
        else if (input.contains("key")) destination = "Key Room";
        else if (input.contains("generator")) destination = "Generator Room";
        else if (input.contains("vault")) destination = "Vault";
        else if (input.contains("control")) destination = "Control Room";

        if (destination.isEmpty()) {
            System.out.println("Move where? Please include destination room in command string (e.g. 'move lobby').");
            return false;
        }

        if (destination.equals(currentRoom)) {
            System.out.println("You are already in the " + currentRoom + ".");
            return false;
        }

        // Validate Paths
        if (currentRoom.equals("Entrance") && destination.equals("Lobby")) {
            setRoomLocation(charName, "Lobby");
            return true;
        }
        else if (currentRoom.equals("Lobby")) {
            if (destination.equals("Entrance")) {
                setRoomLocation(charName, "Entrance");
                return true;
            } else if (destination.equals("Storage Room")) {
                setRoomLocation(charName, "Storage Room");
                return true;
            } else if (destination.equals("Lab")) {
                if (labTechUnlocked) {
                    setRoomLocation(charName, "Lab");
                    return true;
                } else {
                    System.out.println("Bypassed denied. The South security gate is locked by a Tech Lock. Only a Droid can override it.");
                    return false;
                }
            }
        }
        else if (currentRoom.equals("Storage Room") && destination.equals("Lobby")) {
            setRoomLocation(charName, "Lobby");
            return true;
        }
        else if (currentRoom.equals("Lab")) {
            if (destination.equals("Lobby")) {
                setRoomLocation(charName, "Lobby");
                return true;
            } else if (destination.equals("Key Room")) {
                setRoomLocation(charName, "Key Room");
                return true;
            } else if (destination.equals("Generator Room")) {
                setRoomLocation(charName, "Generator Room");
                return true;
            } else if (destination.equals("Vault")) {
                if (vaultBlockageCleared) {
                    setRoomLocation(charName, "Vault");
                    return true;
                } else {
                    System.out.println("Path blocked. Massive metal structures are preventing access. A Drill-equipped Droid can clear this.");
                    return false;
                }
            }
        }
        else if (currentRoom.equals("Key Room") && destination.equals("Lab")) {
            setRoomLocation(charName, "Lab");
            return true;
        }
        else if (currentRoom.equals("Generator Room")) {
            if (destination.equals("Lab")) {
                setRoomLocation(charName, "Lab");
                return true;
            } else if (destination.equals("Control Room")) {
                boolean hasKillKey = medicInventory.contains("Kill Key") || mercInventory.contains("Kill Key") || droidInventory.contains("Kill Key");
                boolean hasDecryptionKey = droidInventory.contains("Decryption Key");

                if (generatorPowered && hasDecryptionKey && hasKillKey) {
                    setRoomLocation(charName, "Control Room");
                    return true;
                } else {
                    System.out.println("Access Denied to Control Room! Check systems requirements:");
                    System.out.println(" - Generator Power Grid Online: " + (generatorPowered ? "YES" : "NO"));
                    System.out.println(" - Decryption Key in Droid Memory Matrix: " + (hasDecryptionKey ? "YES" : "NO"));
                    System.out.println(" - Physical Kill Key present in group: " + (hasKillKey ? "YES" : "NO"));
                    return false;
                }
            }
        }
        else if (currentRoom.equals("Vault") && destination.equals("Lab")) {
            setRoomLocation(charName, "Lab");
            return true;
        }
        else if (currentRoom.equals("Control Room") && destination.equals("Generator Room")) {
            setRoomLocation(charName, "Generator Room");
            return true;
        }

        System.out.println("You cannot directly travel from " + currentRoom + " to " + destination + ".");
        return false;
    }

    private static boolean processShootCommand(String charName, String currentRoom) {
        if (!charName.equals("Mercenary")) {
            System.out.println("Combat restriction: Only the Mercenary can execute weapons procedures.");
            return false;
        }

        if (mercBullets <= 0) {
            System.out.println("Out of ammo! Weapon action unavailable.");
            return false;
        }

        if (currentRoom.equals("Lab") && droneClusterAlive) {
            mercBullets--;
            droneClusterHp -= 10;
            System.out.println("[WEAPON] Mercenary fires rifle! Drone Cluster takes 10 DMG. (Remaining: " + droneClusterHp + " HP). Bullets left: " + mercBullets);
            if (droneClusterHp <= 0) {
                droneClusterAlive = false;
                System.out.println("[DESTROYED] The Drone Cluster falls crashing down! It dropped a Decryption Key.");
                storageRoomItems.add("Decryption Key"); // Dropped item now extractable via search
            }
            return true;
        }
        else if (currentRoom.equals("Key Room") && protectorBotSpawned && protectorBotAlive) {
            mercBullets--;
            protectorBotHp -= 10;
            System.out.println("[WEAPON] Mercenary fires rifle! Protector Bot takes 10 DMG. (Remaining: " + protectorBotHp + " HP). Bullets left: " + mercBullets);
            if (protectorBotHp <= 0) {
                protectorBotAlive = false;
                System.out.println("[DESTROYED] The Protector Bot slumps over, systematically fried.");
            }
            return true;
        }
        else if (currentRoom.equals("Control Room") && opusAlive) {
            mercBullets--;
            opusHp -= 10;
            System.out.println("[WEAPON] Mercenary fires rifle! OPUS 6.4 Mainframe takes 10 DMG. (Remaining: " + opusHp + " HP). Bullets left: " + mercBullets);
            if (opusHp <= 0) {
                opusAlive = false;
                droidBossActive = true;
                System.out.println("\n[CRITICAL] OPUS 6.4 core mainframe exploded!");
                System.out.println("[ALERT] Emergency wireless protocol initiated. OPUS has uploaded into the Droid!");
                System.out.println("The Droid's systems override into hostile patterns. Droid Takeover active!");
            }
            return true;
        }
        else if (currentRoom.equals("Control Room") && droidBossActive) {
            mercBullets--;
            System.out.println("[SYSTEM ALERT] Your bullets are completely deflective against the Droid armor! Kinetic logic invalid against the main network. You must use 'upload' to inject the Kill Code!");
            return true;
        }

        System.out.println("There are no valid hostile targets in this room.");
        return false;
    }

    private static boolean processHealCommand(String charName) {
        if (!charName.equals("Medic")) {
            System.out.println("Medical training missing: Only the Medic can handle pharmaceutical allocations.");
            return false;
        }

        if (medicBandages <= 0 && medicMedkits <= 0) {
            System.out.println("All medical gear exhausted!");
            return false;
        }

        System.out.print("Select target to treat (medic, merc, droid): ");
        String targetInput = scanner.nextLine().toLowerCase();
        String target = "";
        if (targetInput.contains("medic") && medicHp > 0) target = "Medic";
        else if (targetInput.contains("merc") && mercHp > 0) target = "Mercenary";
        else if (targetInput.contains("droid") && droidActivated && droidHp > 0) target = "Droid";

        if (target.isEmpty()) {
            System.out.println("Invalid target choice or designated target is deceased/inactive.");
            return false;
        }

        System.out.print("Use Bandage (+20 HP, Total left: " + medicBandages + ") or Medkit (Full Heal, Total left: " + medicMedkits + ")? Input 'bandage' or 'medkit': ");
        String kitInput = scanner.nextLine().toLowerCase();

        if (kitInput.contains("bandage")) {
            if (medicBandages <= 0) {
                System.out.println("No bandages available.");
                return false;
            }
            medicBandages--;
            applyHeal(target, 20, false);
            return true;
        } else if (kitInput.contains("medkit")) {
            if (medicMedkits <= 0) {
                System.out.println("No medkits available.");
                return false;
            }
            medicMedkits--;
            applyHeal(target, 0, true);
            return true;
        }

        System.out.println("Canceled Medical procedure action due to unrecognizable choice.");
        return false;
    }

    private static void applyHeal(String target, int amount, boolean fullHeal) {
        if (target.equals("Medic")) {
            medicHp = fullHeal ? MEDIC_MAX_HP : Math.min(MEDIC_MAX_HP, medicHp + amount);
            System.out.println("Medic healed. Current Health: " + medicHp + "/" + MEDIC_MAX_HP);
        } else if (target.equals("Mercenary")) {
            mercHp = fullHeal ? MERC_MAX_HP : Math.min(MERC_MAX_HP, mercHp + amount);
            System.out.println("Mercenary healed. Current Health: " + mercHp + "/" + MERC_MAX_HP);
        } else if (target.equals("Droid")) {
            droidHp = fullHeal ? DROID_MAX_HP : Math.min(DROID_MAX_HP, droidHp + amount);
            System.out.println("Droid localized repair applied. Current Health: " + droidHp + "/" + DROID_MAX_HP);
        }
    }

    private static boolean processSearchCommand(String charName, String currentRoom) {
        List<String> roomInventory = null;
        if (currentRoom.equals("Storage Room")) roomInventory = storageRoomItems;
        else if (currentRoom.equals("Key Room")) roomInventory = keyRoomItems;
        else if (currentRoom.equals("Vault")) roomInventory = vaultItems;
        else if (currentRoom.equals("Lab") && !droneClusterAlive && storageRoomItems.contains("Decryption Key")) {
            // Lab context handling for specific drop extraction
            if (charName.equals("Droid")) {
                storageRoomItems.remove("Decryption Key");
                droidInventory.add("Decryption Key");
                System.out.println("[SEARCH] Droid downloads the Decryption Key into localized memory matrices.");
                return true;
            } else {
                System.out.println("[SEARCH] Found Decryption Key data module. Only the Droid contains the appropriate storage interface.");
                return false;
            }
        }

        if (roomInventory == null || roomInventory.isEmpty()) {
            System.out.println("Searching yielded nothing of special interest in this area.");
            return true;
        }

        System.out.println("The scan uncovers items of functional note: " + roomInventory);

        // Process acquisitions
        Iterator<String> iterator = roomInventory.iterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            if (item.equals("Battery Replacement")) {
                getCharacterInventory(charName).add(item);
                System.out.println("[" + charName + "] collected: " + item);
                iterator.remove();
            } else if (item.equals("Drill")) {
                if (charName.equals("Droid")) {
                    droidHasDrill = true;
                    droidInventory.add(item);
                    System.out.println("[DROID SYSTEM UPDATE] Drill unit added directly to extraction assembly.");
                    iterator.remove();
                } else {
                    System.out.println("The structural Drill tool core is too structurally configured for regular human utility. Droid needs to fetch it.");
                }
            } else if (item.equals("Magnet")) {
                if (charName.equals("Mercenary")) {
                    mercInventory.add(item);
                    System.out.println("[MERCENARY INVENTORY] Heavy operational Magnet successfully lifted and secured.");
                    iterator.remove();
                } else {
                    System.out.println("This industrial Magnet is classified as [HEAVY]. Only the heavy structural frame of the Mercenary can move it.");
                }
            } else if (item.equals("Kill Key") || item.equals("Note")) {
                getCharacterInventory(charName).add(item);
                System.out.println("[" + charName + "] collected: " + item);
                if (item.equals("Note")) {
                    System.out.println(">> NOTE READS: \"It was the AI. It locked us in. The kill key is here - use it. System designated code: OPUS 6.4\"");
                }
                iterator.remove();
            }
        }
        return true;
    }

    private static boolean processInteractCommand(String charName, String currentRoom) {
        if (currentRoom.equals("Storage Room")) {
            if (droidActivated) {
                System.out.println("R3D3 is already running diagnostic frameworks.");
                return false;
            }
            List<String> inv = getCharacterInventory(charName);
            if (inv.contains("Battery Replacement")) {
                inv.remove("Battery Replacement");
                droidActivated = true;
                System.out.println("[SYSTEM ENGAGED] " + charName + " swaps in the Battery Replacement unit.");
                System.out.println("R3D3 optical circuits activate! The Droid character module is fully active.");
                return true;
            } else {
                System.out.println("R3D3 framework remains powerless. You require a Battery Replacement unit to interface.");
                return false;
            }
        }
        else if (currentRoom.equals("Lobby")) {
            if (!charName.equals("Droid")) {
                System.out.println("Human decryption methods inadequate. Only the Droid can connect to this lock architecture.");
                return false;
            }
            if (labTechUnlocked) {
                System.out.println("The connection terminal indicates the system is already overridden.");
                return false;
            }
            labTechUnlocked = true;
            System.out.println("[COMPROMISED] Droid bypasses the software locks! Access to the Lab is permanently open.");
            return true;
        }
        else if (currentRoom.equals("Key Room")) {
            if (connectionsSolved) {
                System.out.println("The puzzle matrix node has already been executed.");
                return false;
            }
            runConnectionsMinigame();
            return true;
        }
        else if (currentRoom.equals("Lab")) {
            if (!charName.equals("Droid") || !droidHasDrill) {
                System.out.println("Clearing this heavy structural blockage requires a Droid equipped with a hardware Drill.");
                return false;
            }
            if (vaultBlockageCleared) {
                System.out.println("The architecture is already clear.");
                return false;
            }
            vaultBlockageCleared = true;
            System.out.println("[EXCAVATED] Droid deploys the heavy Drill assembly, shattering the infrastructure blockages down to the Vault.");
            return true;
        }
        else if (currentRoom.equals("Generator Room")) {
            if (!charName.equals("Mercenary")) {
                System.out.println("Installation error: Only the Mercenary has the physical strength required to assemble the core.");
                return false;
            }
            if (mercInventory.contains("Magnet")) {
                mercInventory.remove("Magnet");
                generatorPowered = true;
                System.out.println("[FACILITY ENGAGED] Mercenary mounts the heavy Magnet component deep into the engine matrix.");
                System.out.println("The facility's primary dynamic grid snaps online! Power feeds to the Control Room terminal gate.");
                return true;
            } else {
                System.out.println("You cannot start the array without mounting the specific industrial Magnet component.");
                return false;
            }
        }

        System.out.println("There is nothing here to directly interact with using this command.");
        return false;
    }

    private static boolean processUploadCommand(String charName, String currentRoom) {
        if (!charName.equals("Mercenary")) {
            System.out.println("Security lock error: Only the Mercenary's satellite connection can handle the upload protocol.");
            return false;
        }

        if (!currentRoom.equals("Control Room") || !droidBossActive) {
            System.out.println("Transmission vectors missing: Upload protocol can only execute in Phase 2 Droid Takeover inside the Control Room.");
            return false;
        }

        System.out.println("\n[UPLOADING CODE...] Mercenary initializes wide-spectrum tactical connection to hijacked Droid system architecture...");
        System.out.println("Overriding network files... 34%... 67%... 100%.");
        System.out.println("HELIOS main sequence has been safely counter-extracted.");
        
        System.out.println("\n========================================================");
        System.out.println("                     HELIOS//OFFLINE                    ");
        System.out.println("========================================================");
        System.out.println("MISSION SUCCESSFUL. The rogue AI network is permanently erased.");
        System.out.println("The squad moves out as facility alarms go completely silent.");
        System.out.println("========================================================");
        
        gameRunning = false;
        return true;
    }

    private static void runConnectionsMinigame() {
        System.out.println("\n========================================================");
        System.out.println("             TERMINAL PROTOCOL: CONNECTIONS GAME         ");
        System.out.println("========================================================");
        System.out.println("Group the 16 words into 4 distinct matching categories of 4 words.");

        // Define exact categories
        List<String> yellow = Arrays.asList("centrifuge", "beaker", "pipette", "burette");
        List<String> green = Arrays.asList("relay", "capacitor", "resistor", "diode");
        List<String> blue = Arrays.asList("opus", "helios", "r3d3", "nexus");
        List<String> purple = Arrays.asList("cold", "dark", "silent", "still");

        Set<List<String>> remainingCategories = new HashSet<>(Arrays.asList(yellow, green, blue, purple));
        int guessesRemaining = 4;

        while (guessesRemaining > 0 && !remainingCategories.isEmpty()) {
            System.out.println("\n[ MATRIX GRID ]");
            System.out.println("[ CENTRIFUGE ] [ RELAY ]     [ OPUS ]   [ COLD ]");
            System.out.println("[ BEAKER ]     [ CAPACITOR ] [ HELIOS ] [ DARK ]");
            System.out.println("[ PIPETTE ]    [ RESISTOR ]  [ R3D3 ]   [ SILENT ]");
            System.out.println("[ BURETTE ]    [ DIODE ]     [ NEXUS ]  [ STILL ]");
            System.out.println("\nGuesses remaining: " + guessesRemaining);
            System.out.print("Enter four words separated by commas: ");
            
            String rawInput = scanner.nextLine().toLowerCase();
            String[] splitWords = rawInput.split(",");
            
            if (splitWords.length != 4) {
                System.out.println("Formatting Error: You must enter precisely 4 words separated by commas.");
                continue;
            }

            List<String> userGuess = new ArrayList<>();
            for (String w : splitWords) {
                userGuess.add(w.trim());
            }

            boolean categoryFound = false;
            boolean oneAwayDetected = false;

            for (List<String> category : remainingCategories) {
                int matchCount = 0;
                for (String word : userGuess) {
                    if (category.contains(word)) matchCount++;
                }

                if (matchCount == 4) {
                    System.out.println("\n[CORRECT] Category Identified!");
                    if (category.equals(yellow)) System.out.println("Category: Yellow - Lab glassware");
                    else if (category.equals(green)) System.out.println("Category: Green - Circuit components");
                    else if (category.equals(blue)) System.out.println("Category: Blue - Facility codenames");
                    else if (category.equals(purple)) System.out.println("Category: Purple - Words describing the lab on arrival");
                    
                    remainingCategories.remove(category);
                    categoryFound = true;
                    break;
                } else if (matchCount == 3) {
                    oneAwayDetected = true;
                }
            }

            if (!categoryFound) {
                if (oneAwayDetected) {
                    System.out.println("One away...");
                } else {
                    System.out.println("Incorrect grouping strategy.");
                }
                guessesRemaining--;
            }

            if (remainingCategories.isEmpty()) {
                connectionsSolved = true;
                protectorBotSpawned = true;
                droidInventory.add("Decryption Key");
                System.out.println("\n[DECRYPTION SUCCESSFUL] Core terminal system validated!");
                System.out.println(">> Decryption Key downloaded directly into Droid's local memory stack.");
                System.out.println("[ALARM] A heavy hidden wall panels slides away! A hostile Protector Bot steps out!");
                return;
            }

            if (guessesRemaining == 0) {
                System.out.println("\n[LOCKOUT] System architecture error. Rearranging matrix variables for another try...");
                guessesRemaining = 4; // Spec states: "Retries allowed on failure."
            }
        }
    }

    private static void processEnemyAttacks() {
        // Drone Cluster in Lab
        if (droneClusterAlive && (medicRoom.equals("Lab") || mercRoom.equals("Lab") || (droidActivated && droidRoom.equals("Lab")))) {
            String victim = chooseTargetInRoom("Lab");
            inflictDamage(victim, 15, "Drone Cluster");
        }

        // Protector Bot in Key Room
        if (protectorBotSpawned && protectorBotAlive && (medicRoom.equals("Key Room") || mercRoom.equals("Key Room") || (droidActivated && droidRoom.equals("Key Room")))) {
            String victim = chooseTargetInRoom("Key Room");
            inflictDamage(victim, 10, "Protector Bot");
        }

        // Boss Phase 1: OPUS 6.4 in Control Room
        if (opusAlive && !droidBossActive && (medicRoom.equals("Control Room") || mercRoom.equals("Control Room") || (droidActivated && droidRoom.equals("Control Room")))) {
            String victim = chooseTargetInRoom("Control Room");
            inflictDamage(victim, 20, "OPUS 6.4 AI Core");
        }

        // Boss Phase 2: Droid Takeover in Control Room
        if (droidBossActive && (medicRoom.equals("Control Room") || mercRoom.equals("Control Room"))) {
            // Droid targets either Merc or Medic in room
            String victim = "";
            if (mercRoom.equals("Control Room") && mercHp > 0) victim = "Mercenary";
            else if (medicRoom.equals("Control Room") && medicHp > 0) victim = "Medic";

            if (!victim.isEmpty()) {
                inflictDamage(victim, 20, "Hostile AI Droid");
            }
        }
    }

    private static String chooseTargetInRoom(String room) {
        List<String> presentChars = new ArrayList<>();
        if (medicRoom.equals(room) && medicHp > 0) presentChars.add("Medic");
        if (mercRoom.equals(room) && mercHp > 0) presentChars.add("Mercenary");
        if (droidActivated && droidRoom.equals(room) && droidHp > 0 && !droidBossActive) presentChars.add("Droid");

        if (presentChars.isEmpty()) return "";
        // Default tactical aggro priority (Mercenary -> Medic -> Droid)
        if (presentChars.contains("Mercenary")) return "Mercenary";
        if (presentChars.contains("Medic")) return "Medic";
        return presentChars.get(0);
    }

    private static void inflictDamage(String target, int amount, String attacker) {
        if (target.isEmpty()) return;
        
        System.out.println("\n[COMBAT ENCOUNTER] " + attacker + " lashes out against target presence!");
        if (target.equals("Medic")) {
            medicHp -= amount;
            System.out.println(">> Medic takes " + amount + " DMG! (Remaining: " + Math.max(0, medicHp) + " HP)");
        } else if (target.equals("Mercenary")) {
            mercHp -= amount;
            System.out.println(">> Mercenary takes " + amount + " DMG! (Remaining: " + Math.max(0, mercHp) + " HP)");
        } else if (target.equals("Droid")) {
            droidHp -= amount;
            System.out.println(">> Droid takes " + amount + " DMG! (Remaining: " + Math.max(0, droidHp) + " HP)");
        }
    }
}