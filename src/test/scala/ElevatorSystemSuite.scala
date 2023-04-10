import com.system.elevator._
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.mutable

class ElevatorSystemSuite extends AnyFunSuite {

  test("Elevator running up should handle all up-pickup orders BEFORE current target" +
    "if current floor is smaller") {
    val system = ElevatorSystemImpl(numOfElevators = 1, numOfFloors = 7)
    val targetPickup = PickupOrder(6, ElevatorDirection.Up)

    system.pickup(targetPickup)
    system.step()
    system.pickup(PickupOrder(3, ElevatorDirection.Up))
    system.pickup(PickupOrder(4, ElevatorDirection.Up))

    val result = system.status()
    val expectedOrders = List(PickupOrder(3, ElevatorDirection.Up), PickupOrder(4, ElevatorDirection.Up), targetPickup)
    val expectedResult = mutable.Map(
      (ElevatorId(1), ElevatorStatus(0, isBusy = true, isDoorOpened = false, MovementStatus.Up, expectedOrders))
    )

    assert(result == expectedResult)
  }

  test("Elevator running up should handle all down-pickup orders AFTER current target") {
    val system = ElevatorSystemImpl(numOfElevators = 1, numOfFloors = 7)
    val targetPickup = PickupOrder(6, ElevatorDirection.Up)

    system.pickup(targetPickup)
    system.step()
    system.step()
    system.pickup(PickupOrder(3, ElevatorDirection.Down))
    system.pickup(PickupOrder(4, ElevatorDirection.Down))

    val result = system.status()
    val expectedOrders = List(targetPickup, PickupOrder(4, ElevatorDirection.Down), PickupOrder(3, ElevatorDirection.Down))
    val expectedResult = mutable.Map(
      (ElevatorId(1), ElevatorStatus(1, isBusy = true, isDoorOpened = false, MovementStatus.Up, expectedOrders))
    )

    assert(result == expectedResult)
  }

  test("Elevator running down should handle all down-pickup orders BEFORE current target " +
    "if current floor is larger") {
    val system = ElevatorSystemImpl(numOfElevators = 1, numOfFloors = 7)
    processElevatorToTheHighestFloor(system)
    val targetPickup = PickupOrder(2, ElevatorDirection.Down)

    system.pickup(targetPickup)
    system.step()
    system.step()
    system.pickup(PickupOrder(3, ElevatorDirection.Down))
    system.pickup(PickupOrder(4, ElevatorDirection.Down))

    val result = system.status()
    val expectedOrders = List(PickupOrder(4, ElevatorDirection.Down), PickupOrder(3, ElevatorDirection.Down), targetPickup)
    val expectedResult = mutable.Map(
      (ElevatorId(1), ElevatorStatus(7, isBusy = true, isDoorOpened = false, MovementStatus.Down, expectedOrders))
    )

    assert(result == expectedResult)
  }

  test("Elevator running down should handle all up-pickup orders AFTER current target") {
    val system = ElevatorSystemImpl(numOfElevators = 1, numOfFloors = 7)
    processElevatorToTheHighestFloor(system)
    val targetPickup = PickupOrder(2, ElevatorDirection.Down)

    system.pickup(targetPickup)
    system.step()
    system.step()
    system.pickup(PickupOrder(5, ElevatorDirection.Up))
    system.pickup(PickupOrder(6, ElevatorDirection.Up))

    val result = system.status()
    val expectedOrders = List(targetPickup, PickupOrder(5, ElevatorDirection.Up), PickupOrder(6, ElevatorDirection.Up))
    val expectedResult = mutable.Map(
      (ElevatorId(1), ElevatorStatus(7, isBusy = true, isDoorOpened = false, MovementStatus.Down, expectedOrders))
    )

    assert(result == expectedResult)
  }

  test("Method update should update statuses of elevators in the system") {
    val system = ElevatorSystemImpl(numOfElevators = 1, numOfFloors = 4)
    val orders = List(
      PickupOrder(1, ElevatorDirection.Up),
      PickupOrder(2, ElevatorDirection.Down))

    system.update(ElevatorId(1), orders)
    val result = system.status()
    val expectedResult = mutable.Map(
      (ElevatorId(1), ElevatorStatus(0, isBusy = false, isDoorOpened = false, MovementStatus.Stopped, orders))
    )

    assert(result == expectedResult)
  }


  private def processElevatorToTheHighestFloor(system: ElevatorSystem): Unit = {
    system.pickup(PickupOrder(system.numOfFloors, ElevatorDirection.Down))
    (1 to system.numOfFloors + 1).foreach(_ => system.step())
  }

}
