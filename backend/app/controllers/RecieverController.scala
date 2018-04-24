package controllers

import javax.inject._

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

import models._

import scala.concurrent.{ExecutionContext, Future}


// NOTE: Add the following to conf/routes to enable compilation of this class:
/*
GET     /receiver        controllers.ReceiverController.receiverGet
POST    /receiver        controllers.ReceiverController.receiverPost
*/

/**
  * User form controller for Play Scala
  */
class ReceiverController @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                                   mcc: MessagesControllerComponents
                                  )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(mcc) with HasDatabaseConfigProvider[JdbcProfile] {

  val receiverForm = Form(
    mapping(
      "id" -> number,
      "lastName" -> text,
      "firstName" -> text,
      "tel" -> text,
    )(Receiver.apply)(Receiver.unapply)
  )

  def receiverSearch(name: String) = Action.async { implicit request =>
    val resultingReceivers: Future[Seq[Receiver]] = db.run(receivers.filter(_.lastName === name).result)
    resultingReceivers.map(x =>Ok(views.html.receiver.list(x)))
  }

  def receiverGet() = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.receiver.form(receiverForm))
  }

  def receiverPost() = Action { implicit request: MessagesRequest[AnyContent] =>
    receiverForm.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        BadRequest(views.html.receiver.form(formWithErrors))
      },
      receiverData => {
        /* binding success, you get the actual value. */
        /* flashing uses a short lived cookie */
        val receiverId = (receivers returning receivers.map(_.id)) += receiverData
        db.run(receiverId)
        Redirect(routes.ReceiverController.receiverGet()).flashing("success" -> ("Successful " + receiverData.toString))
      }
    )
  }
}


