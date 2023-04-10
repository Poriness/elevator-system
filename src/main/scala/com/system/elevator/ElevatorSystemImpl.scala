package com.system.elevator

import scala.collection.mutable

class ElevatorSystemImpl(
                          private val numOfElevators: Int,
                          private val numOfFloor: Int
                        ) extends ElevatorSystem {

  private var stepCounter: Int = 0
  private val elevatorsById: mutable.Map[ElevatorId, ElevatorStatus] = {
    val elevators = (1 to numOfElevators).map(i => ElevatorId(i))
    val elevatorStatusesById: mutable.Map[ElevatorId, ElevatorStatus] = {
      val map = mutable.Map[ElevatorId, ElevatorStatus]()
      elevators.foreach {
        i =>
          map.put(i, ElevatorStatus(
            currentFloor = 0,
            isBusy = false,
            isDoorOpened = false,
            movementStatus = MovementStatus.Stopped,
            orders = List.empty))
      }
      map
    }
    elevatorStatusesById
  }

  override def numOfElevator: Int = numOfElevators
  override def numOfFloors: Int = numOfFloor

  override def step(): Unit = {
    elevatorsById.foreach {
      case (id, status) => elevatorsById.put(id, status.progressOneStep())
    }
    printStatusAfterStep()
    stepCounter += 1

  }

  override def pickup(pickupOrder: PickupOrder): Unit = {
    def findElevatorWithTheBestScore(): Unit = {
      val servingElevator = elevatorsById.map {
        case (id, status) => (id, status.scoreStepsToFloorPickup(pickupOrder))
      }.minBy(_._2)._1
      elevatorsById.put(servingElevator, elevatorsById(servingElevator).serveTargetFloor(pickupOrder))
    }

    if (!checkIfPickupOrderValid(pickupOrder)) println(" - cannot pickup elevator with this parameters")
    else findElevatorWithTheBestScore()
  }

  override def update(elevatorId: ElevatorId, orders: List[PickupOrder]): Unit = {
    if (orders.map(checkIfPickupOrderValid).exists(_.==(false))) println()
    else elevatorsById.put(elevatorId, elevatorsById(elevatorId).updateOrders(orders))
  }

  override def status(): mutable.Map[ElevatorId, ElevatorStatus] = elevatorsById

  def checkIfPickupOrderValid(pickup: PickupOrder): Boolean = {
    def checkIfOutOfScope(): Boolean = pickup.numOfFloor > numOfFloor
    def checkIfDirectionMakesSens(): Boolean =
      (pickup.numOfFloor == numOfFloor && pickup.direction == ElevatorDirection.Up) ||
        (pickup.numOfFloor == 0 && pickup.direction == ElevatorDirection.Down)

    if (checkIfOutOfScope()) {
      println("Input floor is out of scope!")
      false
    } else if (checkIfDirectionMakesSens()) {
      println("Moving direction doesn't make sense!")
      false
    } else {
      true
    }
  }

  def printStatusAfterStep(): Unit = {
    elevatorsById.foreach {
      case (id, status) =>
        println(s"$id, floor: ${status.currentFloor}, " +
          s"isBusy: ${status.isBusy}, isDoorOpened: ${status.isDoorOpened}, status: ${status.movementStatus}" +
          s"\n   targets: ${status.orders} ")
    }
    println("------------------")
  }

}

object ElevatorSystemImpl {

  def apply(numOfElevators: Int, numOfFloors: Int): ElevatorSystemImpl = {
    assert(numOfElevators > 0, s"System cannot be created - negative number of elevators: $numOfElevators")
    assert(numOfFloors > 0, s"System cannot be created - negative number of floors: $numOfFloors")

    new ElevatorSystemImpl(numOfElevators, numOfFloors)
  }
}
