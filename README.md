# DupeWatch Developer API Guide

The DupeWatch API lets you build plugins that hook into DupeWatch — read cases, check player risk, trigger scans, manage quarantine, and react to detection events. This guide walks you through setup and every available feature with real examples.

---

## Table of Contents

1. [Setup — Adding DupeWatch as a Dependency](#1-setup--adding-dupewatch-as-a-dependency)
2. [Getting the API Instance](#2-getting-the-api-instance)
3. [Events — Reacting to DupeWatch Detections](#3-events--reacting-to-dupewatch-detections)
4. [Cases — Reading and Managing Investigation Cases](#4-cases--reading-and-managing-investigation-cases)
5. [Player Risk — Checking How Suspicious a Player Is](#5-player-risk--checking-how-suspicious-a-player-is)
6. [Scanning — Triggering Your Own Scans](#6-scanning--triggering-your-own-scans)
7. [Quarantine — Managing Confiscated Items](#7-quarantine--managing-confiscated-items)
8. [Watchlist — Reading and Modifying Rules](#8-watchlist--reading-and-modifying-rules)
9. [Money Watch — Economy Anomalies](#9-money-watch--economy-anomalies)
10. [Stats — Server-Wide Metrics](#10-stats--server-wide-metrics)
11. [Complete Example Plugin](#11-complete-example-plugin)
12. [Quick Reference](#12-quick-reference)

---

## 1. Setup — Adding DupeWatch as a Dependency

### Step 1 — Add the API jar to your project

Copy `dupewatch-api-1.3.4.jar` into your project's `libs/` folder (create the folder if it doesn't exist).

**Maven:**

```xml
<dependency>
    <groupId>dev.gravemc</groupId>
    <artifactId>dupewatch</artifactId>
    <version>1.3.4</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/libs/dupewatch-api-1.3.4.jar</systemPath>
</dependency>
```

**Gradle:**

```groovy
dependencies {
    compileOnly files('libs/dupewatch-api-1.3.4.jar')
}
```

> **Why `compileOnly`/`provided`?** DupeWatch is already on the server — you don't need to include it in your jar. Compile against it, but don't bundle it.

---

### Step 2 — Declare DupeWatch in your plugin.yml

**If your plugin REQUIRES DupeWatch** (won't work without it):

```yaml
name: MyPlugin
main: com.example.MyPlugin
version: 1.0.0
depend:
  - DupeWatch
```

**If DupeWatch is OPTIONAL** (your plugin works without it, just with fewer features):

```yaml
name: MyPlugin
main: com.example.MyPlugin
version: 1.0.0
softdepend:
  - DupeWatch
```

---

### Step 3 — Check if DupeWatch is available (soft-depend only)

If you used `softdepend`, always check before using the API:

```java
if (DupeWatchAPI.isAvailable()) {
    DupeWatchAPI api = DupeWatchAPI.get();
    // use the API here
}
```

If you used `depend`, you can call `DupeWatchAPI.get()` directly — DupeWatch is guaranteed to be loaded first.

---

## 2. Getting the API Instance

Everything starts with `DupeWatchAPI.get()`. From there you access the sub-APIs:

```java
DupeWatchAPI api = DupeWatchAPI.get();

api.cases()      // Investigation cases
api.risk()       // Player risk scores
api.scanning()   // Trigger scans
api.quarantine() // Quarantined items
api.watchlist()  // Watchlist rules
api.moneyWatch() // Economy anomalies
api.stats()      // Server-wide stats
```

Call `DupeWatchAPI.get()` anywhere after your plugin's `onEnable()` runs — never call it in a static initializer or field declaration.

---

## 3. Events — Reacting to DupeWatch Detections

Events are the most powerful way to integrate with DupeWatch. Register them in your plugin exactly like any Bukkit event.

### Available Events

| Event | When it fires | Cancellable? |
|-------|--------------|--------------|
| `DWPlayerFlaggedEvent` | A player's inventory triggered a watch rule | **Yes** |
| `DWCaseCreatedEvent` | A new investigation case was saved | No |
| `DWCaseResolvedEvent` | A case was marked as resolved | No |
| `DWScanCompleteEvent` | Any scan finished (triggered or not) | No |
| `DWItemQuarantinedEvent` | Items were quarantined from a player | No |
| `DWItemReturnedEvent` | A quarantined item was returned to a player | No |

---

### DWPlayerFlaggedEvent — The Most Important Event

This fires **before** DupeWatch creates a case or sends any alerts. Cancelling it completely suppresses DupeWatch's response — no case, no Discord message, no staff alert.

```java
import dev.gravemc.dupewatch.api.event.DWPlayerFlaggedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MyListener implements Listener {

    // Suppress DupeWatch for trusted staff members
    @EventHandler
    public void onPlayerFlagged(DWPlayerFlaggedEvent event) {
        if (event.getPlayer().hasPermission("myserver.trusted")) {
            event.setCancelled(true); // DupeWatch does nothing for this player
            return;
        }

        // Add your own logic for high-severity detections
        if (event.getRule().getSeverity().equals("high")) {
            event.getPlayer().kick(
                net.kyori.adventure.text.Component.text("You have been flagged for suspicious activity.")
            );
        }
    }
}
```

**What you can read from this event:**

```java
event.getPlayer()            // The flagged player (Bukkit Player object)
event.getRule().getLabel()   // The rule name, e.g. "Diamond Sword Dupe"
event.getRule().getSeverity() // "low", "med", or "high"
event.getItemCount()         // How many matching items were found
event.isCancelled()          // Whether it's already been cancelled by another plugin
```

---

### DWCaseCreatedEvent — React When a Case Opens

```java
@EventHandler
public void onCaseCreated(DWCaseCreatedEvent event) {
    DWCase c = event.getCase();

    // Example: announce to all staff in a custom channel
    String msg = "§c[DupeWatch] New case for §f" + c.getPlayerName()
                + " §c— Rule: §f" + c.getRuleLabel()
                + " §c(ID: §f" + c.getCaseId() + "§c)";

    Bukkit.getOnlinePlayers().stream()
        .filter(p -> p.hasPermission("myserver.staff"))
        .forEach(p -> p.sendMessage(msg));
}
```

---

### DWCaseResolvedEvent — React When a Case Closes

```java
@EventHandler
public void onCaseResolved(DWCaseResolvedEvent event) {
    getLogger().info("Case " + event.getCase().getCaseId()
        + " resolved by " + event.getResolvedBy());
}
```

---

### DWScanCompleteEvent — Every Scan, Triggered or Not

```java
@EventHandler
public void onScanComplete(DWScanCompleteEvent event) {
    DWScanResult result = event.getResult();

    if (result.isTriggered()) {
        getLogger().info("Scan triggered for " + result.getPlayerName()
            + " — rules: " + result.getFlaggedRuleLabels());
    }

    // Check if a staff member manually triggered it
    if (event.isManualScan()) {
        event.getScanner().sendMessage("§aScan complete. Flagged: " + result.isTriggered());
    }
}
```

---

### Registering Your Listener

In your plugin's `onEnable()`:

```java
@Override
public void onEnable() {
    getServer().getPluginManager().registerEvents(new MyListener(), this);
}
```

---

## 4. Cases — Reading and Managing Investigation Cases

```java
CaseAPI cases = DupeWatchAPI.get().cases();
```

### Read active cases

```java
List<DWCase> active = cases.getActiveCases();
for (DWCase c : active) {
    getLogger().info(c.getPlayerName() + " — " + c.getRuleLabel() + " — " + c.getSeverity());
}
```

### Get all cases for a specific player

```java
List<DWCase> playerCases = cases.getCasesForPlayer(player.getUniqueId());
if (!playerCases.isEmpty()) {
    player.sendMessage("§cYou have " + playerCases.size() + " DupeWatch case(s) on record.");
}
```

### Look up a case by ID

```java
Optional<DWCase> found = cases.getCase("01HX_CASE_ID_HERE");
found.ifPresent(c -> {
    getLogger().info("Found case: " + c.getCaseId() + " — resolved: " + c.isResolved());
});
```

### Resolve a case

```java
boolean resolved = cases.resolveCase("01HX_CASE_ID", "AdminName", "Reviewed — false positive");
if (resolved) {
    sender.sendMessage("§aCase resolved.");
}
```

### Add a note to a case

```java
cases.addNote("01HX_CASE_ID", "[STAFF] Investigated in-game, items destroyed.");
```

### Delete a case permanently

```java
// Warning: irreversible. Prefer resolving instead.
cases.deleteCase("01HX_CASE_ID");
```

### DWCase fields

| Method | Returns | Description |
|--------|---------|-------------|
| `getCaseId()` | `String` | Unique case ID (ULID format) |
| `getPlayerUuid()` | `UUID` | Flagged player's UUID |
| `getPlayerName()` | `String` | Flagged player's name |
| `getRuleLabel()` | `String` | Rule that triggered the case |
| `getSeverity()` | `String` | `"low"`, `"med"`, or `"high"` |
| `isResolved()` | `boolean` | Whether the case is closed |
| `getResolvedBy()` | `String` | Staff member who closed it |
| `getNotes()` | `List<String>` | Staff notes on the case |
| `getItemCount()` | `int` | Number of flagged items |
| `getCreationTime()` | `Instant` | When the case was opened |

---

## 5. Player Risk — Checking How Suspicious a Player Is

```java
PlayerRiskAPI risk = DupeWatchAPI.get().risk();
```

Risk scores are assigned by DupeWatch's ML system. Each detection adds to the score; scores decay over time.

### Risk levels

| Level | Score Range | Color |
|-------|------------|-------|
| `MINIMAL` | 0.0 – 0.5 | Gray |
| `LOW` | 0.5 – 1.5 | Green |
| `MODERATE` | 1.5 – 3.0 | Yellow |
| `HIGH` | 3.0 – 5.0 | Red |
| `EXTREME` | 5.0+ | Bold Red |

### Check if a player is currently flagged (fast, synchronous)

```java
if (risk.isPlayerFlagged(player.getUniqueId())) {
    player.sendMessage("§cYou have an open DupeWatch investigation.");
}
```

### Get risk level (async)

```java
risk.getRiskLevel(player.getUniqueId()).thenAccept(level -> {
    // This runs asynchronously — don't touch Bukkit API here
    // unless you schedule back to the main thread
    getLogger().info(player.getName() + " risk: " + level.getDisplayName());

    if (level == DWRiskLevel.HIGH || level == DWRiskLevel.EXTREME) {
        // Block shop access, restrict trading, etc.
        Bukkit.getScheduler().runTask(this, () -> {
            player.sendMessage("§cTrade access restricted due to your risk level.");
        });
    }
});
```

### Get numeric risk score

```java
risk.getRiskScore(player.getUniqueId()).thenAccept(score -> {
    getLogger().info(player.getName() + " risk score: " + score);
});
```

### Get a pre-formatted risk string (ready for chat)

```java
risk.getFormattedRiskScore(player.getUniqueId()).thenAccept(formatted -> {
    // formatted looks like: "§e2.3 (Moderate)" or "§c§l5.8 (Extreme)"
    sender.sendMessage("Risk score: " + formatted);
});
```

> **Important:** Methods that return `CompletableFuture` run asynchronously. If you need to interact with Bukkit (send messages, cancel events, etc.) inside the callback, schedule it back on the main thread using `Bukkit.getScheduler().runTask(plugin, runnable)`.

---

## 6. Scanning — Triggering Your Own Scans

```java
ScanAPI scan = DupeWatchAPI.get().scanning();
```

### Trigger a scan on a player

```java
scan.scanPlayer(player.getUniqueId()).thenAccept(result -> {
    if (result.isTriggered()) {
        getLogger().info("Scan flagged " + result.getPlayerName()
            + " for: " + result.getFlaggedRuleLabels());
    } else {
        getLogger().info("Scan clean for " + result.getPlayerName());
    }
});
```

The scan respects all DupeWatch rate limits and cooldowns set in `config.yml`. If the player is offline or scanning is disabled, the future completes with a non-triggered result immediately.

### Check if scanning is currently enabled

```java
if (!scan.isScanningEnabled()) {
    sender.sendMessage("§cDupeWatch scanning is currently disabled.");
    return;
}
```

### DWScanResult fields

| Method | Returns | Description |
|--------|---------|-------------|
| `isTriggered()` | `boolean` | Whether any rule was triggered |
| `getFlaggedRuleLabels()` | `List<String>` | Names of triggered rules |
| `getFlaggedFingerprints()` | `List<String>` | Item fingerprints that matched |
| `getTotalFlaggedItems()` | `int` | Total matching items found |
| `getPlayerName()` | `String` | The scanned player's name |
| `getScanTimestamp()` | `long` | When the scan ran (epoch ms) |

---

## 7. Quarantine — Managing Confiscated Items

```java
QuarantineAPI quarantine = DupeWatchAPI.get().quarantine();
```

Quarantine is only available when `confiscation.mode: QUARANTINE` is set in DupeWatch's `config.yml`.

### Check if quarantine is enabled

```java
if (!quarantine.isAvailable()) {
    sender.sendMessage("§cQuarantine is not enabled on this server.");
    return;
}
```

### Get quarantined items for a player

```java
quarantine.getQuarantinedItems(player.getUniqueId()).thenAccept(items -> {
    if (items.isEmpty()) {
        sender.sendMessage("§aNo quarantined items for this player.");
    } else {
        sender.sendMessage("§c" + items.size() + " item(s) in quarantine:");
        for (DWQuarantinedItem item : items) {
            sender.sendMessage("  §7[" + item.getQuarantineId() + "] §f" + item.getReason());
        }
    }
});
```

### Return an item to a player

```java
quarantine.returnItem("QUARANTINE_ID", "AdminName").thenAccept(success -> {
    if (success) {
        sender.sendMessage("§aItem returned.");
    }
});
```

If the player is offline, the item is queued and delivered automatically on their next login.

### Return all items for a player

```java
quarantine.returnAllForPlayer(player.getUniqueId(), "AdminName").thenAccept(count -> {
    sender.sendMessage("§aReturned " + count + " item(s).");
});
```

### Delete a quarantined item (permanent)

```java
// Warning: irreversible.
quarantine.deleteItem("QUARANTINE_ID", "AdminName");
```

### DWQuarantinedItem fields

| Method | Returns | Description |
|--------|---------|-------------|
| `getQuarantineId()` | `String` | Unique quarantine ID |
| `getPlayerName()` | `String` | Player the item was taken from |
| `getReason()` | `String` | Why it was quarantined |
| `getStatus()` | `String` | `"QUARANTINED"`, `"RETURNED"`, or `"DELETED"` |
| `isActive()` | `boolean` | `true` if still quarantined |
| `isPendingReturn()` | `boolean` | Queued for offline player delivery |
| `getHandledBy()` | `String` | Staff member who handled it |

---

## 8. Watchlist — Reading and Modifying Rules

```java
WatchlistAPI watchlist = DupeWatchAPI.get().watchlist();
```

### List all rules

```java
for (DWRule rule : watchlist.getRules()) {
    getLogger().info(rule.getLabel() + " — threshold: " + rule.getThresholdCount()
        + " — severity: " + rule.getSeverity());
}
```

### Look up a rule by fingerprint

```java
Optional<DWRule> rule = watchlist.getRule("abc123fingerprint");
rule.ifPresent(r -> {
    getLogger().info("Rule found: " + r.getLabel());
    getLogger().info("Actions: " + r.getActions());
});
```

### Add a new rule

```java
DWRule newRule = new DWRule.Builder()
    .fingerprint("unique_fingerprint_here")
    .label("My Custom Rule")
    .thresholdCount(5)
    .windowMinutes(60)
    .severity("high")
    .actions(List.of("ALERT_STAFF", "LOG_CASE", "DISCORD_WEBHOOK"))
    .build();

boolean added = watchlist.addRule(newRule);
```

### Remove a rule

```java
watchlist.removeRule("unique_fingerprint_here");
```

### DWRule fields

| Method | Returns | Description |
|--------|---------|-------------|
| `getLabel()` | `String` | Human-readable rule name |
| `getFingerprint()` | `String` | Item fingerprint (unique key) |
| `getThresholdCount()` | `int` | Items needed to trigger |
| `getWindowMinutes()` | `int` | Time window for threshold |
| `getSeverity()` | `String` | `"low"`, `"med"`, `"high"` |
| `getScope()` | `String` | Where to scan (`"inventory"`, `"inventory+ender"`, `"everywhere"`) |
| `getActions()` | `List<String>` | What happens when triggered |
| `hasAction(String)` | `boolean` | Check for a specific action |

---

## 9. Money Watch — Economy Anomalies

```java
MoneyWatchAPI mw = DupeWatchAPI.get().moneyWatch();
```

Money Watch requires Vault to be installed and `moneyWatch.enabled: true` in `config.yml`.

### Check if Money Watch is running

```java
if (!mw.isEnabled()) {
    getLogger().info("Money Watch is not active.");
    return;
}
```

### Check if a player has recent economy anomalies

```java
mw.hasRecentAnomalies(player.getUniqueId()).thenAccept(flagged -> {
    if (flagged) {
        Bukkit.getScheduler().runTask(this, () -> {
            player.sendMessage("§c[Security] Your economy activity has been flagged.");
        });
    }
});
```

### Get a player's last recorded balance

```java
mw.getLastRecordedBalance(player.getUniqueId()).thenAccept(balance -> {
    if (balance >= 0) {
        getLogger().info(player.getName() + " last balance: " + balance);
    }
});
```

---

## 10. Stats — Server-Wide Metrics

```java
StatsAPI stats = DupeWatchAPI.get().stats();
```

All stats methods are synchronous and fast.

```java
getLogger().info("DupeWatch version: " + stats.getPluginVersion());
getLogger().info("Active cases: "     + stats.getActiveCaseCount());
getLogger().info("Resolved cases: "   + stats.getResolvedCaseCount());
getLogger().info("Total cases: "      + stats.getTotalCaseCount());
getLogger().info("Watchlist rules: "  + stats.getRuleCount());
getLogger().info("In quarantine: "    + stats.getQuarantinedItemCount());
getLogger().info("API ready: "        + stats.isReady());
```

---

## 11. Complete Example Plugin

Here is a minimal but complete plugin that uses events, the risk API, and the cases API together:

```java
package com.example;

import dev.gravemc.dupewatch.api.DupeWatchAPI;
import dev.gravemc.dupewatch.api.event.DWPlayerFlaggedEvent;
import dev.gravemc.dupewatch.api.event.DWCaseCreatedEvent;
import dev.gravemc.dupewatch.api.model.DWRiskLevel;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class MyDupeIntegration extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Check DupeWatch is loaded (if using softdepend)
        if (!DupeWatchAPI.isAvailable()) {
            getLogger().warning("DupeWatch not found — integration disabled.");
            return;
        }

        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("DupeWatch integration enabled. Version: "
            + DupeWatchAPI.get().stats().getPluginVersion());
    }

    // Suppress alerts for players with the "trusted" permission
    @EventHandler
    public void onPlayerFlagged(DWPlayerFlaggedEvent event) {
        if (event.getPlayer().hasPermission("myserver.trusted")) {
            event.setCancelled(true);
            getLogger().info("Suppressed DupeWatch flag for trusted player: "
                + event.getPlayer().getName());
        }
    }

    // When a case opens, notify staff and check the player's risk level
    @EventHandler
    public void onCaseCreated(DWCaseCreatedEvent event) {
        String playerName = event.getCase().getPlayerName();

        // Notify online staff
        Bukkit.getOnlinePlayers().stream()
            .filter(p -> p.hasPermission("myserver.staff"))
            .forEach(p -> p.sendMessage(
                "§c[Alert] New DupeWatch case opened for §f" + playerName
            ));

        // Check risk level async and kick if extreme
        DupeWatchAPI.get().risk()
            .getRiskLevel(event.getCase().getPlayerUuid())
            .thenAccept(level -> {
                if (level == DWRiskLevel.EXTREME) {
                    Bukkit.getScheduler().runTask(this, () -> {
                        var player = Bukkit.getPlayer(event.getCase().getPlayerUuid());
                        if (player != null) {
                            player.kick(net.kyori.adventure.text.Component.text(
                                "Your account has been suspended pending review."
                            ));
                        }
                    });
                }
            });
    }
}
```

---

## 12. Quick Reference

### Getting sub-APIs

```java
DupeWatchAPI api = DupeWatchAPI.get();
CaseAPI      cases     = api.cases();
PlayerRiskAPI risk     = api.risk();
ScanAPI      scan      = api.scanning();
QuarantineAPI quarantine = api.quarantine();
WatchlistAPI  watchlist  = api.watchlist();
MoneyWatchAPI moneyWatch = api.moneyWatch();
StatsAPI      stats      = api.stats();
```

### Sync vs Async at a glance

| Method type | Runs on | Notes |
|-------------|---------|-------|
| `isPlayerFlagged()`, all stats, `isAvailable()`, etc. | Main thread (sync) | Safe to call anywhere |
| Methods returning `CompletableFuture<T>` | Async | Use `Bukkit.getScheduler().runTask()` to touch Bukkit API inside callbacks |

### All events

```java
DWPlayerFlaggedEvent    // Before detection is processed — cancellable
DWCaseCreatedEvent      // After a case is saved
DWCaseResolvedEvent     // After a case is resolved
DWScanCompleteEvent     // After any scan finishes
DWItemQuarantinedEvent  // After items are quarantined
DWItemReturnedEvent     // After items are returned from quarantine
```

### Import paths

```java
import dev.gravemc.dupewatch.api.DupeWatchAPI;
import dev.gravemc.dupewatch.api.CaseAPI;
import dev.gravemc.dupewatch.api.PlayerRiskAPI;
import dev.gravemc.dupewatch.api.ScanAPI;
import dev.gravemc.dupewatch.api.QuarantineAPI;
import dev.gravemc.dupewatch.api.WatchlistAPI;
import dev.gravemc.dupewatch.api.MoneyWatchAPI;
import dev.gravemc.dupewatch.api.StatsAPI;
import dev.gravemc.dupewatch.api.model.DWCase;
import dev.gravemc.dupewatch.api.model.DWRule;
import dev.gravemc.dupewatch.api.model.DWScanResult;
import dev.gravemc.dupewatch.api.model.DWQuarantinedItem;
import dev.gravemc.dupewatch.api.model.DWRiskLevel;
import dev.gravemc.dupewatch.api.event.DWPlayerFlaggedEvent;
import dev.gravemc.dupewatch.api.event.DWCaseCreatedEvent;
import dev.gravemc.dupewatch.api.event.DWCaseResolvedEvent;
import dev.gravemc.dupewatch.api.event.DWScanCompleteEvent;
import dev.gravemc.dupewatch.api.event.DWItemQuarantinedEvent;
import dev.gravemc.dupewatch.api.event.DWItemReturnedEvent;
```
