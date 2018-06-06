package io.othree.composer

import java.io.File

import com.typesafe.scalalogging.LazyLogging
import io.othree.composer.configuration.MustacheComposerConfiguration
import io.othree.composer.data.files.MustacheTemplateRepository
import io.othree.composer.exceptions.TemplateNotFoundException
import io.othree.composer.models.MustacheTemplateKey
import io.othree.envelope.models.Envelope
import org.fusesource.scalate.{TemplateEngine, TemplateSource}

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

class MustacheTemplateComposer(config: MustacheComposerConfiguration,
                               mustacheTemplateRepository: MustacheTemplateRepository)
                              (implicit ec: ExecutionContext)
  extends TemplateComposer
    with LazyLogging {

  private val engine = new TemplateEngine()
  engine.workingDirectory = new File(config.buildDir)

  override def compose(envelope: Envelope): Future[String] = {
    val templateKey = mustacheTemplateRepository.getKey(MustacheTemplateKey(envelope.templateId))
    mustacheTemplateRepository.get(MustacheTemplateKey(envelope.templateId)) map { maybeTemplate =>
      if (maybeTemplate.isEmpty) {
        throw new TemplateNotFoundException(envelope.templateId, s"Template ${envelope.templateId} not found in path: $templateKey")
      }

      val mustacheTemplate = maybeTemplate.get

      logger.info(s"Loading template: ${envelope.templateId}")
      val source = TemplateSource.fromSource(templateKey, Source.fromInputStream(mustacheTemplate.inputStream))
      val template = engine.load(source)

      logger.info(s"Processing layout for template: ${envelope.templateId}")
      val output = engine.layout(envelope.templateId, template, envelope.content)

      output
    }
  }
}
