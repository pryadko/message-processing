import cats.effect._
import kafka.KafkaProducer
import org.http4s.implicits._
import org.http4s.server.blaze._
import route.ServerRoute

object ServiceB extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(new ServerRoute[IO](KafkaProducer.producer).routes.orNotFound)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
