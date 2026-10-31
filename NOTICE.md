# Anime Launcher - Notice

**NOT AN OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG OR MICROSOFT.**

Minecraft is a trademark of Mojang AB / Microsoft. Anime Launcher does not contain or distribute Minecraft game files. The game is downloaded from Mojang's servers, and you need to own a copy of Minecraft: Java Edition to play it.

## What this is

Anime Launcher is a modified version of [MojoLauncher](https://github.com/MojoLauncher/MojoLauncher), which is itself based on [PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher). The original authors keep their copyright. Thanks to all the original developers and contributors.

## License

Anime Launcher is distributed under the **GNU Lesser General Public License, version 3 (GNU LGPL v3)**. See `LICENSE` for the LGPL v3 text and `COPYING` for the GNU GPL v3 text that it refers to. Both texts are also bundled inside the app (`assets/licenses`).

Third-party components are distributed under their own licenses. The list with links is in `README.md`, section "Third party components, licenses and sources". It includes, among others, OpenJDK (GPLv2 + Classpath Exception), Authlib-Injector (AGPL-3.0), LWJGL (BSD-3), GLFW and SDL (zlib), Mesa (MIT), OpenAL Soft (LGPL) and Android support libraries (Apache 2.0).

## Source code

The complete source code of Anime Launcher is available at https://github.com/aamirplayzz/MojoLauncher. Every release lists the commit it was built from. The native submodules (`glfw`, `sdl`, `mojoexec`, `NoBlocklist`) are pinned in this repository and their sources are published by their upstream projects:

- https://github.com/MojoLauncher/glfw
- https://github.com/MojoLauncher/MojoSDL
- https://github.com/MojoLauncher/mojoexec
- https://github.com/MojoLauncher/NoBlocklist

You may modify and redistribute Anime Launcher under the terms of the LGPL v3.

## Changes compared to MojoLauncher (31 Oct 2026)

- New name (Anime Launcher) and application id `com.animelauncher.app`.
- New launcher icon, logo and artwork, purple anime theme, 3D buttons, new landscape main menu with sidebar, new game loading screen.
- The launcher UI is locked to landscape.
- Different default settings for weaker phones (lower default render resolution, sustained performance on, lighter default `options.txt`).
- Branding text in the app strings was replaced.
- New GitHub Actions workflow that builds the APK and publishes it as a release.

## Privacy

See [PRIVACY_POLICY.md](PRIVACY_POLICY.md).

## Warranty

This software is provided without warranty, as described in the LGPL v3.
