# JChess

An application to play chess against a friend or the JChess AI - implemented with a min-max algorithm with alpha-beta pruning and quiescence searching, rated at over 1900 ELO. You can also load positions from FEN or PGN and test the strength of the Engine!

<img src="./Chess/resources/screenshots/JChess_TheOperaGame.png?raw=true" height="400">

## Usage

Manual for JChess

### Game Setup

Select game mode 

```
Menu->Options->Setup Game
Select White Player Type
Select Black Player Type
Select Search Depth (recommended 4)
```

Toggle Opening Move Database

```
Menu->Options
Use Book Moves
```

### New Game

```
Menu->Options->New Game
```

### Load Game

Load a game from PGN

```
Menu->File->Load PGN File
Select <file>.pgn
```

Load a FEN position

```
Menu->File->Load from FEN
Enter FEN (ex. "BK6/6P1/3N3p/1p2Pp2/4p1r1/4R2P/p1P5/1k2n3 b")
```

### Save Game

Save the PGN file of a game

```
Menu->File->Save PGN File
Select location
```

### Change Board/Piece Layout

Flip the board

```
Menu->Preferences->Flip Board
```

Show/Disable Legal Moves

```
Menu->Preferences->Highlight Legal Moves
```

Change Piece Set

```
Menu->Preferences->Choose Piece Set
```

Change Board Colors

```
Menu->Preferences->Choose Board Colors
```

## Opening Move Database

The opening move database (Opening Book) contains over 2000 openings, persisted as a tree data structure - each move coresponding to a node. Each node has a weight coresponding to the amount of times the opening has been played in Master Games, indicating the lines strength. The AI selects a line from a randomizing function which depends on the players moves, the weight, and the depth (strength) setting. The opening book structure is persisted in ./resources/structures/opening_book.txt

### Add Openings

Write new openings to a .cvs file in the following format

```
A04    Reti: King's Indian Attack
1.Nf3 g6 2.g3 Bg7 3.Bg2 e5 4.O-O Nc6 5.e4 d6 6.d3 Nge7 7.c3 O-O 8.Nbd2 h6 9.b4 1/2
```

Then run OpeningBookParser.java with the file as an argument


## Authors

* **Arian Omidi** - https://github.com/ArianOmidi


## Acknowledgments

* Thanks to https://github.com/bhlangonijr for the initial chess library and https://github.com/amir650 for the Java Swing GUI tutorial
* Inspiration for Board and Piece design from Lichess.org
