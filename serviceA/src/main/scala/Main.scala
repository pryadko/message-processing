import java.util.concurrent.Executors

import cats.effect.{ContextShift, Fiber, IO}
import model.RobotBuilder
import io.circe.literal._
import io.circe.syntax._

import scala.concurrent.ExecutionContext


object Main {

  import scala.concurrent.ExecutionContext.global

  implicit val cs: ContextShift[IO] = IO.contextShift(global)

  val executorService = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(5))

  val contexShift: ContextShift[IO] = IO.contextShift(executorService)

  val client = new HttpClient[IO]()

  def infiniteIO(execId: Int)(implicit cs: ContextShift[IO]): IO[Fiber[IO, Unit]] = {
    def repeat: IO[Unit] = IO(
      client.post("http://localhost:8080/", RobotBuilder.createRobot(execId).asJson).start.unsafeRunSync()
    )
      .map(_ => Thread.sleep(4000))
      .flatMap(_ => IO.shift *> repeat)

    repeat.start
  }

  def main(args: Array[String]): Unit = {
    val prog =
      for {
        _ <- infiniteIO(1)(contexShift)
        _ <- infiniteIO(2)(contexShift)
        _ <- infiniteIO(3)(contexShift)
        _ <- infiniteIO(4)(contexShift)
      } yield ()

    prog.unsafeRunSync()
  }

}
