package com.system.elevator

import scala.collection.mutable

trait ElevatorSystem {
  def numOfFloors: Int
  def numOfElevator: Int

  def pickup(button: PickupOrder): Unit

  def update(elevatorId: ElevatorId, orders: List[PickupOrder]): Unit

  def step(): Unit

  def status(): mutable.Map[ElevatorId, ElevatorStatus]
}
