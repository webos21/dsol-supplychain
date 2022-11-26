# ACTOR Project - Introduction

## What is the `Actor` class?

The `Actor` class is the base class to represent an organization in the supplychain simulation library. 
The `Actor` has the following functions:

 - Sending instances of `Message` to other instances of `Actor`.
 - Receiving and handling instances of `Message` trough `Handler` classes that determine how to 'handle' the `Message`.
 - The actor can have zero or more `Role` instances that instantiates one or more `Handler` instances for the `Actor`. Examples are a `BuyingRole` or an `InventoryRole`.
 - When multiple `Handler` instances are present for the same message, all of them will be executed.
 
 