/*
 * Copyright 2015 Heiko Seeberger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.heikoseeberger.akkasse.example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import de.heikoseeberger.akkasse.{ EventStreamUnmarshalling, ServerSentEvent }
import java.time.LocalTime

object TimeClient {
  import EventStreamUnmarshalling._

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val mat = ActorMaterializer()
    import system.dispatcher

    Source.single(Get())
      .via(Http().outgoingConnection("127.0.0.1", 9000))
      .mapAsync(1)(Unmarshal(_).to[Source[ServerSentEvent, Any]])
      .runForeach(_.runForeach(event => println(s"${LocalTime.now()} $event")))
  }
}
