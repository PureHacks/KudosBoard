package controllers

import play.api.mvc._
import models.domain._

object DDL extends Controller {

  def initDb = Action {
    val ddl = DAO.cards.ddl ++ DAO.comments.ddl ++ DAO.coAuthors.ddl ++ DAO.tags.ddl ++ DAO.recipients.ddl ++ DAO.users.ddl
    val drop = ddl.dropStatements.mkString(";\n") ++ ";\n"
    val create = ddl.createStatements.mkString(";\n") ++ ";\n"
    Ok(drop ++ create)
  }

}
