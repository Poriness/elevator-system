package com.system.elevator

import ElevatorStatus.toServeBefore


case class ElevatorStatus(
                           currentFloor: Int,
                           isBusy: Boolean,
                           isDoorOpened: Boolean,
                           movementStatus: MovementStatus,
                           orders: List[PickupOrder]
                         ) {

  def onTarget: Boolean =
    if (orders.nonEmpty) {
      currentFloor == orders.head.numOfFloor
    } else {
      false
    }

  def updateOrders(newOrders: List[PickupOrder]): ElevatorStatus = {
    this.copy(orders = newOrders)
  }

  def serveTargetFloor(pickupOrder: PickupOrder): ElevatorStatus = {
    def includeOrder(pickupOrder: PickupOrder, orders: List[PickupOrder]): List[PickupOrder] = {
      orders match {
        case Nil => List(pickupOrder)
        case head :: tail if toServeBefore(pickupOrder, head, currentFloor) =>
          pickupOrder :: head :: tail
        case head :: tail if !toServeBefore(pickupOrder, head, currentFloor) =>
          head :: includeOrder(pickupOrder, tail)
      }
    }

    this.copy(orders = includeOrder(pickupOrder, orders))
  }

  def scoreStepsToFloorPickup(pickupOrder: PickupOrder): Int = {
    def _scoreStepsToFloorPickup(pickupOrder: PickupOrder, currentFloor: Int, orders: List[PickupOrder]): Int = {
      orders match {
        //elevator is free
        case Nil => Math.abs(currentFloor - pickupOrder.numOfFloor) + 1
        //serve first
        case head :: tail if ElevatorStatus.toServeBefore(pickupOrder, head, currentFloor) =>
          _scoreStepsToFloorPickup(pickupOrder, currentFloor, Nil) + _scoreStepsToFloorPickup(head, pickupOrder.numOfFloor, tail)
        //serve after
        case head :: tail if !ElevatorStatus.toServeBefore(pickupOrder, head, currentFloor) =>
          _scoreStepsToFloorPickup(head, currentFloor, Nil) + _scoreStepsToFloorPickup(pickupOrder, head.numOfFloor, tail)
      }
    }
    _scoreStepsToFloorPickup(pickupOrder, currentFloor, orders)
  }


  def progressOneStep(): ElevatorStatus = {
    this match {
      case status if isNotBusyAndHaveOrders(status) =>
        val direction = getDirectionOfMoving(status)
        status.copy(isBusy = true, movementStatus = direction)
      case status if isRunningToTarget(status) =>
        val currentFloor = status.currentFloor + getValueToAdd(status.movementStatus)
        status.copy(currentFloor = currentFloor)
      case status if isOnTargetAndClosed(status) =>
        status.copy(movementStatus = MovementStatus.Stopped, orders = status.orders.tail, isDoorOpened = true)
      case status if isOpenedAndHaveOrders(status) =>
        val direction = getDirectionOfMoving(status)
        status.copy(movementStatus = direction, isDoorOpened = false)
      case status if isOpenedAndNoOrders(status) =>
        status.copy(movementStatus = MovementStatus.Stopped, isBusy = false, isDoorOpened = false)
      case _ => this
    }
  }

  def isNotBusyAndHaveOrders(status: ElevatorStatus): Boolean =
    !status.isBusy && status.orders.nonEmpty

  def isRunningToTarget(status: ElevatorStatus): Boolean =
    status.isBusy && !status.onTarget && status.movementStatus.isRunning

  def isOnTargetAndClosed(status: ElevatorStatus): Boolean =
    status.isBusy && status.onTarget && !status.isDoorOpened

  def isOpenedAndHaveOrders(status: ElevatorStatus): Boolean =
    status.isBusy && status.isDoorOpened && status.orders.nonEmpty

  def isOpenedAndNoOrders(status: ElevatorStatus): Boolean =
    status.isBusy && status.isDoorOpened && status.orders.isEmpty

  def getDirectionOfMoving(status: ElevatorStatus): MovementStatus =
    if (status.orders.head.numOfFloor > status.currentFloor) MovementStatus.Up
    else MovementStatus.Down

  def getValueToAdd(direction: MovementStatus): Int =  direction match {
    case MovementStatus.Up => 1
    case MovementStatus.Down => -1
    case MovementStatus.Stopped => 0
  }
}

object ElevatorStatus {
  def toServeBefore(pickupOrder: PickupOrder, currentHeadOrder: PickupOrder, currentFloor: Int): Boolean = {
    if (currentFloor < currentHeadOrder.numOfFloor) {
      if (currentHeadOrder.direction == ElevatorDirection.Down) {
        //case 1
        if (pickupOrder.numOfFloor < currentHeadOrder.numOfFloor) {
          pickupOrder.direction == ElevatorDirection.Up
        } else {
          true
        }
      } else {
        //case 2
        if (pickupOrder.numOfFloor < currentHeadOrder.numOfFloor) {
          pickupOrder.direction == ElevatorDirection.Up
        } else {
          false
        }
      }
    } else {
      if (currentHeadOrder.direction == ElevatorDirection.Down) {
        //case 3
        if (pickupOrder.numOfFloor < currentHeadOrder.numOfFloor) {
          false
        } else {
          pickupOrder.direction == ElevatorDirection.Down
        }
      } else {
        //case 4
        if (pickupOrder.numOfFloor < currentHeadOrder.numOfFloor) {
          true
        } else {
          pickupOrder.direction == ElevatorDirection.Down
        }
      }
    }
  }
}
