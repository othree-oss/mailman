package io.othree.pidgeon.postmark.models

case class SuccessfulEmailResponse(to: String,
                                   submittedAt: String,
                                   messageID: String,
                                   errorCode: Int,
                                   message: String)
