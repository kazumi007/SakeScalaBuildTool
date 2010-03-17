package sake.command.builtin

import org.specs._ 
import sake.util._
import sake.environment.Environment._
import java.io.{File => JFile}

object SpecCommandSpec extends Specification {
    def makeTestSpecCommand(expectedPath: String, expectedPattern: String) = {
        new SpecCommand() {
            override def action(options: Map[Symbol, Any]):Result = {
                val path    = options.getOrElse('path,    "spec").toString()
                val pattern = options.getOrElse('pattern, ".*").toString()
                path    mustEqual expectedPath
                pattern mustEqual expectedPattern
                new Passed()
            } 
            // Don't remove 'path and 'pattern, so tests in "action" can work.
            override def removePathAndPattern(options: Map[Symbol,Any]) = options
        }
    }
    
    "SpecCommand" should {
        "default to 'spec' for the path" in {
            val sc = makeTestSpecCommand("spec", ".*")
            sc()
        }

        "default to '.*' for the pattern" in {
            val sc = makeTestSpecCommand("spec", ".*")
            sc()
        }
    }
    
    "Invoking a SpecCommand object" should {
        "override the path if 'path is specified" in {
            val sc = makeTestSpecCommand("foo", ".*")
            sc('path -> "foo")
        }

        "override the path if 'pattern is specified" in {
            val sc = makeTestSpecCommand("spec", "Spec.*")
            sc('pattern -> "Spec.*")
        }
    }
    
    "Invoking a failing spec" should {
        "throw a build error" in {
            new SpecCommand() (
                'classpath -> environment.classpath,
                'path -> new JFile("./spec/**/*.scala").getPath,
                'pattern -> "FailingSpek"
            ) must throwA[BuildError]
        }
    }
    
    "Invoking a failing specs" should {
        "return Passed" in {
            new SpecCommand() (
                'path -> new JFile("./spec/**/*.scala").getPath,
                'pattern -> "PassingSpek").success must beTrue
        }
    }
}

