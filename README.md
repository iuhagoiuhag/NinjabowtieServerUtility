# Ninjabowtie Server Utility (NSU)

A Minecraft Fabric mod that lets you bind keys to execute chat commands instantly.

## Features

- Bind up to 3 keys to execute any chat command
- Configure keys and commands in the config file
- Works entirely client-side

## Configuration

The config file is located at `.minecraft/config/nsu.json`. It is automatically created after first launch.

```json
{
  "key1": "[",
  "command1": "/ah",
  "key2": "]",
  "command2": "/rtp",
  "key3": "\\",
  "command3": "/home"
}
```

## Building

```sh
./gradlew build
```

The built jar will be in `build/libs/`.

## License

Apache 2.0

## Credits

Icon by [Magnific](https://www.flaticon.com/free-icons/gears) on Flaticon.
