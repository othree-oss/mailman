package io.othree.composer.exceptions

class TemplateNotFoundException(val templateId: String, message: String) extends ComposerException(message)
