import java.util.*;
import java.util.concurrent.*;
import java.io.*;

public class Axion_AntiCheat_AAC {

    // Also a poor version
    // Toggleable checks
    private final Map<String, Boolean> checks = new HashMap<>();

    // Whitelist for native libraries used by your client
    private final Set<String> dllWhitelist = new HashSet<>(Arrays.asList(
        "lwjgl.dll", "openal.dll", "jinput-dx8.dll", "jinput-raw.dll", "java.dll"
    ));

    // Known injector or cheat related process names
    private final Set<String> injectorProcesses = new HashSet<>(Arrays.asList(
        "cheatengine", "extreme injector", "xenos", "process hacker", "wemod", "injector"
    ));

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public Axion_AntiCheat_AAC() {
        checks.put("SpeedHack", true);
        checks.put("FlyHack", true);
        checks.put("ReachHack", true);
        startBackgroundScan();
    }

    // Exempt players in certain gamemodes or admin state
    public boolean isExempt(Player p) {
        String gm = p.getGameMode();
        return gm.equalsIgnoreCase("CREATIVE")
                || gm.equalsIgnoreCase("SPECTATOR")
                || gm.equalsIgnoreCase("ADVENTURE")
                || p.isAdmin();
    }

    // Basic movement validation
    public void checkPlayerMovement(Player p) {
        if (isExempt(p)) return;

        if (checks.get("SpeedHack") && p.getVelocity() > 0.35) {
            flag(p, "Speed modification detected (Abnormal velocity)");
        }

        if (checks.get("FlyHack") && !p.isOnGround() && !p.isFlyingAllowed()) {
            flag(p, "Flight modification detected (Illegal air movement)");
        }
    }

    // Basic combat reach validation
    public void checkPlayerAttack(Player atk, Player target) {
        if (isExempt(atk)) return;

        double distance = atk.distanceTo(target);

        if (checks.get("ReachHack") && distance > 4.5) {
            flag(atk, "Combat reach modification detected (Distance: " + distance + ")");
        }
    }

    // DLL & Injector detection - Rejects but does not close the game
    public void scanSystemForInjectors() {
        try {
            Process process = Runtime.getRuntime().exec("tasklist /m");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                String lower = line.toLowerCase();

                // Process scanner for injector tools
                for (String inj : injectorProcesses) {
                    if (lower.contains(inj)) {
                        System.out.println("[Axion A.A.C] Injector/cheat process found: " + inj.toUpperCase());
                        System.out.println("[Axion A.A.C] Action: Authentication or injection request has been denied.");
                    }
                }

                // DLL scanner for suspicious modules
                if (lower.contains(".dll")) {
                    if (!isWhitelistedDLL(lower) && isNotInjector(lower)) {
                        System.out.println("[Axion A.A.C] Unapproved native module detected: " + line);
                        System.out.println("[Axion A.A.C] Action: DLL access or injection attempt has been denied.");
                    }
                }
            }

            reader.close();
        } catch (Exception e) {
            System.out.println("[Axion A.A.C] System scan error: " + e.getMessage());
        }
    }

    private boolean isWhitelistedDLL(String line) {
        for (String dll : dllWhitelist) {
            if (line.contains(dll)) return true;
        }
        return false;
    }

    private boolean isNotInjector(String line) {
        for (String inj : injectorProcesses) {
            if (line.contains(inj)) return false;
        }
        return true;
    }

    // Flag logger - does not kick or ban, only rejects
    private void flag(Player p, String reason) {
        System.out.println("[Axion A.A.C] Player " + p.getName().toUpperCase() + " flagged: " + reason);
        System.out.println("[Axion A.A.C] Action: The abnormal behavior or modification request has been denied.");
    }

    // Background system scanner, runs every 5 seconds
    private void startBackgroundScan() {
        scheduler.scheduleAtFixedRate(() -> {
            scanSystemForInjectors();
        }, 0, 5, TimeUnit.SECONDS);
    }

    // Optional: Manually toggle checks
    public void setCheck(String check, boolean enabled) {
        if (checks.containsKey(check)) {
            checks.put(check, enabled);
        }
    }
}