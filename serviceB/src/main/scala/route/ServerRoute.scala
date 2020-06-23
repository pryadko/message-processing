package route

import cats.effect.Sync
import io.circe.syntax._
import model.{EnrichRobot, Robot}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, EntityEncoder, Header, HttpRoutes}
import service.RobotValidator


class ServerRoute[F[_] : Sync](producer: KafkaProducer[String, String]) extends Http4sDsl[F] {

  implicit val robotEntityDecoder: EntityDecoder[F, Robot] = jsonOf[F, Robot]
  implicit val robotEntityEncoder: EntityEncoder[F, Robot] = jsonEncoderOf[F, Robot]

  implicit val enrichRobotEntityDecoder: EntityDecoder[F, EnrichRobot] = jsonOf[F, EnrichRobot]
  implicit val enrichRobotEntityEncoder: EntityEncoder[F, EnrichRobot] = jsonEncoderOf[F, EnrichRobot]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "status" =>
      Ok(s"Version of service B is 0.0.1")

    case req@POST -> Root / "events" =>
      req.decode[Robot] { robot =>
        val enrichRobot = EnrichRobot.toEnrichRobot(robot)

        if (RobotValidator.validate(enrichRobot).isLeft)
          BadRequest("Invalid Robot", Header("X-Transaction-Id", enrichRobot.transactionId))
        else
        {
          producer.send(new ProducerRecord[String, String]("robot", enrichRobot.asJson.toString()))

          Ok(enrichRobot, Header("X-Transaction-Id", enrichRobot.transactionId))
        }

      }
  }

}
