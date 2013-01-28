import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName           = "angularjs_play2_mongodb"
  val appVersion        = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "be.objectify" %% "deadbolt-scala" % "2.0-SNAPSHOT",
    "org.reactivemongo" %% "play2-reactivemongo" % "0.9-SNAPSHOT"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += Resolver.url("Objectify Play Repository", url("http://schaloner.github.com/releases/"))(Resolver.ivyStylePatterns),
    resolvers += Resolver.url("Objectify Play Snapshot Repository", url("http://schaloner.github.com/snapshots/"))(Resolver.ivyStylePatterns),
    resolvers += "ReactiveMongo Snapshot" at "https://oss.sonatype.org/content/repositories/snapshots"   
  )

}
