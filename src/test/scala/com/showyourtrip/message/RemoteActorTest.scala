package com.showyourtrip.message

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}
import com.showyourtrip.message.models.Message
import com.showyourtrip.message.services.Subscription
import com.typesafe.config.ConfigFactory


class RemoteActorTest extends TestKit(ActorSystem("test-system", RemoteActorTest.config))
  with FunSuiteLike
  with BeforeAndAfterAll {

  override def afterAll {
    shutdown()
  }

  test("remote actor selection") {
    val actorRef = system.actorOf(Props(new Actor {
      def receive = {
        case m: Message => println(m)
      }
    }))
    val selection = system
      .actorSelection("akka.tcp://message-writing-system@127.0.0.1:3652/user/messageActor/eventActor")

    selection ! Subscription("system", actorRef)

    Thread.sleep(30000)
  }

}

object RemoteActorTest {
  val config = ConfigFactory.parseString(
    """
      akka {
        actor {
          provider = remote
          warn-about-java-serializer-usage = no
        }
        remote {
          enabled-transports = ["akka.remote.netty.tcp"]
          netty.tcp {
            hostname = "127.0.0.1"
            port = 4652
          }
        }
      }
    """)
}
