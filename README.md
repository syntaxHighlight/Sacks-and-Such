# Sacks 'N Such — NeoForge 1.21.1 Port

> This is an unofficial Minecraft 1.21.1 port of
> [Traister101/Sacks-and-Such](https://github.com/Traister101/Sacks-and-Such).
> Traister101 is the original author; this fork is independently maintained and is not an
> official release by the original project.

Sacks 'N Such is a TerraFirmaCraft addon which primarily adds sacks and similar held
containers for item storage. This branch targets Minecraft 1.21.1, NeoForge 21.1, Java 21,
and TerraFirmaCraft 4.2.4 or newer.

All containers are very configurable allowing you to change things such as Slot Count,
Slot Capacity, Automatic Item Pickup, the Maximum allowed TFC Item Size and more. These
containers are inspired by mods like Dank Null by p455w0rd in that their slots can exceed
the usual stack size limit. Unlike Dank Null you cannot directly place or interact with
items from within the container however Pick Block is supported. Many containers allow you
to insert and extract items from the container while it's in a slot (Like how vanilla
bundles and TFC Vessels work).

This mod also adds some other miscellaneous items such as the Hiking Boots or Horseshoes
providing QOL and increasing movement speed.

When curios is installed many of our containers can be worn and even display in world on
your player.

## Building

Keep an `ExtendedSlotCapacity` checkout next to this repository, then run:

```shell
./gradlew build
```

The release JAR is written to `build/libs/` and embeds Extended Slot Capacity with `jarJar`.

## License and credits

The project is distributed under the MIT License. Preserve the included `LICENSE` when
redistributing it. See `THIRD_PARTY_NOTICES.md` for separately licensed material included by
the 1.21.1 port.
