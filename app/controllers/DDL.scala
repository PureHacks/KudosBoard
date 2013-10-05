package controllers

import play.api.mvc._
import models.domain._

object DDL extends Controller {

  def initDb = Action {
    val ddl = Cards.ddl ++ Comments.ddl ++ CoAuthors.ddl ++ Tags.ddl ++ Recipients.ddl
    val drop = ddl.dropStatements.mkString(";\n") ++ ";\n"
    val create = ddl.createStatements.mkString(";\n") ++ ";\n"
    Ok(drop ++ create)
  }

}
