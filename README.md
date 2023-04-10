## About the system
System manages and controls elevators in the building.\
It is a console app which enables to simulate steps and pickups of elevators.

After start, you have to input number of floors and elevators.\
There is no negative floors.\
After initialisation of the system, all elevators are on zero floor, stopped, not busy, without orders and with closed doors.

In order to optimize work of elevators, they are running max up and max down.\
For example:
- *If elevator is running up to 7th floor, it won't stop on 5th floor, if direction of pickup order is down.\
But it will, if is up.* 
- *If elevator is running down from 7th floor, it won't stop on 5th floor if direction of pickup order is up.\
But it will if is down.*
- *When elevator is on 5th floor running down and at one moment there are pickups: (3, up), (4, down), (2,down), (6,up) 
 it will result in orders: (4,down), (2, down) //max down (3,up), (6, up) //max up*\

In order to choose the best elevator to serve a floor, for each of them system calculates a score. The one with the lowest will be chosen.\
For better understanding of this algorithm, unit tests were written.


System enables to update only orders in elevators statuses.

-----

## How to run

    /gradlew. run

Follow instructions on the console - enter floors and elevators numbers in the building.\
Then instruction of elevator system simulation will appear on the screen.
