package io.othree.composer.data.files

import io.othree.composer.configuration.MustacheComposerConfiguration
import io.othree.composer.models.MustacheTemplateKey
import io.othree.ocular.InputStreamClient
import io.othree.ocular.file.BaseOcularRepository

import scala.concurrent.ExecutionContext

class MustacheTemplateRepository(config: MustacheComposerConfiguration,
                                 isClient: InputStreamClient)
                                (implicit override val ec: ExecutionContext)
  extends BaseOcularRepository[MustacheTemplateKey](isClient) {
  override def getKey(key: MustacheTemplateKey): String = s"${config.templatesDir}/${key.templateId}.mustache"
}
