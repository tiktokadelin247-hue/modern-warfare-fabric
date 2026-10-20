# Build Modern Warfare Fabric 1.20.1

This is the SOURCE project, not a compiled mod JAR. Do not rename this ZIP to `.jar`.

## Easiest route: GitHub Actions in a browser
1. Create a GitHub repository.
2. Upload the CONTENTS of this folder to the repository root. `build.gradle`, `settings.gradle`, `gradle.properties`, `src/`, and `.github/` must be directly at the repository root (not inside another nested `modern_warfare_fabric/` folder).
3. Open the repository's Actions tab and run `Build Modern Warfare Fabric Mod` (or push a commit to main/master).
4. Open the completed workflow run. If it is green, download artifact `modern-warfare-fabric-1.20.1` from the Artifacts section. That download is a ZIP containing the compiled JAR.
5. Extract that artifact ZIP and copy the `.jar` into `%appdata%\.minecraft\mods`.
6. Install Fabric Loader for Minecraft 1.20.1 and Fabric API 0.92.2+1.20.1. For multiplayer, install the mod and Fabric API on the server and every client.

If Actions fails, open the workflow run and read the first red step. A failed build means no working JAR was produced.

## Local route
With Java 17 and Gradle 8.6 installed, run `gradle clean build` from this folder. The output should be in `build/libs/`.

This project has not yet been compiled or tested in Minecraft in the preparation environment. Treat it as an early prototype, not a verified release.
