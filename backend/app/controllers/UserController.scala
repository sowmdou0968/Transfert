package controllers

import javax.inject._

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}

import slick.jdbc.JdbcProfile
import models._
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}



// NOTE: Add the following to conf/routes to enable compilation of this class:
/*
GET     /user        controllers.UserController.userGet
POST    /user        controllers.UserController.userPost
*/

/**
  * User form controller for Play Scala
  */
class UserController @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                               mcc: MessagesControllerComponents
                              )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(mcc) with HasDatabaseConfigProvider[JdbcProfile] {


  val userForm = Form(
    mapping(
      "id" -> number,
      "lastName" -> text,
      "firstName" -> text,
      "email" -> text,
      "password" -> text,
      "tel" -> text,
      "address" -> text
    )(User.apply)(User.unapply)
  )

  def userSearch(name: String) = Action.async { implicit request =>
    val resultingUsers: Future[Seq[User]] = db.run(users.filter(_.lastName === name).result)
    resultingUsers.map(users => Ok(views.html.user.list(users)))
  }

  def userGet() = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.user.form(userForm))
  }

  def userPost() = Action { implicit request: MessagesRequest[AnyContent] =>

    userForm.bindFromRequest.fold(
      formWithErrors => {

        // binding failure, you retrieve the form containing errors:
        BadRequest(views.html.user.form(formWithErrors))
      },
      userData => {
        /* binding success, you get the actual value. */
        /* flashing uses a short lived cookie */
        val userId = (users returning users.map(_.id)) += userData
        db.run(userId)
        Redirect(routes.UserController.userGet()).flashing("success" -> ("Successful " + userData.toString))
      }
    )
  }
}