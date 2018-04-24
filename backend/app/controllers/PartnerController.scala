package controllers

import javax.inject._

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import models._
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContext

import scala.concurrent.{ExecutionContext, Future}


// NOTE: Add the following to conf/routes to enable compilation of this class:
/*
GET     /partner        controllers.PartnerController.partnerGet
POST    /partner        controllers.PartnerController.partnerPost
*/

/**
  * Partner form controller for Play Scala
  */
class PartnerController @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                                  mcc: MessagesControllerComponents
                                 )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(mcc) with HasDatabaseConfigProvider[JdbcProfile] {


  val partnerForm = Form(
    mapping(
      "id" -> number,
      "Name" -> text,
      "Location" -> text,
      "City" -> text,
      "tel" -> text,
    )(Partner.apply)(Partner.unapply)
  )

  def partnerSearch(name: String) = Action.async { implicit request =>
    val resultingPartners: Future[Seq[Partner]] = db.run(partners.filter(_.Name === name).result)
    resultingPartners.map(partners => Ok(views.html.partner.list(partners)))
  }

  def partnerGet() = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.partner.form(partnerForm))
  }

  def partnerPost() = Action { implicit request: MessagesRequest[AnyContent] =>
    partnerForm.bindFromRequest.fold(
      formWithErrors => {
        //        throw new Exception(""+1)
        // binding failure, you retrieve the form containing errors:
        BadRequest(views.html.partner.form(formWithErrors))
      },
      partnerData => {

        /* binding success, you get the actual value. */
        /* flashing uses a short lived cookie */

        val partnerId = (partners returning partners.map(_.id)) += partnerData
        db.run(partnerId)
        Redirect(routes.PartnerController.partnerGet()).flashing("success" -> ("Successful " + partnerData.toString))
      }
    )
  }
}
