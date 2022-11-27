# ACTOR Project - Introduction

## What is the `Actor` class?

The `Actor` class is the base class to represent an organization in the supplychain simulation library. An `Actor` can have zero or more `Role` instances. The `Actor` has the following functions:

 - Sending instances of `Message` to other instances of `Actor`.
 - Receiving instances of `Message` trough a `MessageHandler` class that determines what to do when receiving a message (handle immediately, handle after a period, or handle based on a schedule or available resources).
 -  Processing the inclming messages with the matching `MessagePolicy` instances that determine how to process the `Message`. This can, for instance, be storing the message, sending a reply, or ignoring the message.
 - The actor can have zero or more `Role` instances that instantiates one or more `MessagePolicy` instances for the `Actor`. Examples are a `BuyingRole` or an `InventoryRole`.
 - When multiple `MessagePolicy` instances are present for the same message, all of them will be executed.
 
 