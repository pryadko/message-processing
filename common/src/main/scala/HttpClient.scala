import cats.effect.{ConcurrentEffect, IO}
import io.circe.Json
import org.http4s.{Method, Request, Uri}
import org.http4s.circe._
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.dsl.Http4sClientDsl

import scala.concurrent.ExecutionContext.global

class HttpClient[F[_] : ConcurrentEffect] extends Http4sClientDsl[IO] {
  val clientBuilder: BlazeClientBuilder[F] = BlazeClientBuilder[F](global)

  def get(url: String): F[String] =
    clientBuilder.resource.use { client =>
      val req = Request[F](uri = Uri.unsafeFromString(url))
      client.expect[String](req)
    }

  def post(url: String, body: Json): F[String] =
    clientBuilder.resource.use { client =>
      val req = Request[F](method = Method.POST, uri = Uri.unsafeFromString(url))
        .withEntity(body)

      client.expect[String](req)
    }
}
