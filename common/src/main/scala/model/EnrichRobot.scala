package model

import java.util.concurrent.atomic.AtomicLong

import io.circe.{Decoder, Encoder}
import java.util.UUID.randomUUID

case class EnrichRobot(
                        id: Long,
                        sentient: Boolean,
                        name: String,
                        model: String,
                        eventId: Long,
                        transactionId: String) extends BaseRobot {
}

object EnrichRobot {
  private val id = new AtomicLong(0)

  implicit val decoder: Decoder[EnrichRobot] =
    Decoder.forProduct6("id", "sentient", "name", "model", "eventId", "transactionId")(EnrichRobot.apply)

  implicit val encoder: Encoder[EnrichRobot] =
    Encoder.forProduct6("id", "sentient", "name", "model", "eventId", "transactionId")(er =>
      (er.id, er.sentient, er.name, er.model, er.eventId, er.transactionId)
    )

  def toEnrichRobot(robot: Robot): EnrichRobot = {
    new EnrichRobot(robot.id, robot.sentient, robot.name, robot.model, id.getAndIncrement, randomUUID.toString)
  }

}



