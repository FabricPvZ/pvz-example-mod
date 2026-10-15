# pvz-example-mod

The example mod (a standalone Gradle project). It demonstrates the two halves of the capabilities available to mod authors:

- **Events**: `ExampleMod` implements `net.fabricmc.api.ModInitializer` and listens for `LawnEvents.SUN_COLLECTED` (doubling the sun), `PLANT_PLACED`, `ZOMBIE_DEATH`, `LEVEL_START`;
- **Mixin**: `LawnCommonMixin` (HEAD injection into the static method `modInRange(II)Z`), `BoardMixin` (`Board.update()V` injection + `@Shadow mSunMoney`), `BoardAccessor` (an `@Accessor` generating an accessor interface for `Board`).

Build and publish (as a mod jar containing `fabric.mod.json` and `examplemod.mixins.json`, bundling no dependencies):

```bash
./gradlew build publishToMavenLocal   # publishes cn.enaium.fabricpvz:pvz-example-mod:1.0.0
```

The build file is the toolchain plugin plus the coordinates
(`plugins { id("cn.enaium.fabricpvz.loom") version "1.0.0" }`): the plugin, from the
Gradle Plugin Portal (`https://plugins.gradle.org/plugin/cn.enaium.fabricpvz.loom`, so nothing has to
be published locally first), puts the real Fabric Loader, Mixin, ASM, the API and the game on the
compile class path without this project listing any of them. `./gradlew runClient` boots the game
through the real loader with the built jar in `run/mods/`.

Installing into the game: drop the jar into the game directory's `mods/` (for example `PvZ-Portable/run/mods/`). The game is launched by the real Fabric Loader through `net.fabricmc.loader.impl.launch.knot.KnotClient` together with `cn.enaium.fabricpvz:fabric-provider`, which registers the game as a built-in mod; the loader then discovers the jar automatically at startup.
`PvZ-Portable`'s end-to-end test also pulls this artifact from `mavenLocal`, places it into `build/testmods/`, and verifies the event chain and bytecode injection.

## License

MIT (see `LICENSE`). The game port itself (`PvZ-Portable`) is LGPL-3.0-or-later with the PopCap framework licence.
