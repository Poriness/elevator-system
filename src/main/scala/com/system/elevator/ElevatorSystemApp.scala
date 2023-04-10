package com.system.elevator

import scala.io.StdIn

object ElevatorSystemApp extends App {

  runApp()
  
  def runApp(): Unit = {
    println("How many floors?")
    val numOfFloors = StdIn.readInt()
    println("How many elevators?")
    val numOfElevators = StdIn.readInt()
    val system = ElevatorSystemImpl(numOfElevators, numOfFloors)

    println(s"System is running with $numOfElevators elevators and $numOfFloors floors")
    println("------------------------------")
    println("Enter 'q' to exit system")
    println("Enter 's' to simulate one step")
    println("Enter 'status' to print elevator statuses")
    println("Enter '{numberOfFloor},'{up}|{down}' to call elevator")
    println("       '0,up'   - calling elevator up   on 0 floor")
    println("       '3,down' - calling elevator down on 3 floor")
    println("------------------------------")

    //simulation
    var toContinue = true
    while (toContinue) {
      println("waiting for the input...")
      toContinue = translateLine(StdIn.readLine(), system)
    }
  }

  def translateLine(s: String, system: ElevatorSystemImpl): Boolean = {
    s.trim match {
      case "q" => false
      case "s" =>
        system.step()
        true
      case "status" =>
        system.status().foreach(println)
        true
      case s =>
        translateToPickupOrder(s, system)
        true
    }
  }

  def translateToPickupOrder(s: String, system: ElevatorSystem): Unit = {
    val params = s.split(",")
    try {
      val num = params(0).toInt
      val direction = params(1) match {
        case "up" => ElevatorDirection.Up
        case "down" => ElevatorDirection.Down
      }
      system.pickup(PickupOrder(num, direction))
    } catch {
      case _: RuntimeException =>
        println(s"Incorrect argument '$s'")
    }
  }
}




