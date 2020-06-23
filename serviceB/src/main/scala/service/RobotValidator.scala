package service

import model.EnrichRobot

object RobotValidator {

  type ValidationError = String

  def validate(enrichRobot: EnrichRobot): Either[ValidationError, EnrichRobot] = {
    for {
      _ <- validateId(enrichRobot.id)
    } yield enrichRobot
  }

  def validateId(id: Long): Either[ValidationError, Long] =
    if (id > 0) Right(id) else Left("Id must be positive")
}
