victory dispatcher
======

Victory Dispatcher

#RUNNING INSTRUCTIONS

There is executable file called "run" in the project's root directory.
This will automatically compile and run the program in an unix like environment.
This project requires:

1. The Java 7 JDK including the virtual machine.

2. Bash.

3. Unix environment.

#Notice

Please read before contributing.

#Design Decisions

##Game Loop

The `VD` object extends a `JFrame` and is responsible for the game loop.
We follow a common convention of the loop being devided between updating game logic and drawing.
This way we can control the frame rate independently of the logic.
Hence, they are on different threads.
All drawing is done with the graphics context which normally has the variable name `g`.
The game is updated by taking in the number of milliseconds (dt) that have elapsed since the last update.
The `VD` object updates and draws the `Room` object.
The `Room` object updates and draws everything else.

##GameObject Inheritance

**Everything** in the `Room` extends `GameObject`.
The `GameObject` has all the properties needed for an object to be:
1. updated
2. drawn
3. collision detected
If you only need an object to be drawn but not updated, don't overload the `update` method.
The same principle applies any functionality you don't need for an object in the `Room`.

##Media

All in game artwork is currently contained in a spritemap located at `./media/<spritemap>.png`.
Use `drawSprite(Graphics2D g,int size, int i, int j, int offsetX, int offsetY)`.
`i` and `j` are for which tile.
The offset is used to move the position of the graphic in relation to the object's bounding box.

#TODO

