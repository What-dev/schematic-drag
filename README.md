
# Schematic Drag
[![CurseForge](https://img.shields.io/badge/CurseForge-Schematic_Drag-orange?logo=curseforge)](https://www.curseforge.com/minecraft/mc-mods/schematic-drag)
[![CurseForge Downloads](https://img.shields.io/curseforge/dt/1689153?logo=curseforge)](https://www.curseforge.com/minecraft/mc-mods/schematic-drag)



A client-only Fabric mod for dragging Litematica schematic placements while using
freecam.

<img src="showcase.gif" alt="Project Showcase" width="600">

## Disclaimer

This project was made primarily with the assistance of generative AI. I do not know how to make Minecraft mods; I created it purely as a utility I wanted. I am slowly working towards adding more features myself, but large drops will have some AI contributions (and will be marked accordingly).

## Requirements

- Minecraft 26.2
- Fabric Loader 0.19.5 or newer
  - Fabric API
- Litematica 0.28.8 or newer
  - MaLiLib

The mod (hopefully) detects any sort of freecam mod where the camera would be detached from the player. I have only personally tested with tweakaroos freecam, but any other should do the trick.

## Usage

1. Start freecam with a schematic placement loaded.
2. Point at the placement and hold the left mouse button.
3. Move the camera to move the placement.
4. Scroll while holding to change its distance.
5. Release the left mouse button to stop dragging.

Dragging is only active when the camera is in freecam mode and when there is no menu open.

Schematic-drag is also only enabled for unlocked
placements. Without meeting these conditions, normal Minecraft and Litematica behavior is
unchanged.

## Building

```bash
./gradlew build
```

The built mod is written to `build/libs/`.



