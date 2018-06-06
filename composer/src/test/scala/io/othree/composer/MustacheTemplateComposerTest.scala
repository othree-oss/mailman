package io.othree.composer

import java.io.File

import io.othree.aok.AsyncBaseTest
import io.othree.composer.configuration.MustacheComposerConfiguration
import io.othree.composer.data.files.MustacheTemplateRepository
import io.othree.composer.exceptions.TemplateNotFoundException
import io.othree.envelope.models.{Envelope, Package}
import io.othree.ocular.clients.LocalFileClient
import org.junit.runner.RunWith
import org.mockito.Mockito._
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MustacheTemplateComposerTest extends AsyncBaseTest {

  case class ExampleData(name: String,
                         override val to: String,
                         override val attachments: Option[Array[Package]],
                         override val templateId: String = "example.html") extends Envelope {
    override def content: Map[String, Any] = {
      Map(("name", name))
    }

    override val subject: String = "Example Email"
  }

  var mustacheTemplateComposer: TemplateComposer = _

  val baseDir = new File(getClass.getResource("/example.html.mustache").getPath).getParent

  override protected def beforeAll(): Unit = {
    val mustacheComposerConfiguration = mock[MustacheComposerConfiguration]
    when(mustacheComposerConfiguration.templatesDir).thenReturn(baseDir)
    when(mustacheComposerConfiguration.buildDir).thenReturn(baseDir)

    val isClient = new LocalFileClient("")
    val mustacheTemplateRepository = new MustacheTemplateRepository(mustacheComposerConfiguration, isClient)

    mustacheTemplateComposer = new MustacheTemplateComposer(mustacheComposerConfiguration, mustacheTemplateRepository)
  }

  "MustacheTemplateComposer" when {
    "composing a template" must {
      "return a string with the templated variables replaced" in {
        val eventualResult = mustacheTemplateComposer.compose(ExampleData("othree", "mail@email.com", None))

        eventualResult map { result =>
          result shouldBe "<b>othree</b>"
        }
      }
    }

    "composing an invalid template id" must {
      "throw a TemplateNotFoundException" in {

        val eventualException = recoverToExceptionIf[TemplateNotFoundException] {
          mustacheTemplateComposer.compose(ExampleData("othree", "mail@email.com", Some(Array(Package("hola", "base64Content", ".png"))), "notFound.html"))
        }

        eventualException map { exception =>
          exception.templateId shouldBe "notFound.html"
          exception.getMessage shouldBe s"Template notFound.html not found in path: $baseDir/notFound.html.mustache"
        }
      }
    }

    "composing a template with attachments" must {
      "return a string with the template variables replaced" in {

        val eventualResult = mustacheTemplateComposer.compose(ExampleData("attachments", "mail@mail.com", Some(Array(Package("photo", "base64Content", ".png")))))

        eventualResult map { result =>
          result shouldBe "<b>attachments</b>"
        }
      }
    }
  }
}
