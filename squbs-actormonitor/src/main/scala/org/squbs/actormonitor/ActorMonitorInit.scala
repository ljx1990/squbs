/*
 * Licensed to Typesafe under one or more contributor license agreements.
 * See the AUTHORS file distributed with this work for
 * additional information regarding copyright ownership.
 * This file is licensed to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.squbs.actormonitor

import akka.actor._
import scala.concurrent.duration._
import org.slf4j.LoggerFactory
import org.squbs.lifecycle.ExtensionLifecycle


private class ActorMonitorInit extends ExtensionLifecycle  {
  private val logger = LoggerFactory.getLogger(this.getClass)

  override def postInit() {
    logger.info(s"postInit ${this.getClass}")

    import boot._
    implicit val system = actorSystem

    val monitorConfig = config.getConfig("squbs-actormonitor")

    import monitorConfig._
    system.actorOf(Props(classOf[ActorMonitor], ActorMonitorConfig(getInt("maxActorCount"), getInt("maxChildrenDisplay"))))
  }
}

