# message-post-system
Service provide REST endpoint for pushing messages to the system. MongoDB is used to persist data. Every message is send to proper topic in Event Bus. Thanks to remote Akka actors you can subscribe any ActorRef to get notifications. It is a "Command" part of CQRS pattern.

## Compile
Compiles code and executes tests:

`sbt test`

## Configuration
//TBA

## Run
Run locally from terminal:

`sbt run`

Statement run application locally on default port `9091`.

## Example of use

#### Add subscriber
Subsribe reading Actor by sending `Subscribe` message to remote Event Handler. 
```
val readingActor = system
  .actorOf(Props[YourReadingActorClass])
  
val eventHandlerActor = system
  .actorSelection("akka.tcp://message-post-system@127.0.0.1:3652/user/event-handler")

eventHandlerActor ! Subscribe("system", readingActor)
```

Reading actor can expect `Subscribed` message confirmation.

#### Send message
Push message to the service by sending POST request:

`http://localhost:9091/message`

with Header:

`Content-Type: application/json`

with request Body:

```
{
  "senderId": "example senderId or system",
  "receiverId": "example receiverId",
  "text": "simple message conten",
  "date": "today or 2017-01-01T00:00:00Z"
}
```
All fields are mandatory.

