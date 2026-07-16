# Ninjabowtie Server Utility (NSU)

A Minecraft Fabric client mod that lets you bind keys to execute chat commands instantly.

## Features

- Bind any number of keys to execute chat commands
- In-game config screen via ModMenu (Mods > NSU > Config)
- Works entirely client-side

## Requirements

- Minecraft 26.2+
- Java 25+
- Fabric Loader 0.19+
- Fabric API
- ModMenu (for in-game config)

## Configuration

The config file is located at `.minecraft/config/nsu.json`. It is automatically created after first launch with default binds.

```json
{
  "binds": {
    "[": "/ah",
    "]": "/rtp",
    "\\": "/home"
  }
}
```

You can also configure binds in-game through ModMenu without editing the file directly.

## Building

```sh
./gradlew build
```

The built jar will be in `build/libs/`.

## License

Apache 2.0

## Credits

Icon by [Magnific](https://www.flaticon.com/free-icons/gears) on Flaticon.
