package controllers

import javax.inject._
import java.sql.Date
import java.time.LocalDate

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import models._
import slick.jdbc.MySQLProfile.api._
import play.api.data.format.Formats._

import scala.concurrent.{ExecutionContext, Future}



// NOTE: Add the following to conf/routes to enable compilation of this class:
/*
GET     /transfert        controllers.TransfertController.transfertGet
POST    /transfert        controllers.TransfertController.transfertPost

*/

/**
  * Transfert form controller for Play Scala
  */
class TransfertController @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                                    mcc: MessagesControllerComponents
                                   )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(mcc) with HasDatabaseConfigProvider[JdbcProfile] {


  val transfertForm: Form[Transfert]  = Form(
    mapping(
      "id" -> number,
      "date_Transfert" -> of[LocalDate],
      "amount" -> of[Double]
    )(Transfert.apply)(Transfert.unapply)
  )

  def transfertSearch(name: LocalDate) = Action.async { implicit request =>
    val resultingTransferts: Future[Seq[Transfert]] = db.run(transferts.filter(_.date_transfert === name).result)
    resultingTransferts.map(transferts =>Ok(views.html.transfert.list(transferts)))
  }


  def transfertGet() = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.transfert.form(transfertForm))
  }

  def transfertPost() = Action { implicit request: MessagesRequest[AnyContent] =>
    transfertForm.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        BadRequest(views.html.transfert.form(formWithErrors))
      },
      transfertData => {
        /* binding success, you get the actual value. */
        /* flashing uses a short lived cookie */
        val transfertId = (transferts returning transferts.map(_.id)) += transfertData
        db.run(transfertId)
        Redirect(routes.TransfertController.transfertGet()).flashing("success" -> ("Successful " + transfertData.toString))
      }
    )
  }
}

