
# Schematic Drag

A client-only Fabric mod for dragging Litematica schematic placements while using
freecam.


https://github.com/user-attachments/assets/29c475d6-74e1-4ae1-b1a3-2c5a7134361a


## Disclaimer

This project was made 100% with AI assistance. I do not know how to make
Minecraft mods; I created it purely as a utility I wanted.

## Requirements

- Minecraft 26.2
- Fabric Loader 0.19.5 or newer
- Fabric API
- Litematica 0.28.8 or newer
- MaLiLib

Tweakeroo is optional. The mod detects a detached camera and does not require
Tweakeroo specifically.

## Usage

1. Start freecam with a schematic placement loaded.
2. Point at the placement and hold the left mouse button.
3. Move the camera to move the placement.
4. Scroll while holding to change its distance.
5. Release the left mouse button to stop dragging.

Dragging is only active with no screen open and only for enabled, unlocked
placements. Without freecam, normal Minecraft and Litematica behavior is
unchanged.

## Building

```bash
./gradlew build
```

The built mod is written to `build/libs/`.



