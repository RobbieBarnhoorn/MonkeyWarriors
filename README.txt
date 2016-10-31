Monkey Warriors
---------------------
Robbie Barnhoorn
BRNROB026
brnrob026@myuct.ac.za
---------------------


---------------------
Running
---------------------

Hello. I hope you enjoy my game.

To run it:
./gradlew desktop:run

To create a jar file:
./gradlew desktop:dist

To run the jar file:
java -jar desktop/build/libs/desktop-1.0.jar

Additionally, I will attach a "standalone" jar file 
in case anyone has trouble with gradle. To run it:

java -jar desktop-1.0.jar

---------------------
Controls
---------------------

W,A,S,D OR UP,LEFT,DOWN,RIGHT-arrow keys to move
(Did I mention you can double-jump?)
ESC to escape
Mouse to navigate the menu

---------------------
About
---------------------

Monkey Warriors is a 2D stealth-and-evasion platformer.

You travel through a temple, sneaking past bandits that have guns and trying
not to be bitten by deadly bats. You do not have a weapon, and are incredibly
fragile. I wanted this game to be more about the stealth than the combat. 
However, you are able to kill enemies by using them against eachother.
Bandits can shoot bats, bats can kill bandits, and bandits can shoot
eachother; if you position yourself properly that is.

There are three levels for you to play. I want you to have fun on all three,
so if you get stuck on any level, please feel free to change the value in
core/assets/levels/currentLevel.txt to whichever level you want to try (1, 2,
or 3), and then in game click "Resume" in the Main menu to "Resume" from this level.
I do recommend trying to finish them though.

There are 2 types of enemies:
Bats and Bandits. They both respond to you coming within their field of
vision. Bats will attempt to bite you by flying towards you. Their movement is
unpredictable, and at different times will give off the impression of
intelligence, or the lack thereof. Bandits will shoot at you. 
Their bullets are relatively slow, so you have the chance to
evade their attacks. They also take some time to whip out their gun upon
seeing you.

Some features of the game:
    A sweet menu
    Some nice sounds to play to
    Some pretty cool art and animations
    (IMO) 3 fun levels/stages
    2 unique types of enemies
    The best bat AI you will ever see
    60FPS
    Resuming where you left off
