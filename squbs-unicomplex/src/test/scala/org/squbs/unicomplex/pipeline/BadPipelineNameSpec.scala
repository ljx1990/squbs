/*
 * Copyright 2015 PayPal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.squbs.unicomplex.pipeline

import akka.actor.ActorSystem
import akka.pattern._
import akka.stream.ActorMaterializer
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}
import org.squbs.lifecycle.GracefulStop
import org.squbs.unicomplex._

import scala.concurrent.Await

object BadPipelineNameSpec {

  val classPaths = Array(getClass.getClassLoader.getResource("classpaths/pipeline/BadPipelineNameSpec").getPath)

  val config = ConfigFactory.parseString(
    s"""
       |default-listener.bind-port = 0
       |squbs {
       |  actorsystem-name = BadPipelineNameSpec
       |  ${JMX.prefixConfig} = true
       |}
    """.stripMargin
  )

  val boot = UnicomplexBoot(config)
    .createUsing {(name, config) => ActorSystem(name, config)}
    .scanComponents(classPaths)
    .initExtensions.start()
}

class BadPipelineNameSpec extends TestKit(
  BadPipelineNameSpec.boot.actorSystem) with FlatSpecLike with Matchers with ImplicitSender with BeforeAndAfterAll {

  implicit val am = ActorMaterializer()
  import Timeouts._

  val portBindings = Await.result((Unicomplex(system).uniActor ? PortBindings).mapTo[Map[String, Int]], awaitMax)
  val port = portBindings("default-listener")

  override def afterAll() {
    Unicomplex(system).uniActor ! GracefulStop
  }

  "System state" should "be Failed" in {
    Unicomplex(system).uniActor ! ReportStatus
    val StatusReport(systemState, _, _) = expectMsgType[StatusReport]
    systemState should be(Failed)
  }

}