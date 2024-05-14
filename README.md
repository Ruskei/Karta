# Karta

-----
Base library for rendering a screen to a player.

*Currently only supports 1.20.4. Also, this project is heavily WIP and heavy refactoring is to be done.*
*Note: Due to inherent inferiority of this method of rendering in comparison to font gui rendering, progress should not be expected.*

### Functionality

- Draws screens of variable size to any player.
- Overridable implementation for management of player location while viewing screen (subject to change).
- Component driven UI.
- Interprets mouse movement, left clicks from player.

### TODO

- Add right click, scrolling, and hotbar hotkey detection.
- Cache component renders to improve performance.
- Add vanilla shader to provide proper on-screen rendering, inspired by JNNGL, heavily expanding the scope of this plugin.
