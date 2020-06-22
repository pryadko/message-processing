package model

import io.circe.{Decoder, Encoder}
import model.Robot.rng

case class Robot(id: Long,
                 sentient: Boolean,
                 name: String,
                 model: String) {
}
object Robot {
  val rng = new scala.util.Random(0L)
  implicit val decoder: Decoder[Robot] =
    Decoder.forProduct4("id", "sentient", "name", "model")(Robot.apply)

  implicit val encoder: Encoder[Robot] =
    Encoder.forProduct4("id", "sentient", "name", "model")(r =>
      (r.id, r.sentient, r.name, r.model)
    )
}

object RobotBuilder {
  def createRobot(execId: Int): Robot = {

    val id = rng.nextLong()
    val sentient = rng.nextBoolean()
    val name = "Carlos " + execId
    val isReplicant = rng.nextBoolean()
    val model = if (isReplicant) "replicant" else "borg"
    Robot(id, sentient, name, model)
  }

}



