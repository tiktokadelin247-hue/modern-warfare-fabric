# Modern Warfare (prototype) — Minecraft Java 1.20.1 / Fabric

Early source-code prototype for a multiplayer-oriented tactical combat mod.

## Current prototype scope
- Assault rifle and combat pistol items with simple server-side hitscan damage.
- Basic ammo and reload behavior.
- Prone toggle and slide impulse.
- Simple downed-state timer, revive, and finish actions.
- Placeholder items for armor plates and a medkit.

## Important limitations
- This is source code, not a compiled `.jar`.
- Renaming the ZIP file to `.jar` will not make it a Minecraft mod.
- Armor plate and medkit are placeholder items; full armor mechanics, animations, weapon models, and polished downed/revive interactions are not implemented yet.
- Build and test with the included GitHub Actions workflow. After a successful build, test in a separate Minecraft instance before using it in a main world.
- Multiplayer requires the same mod version and Fabric API on the server and all clients.

See `BUILDING.md` for the easiest browser-based build instructions.
