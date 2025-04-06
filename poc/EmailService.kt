import aws.sdk.kotlin.services.ses.SesClient
import aws.sdk.kotlin.services.ses.model.Body
import aws.sdk.kotlin.services.ses.model.Content
import aws.sdk.kotlin.services.ses.model.Destination
import aws.sdk.kotlin.services.ses.model.Message
import aws.sdk.kotlin.services.ses.model.SendEmailRequest
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

class EmailService {
    private val logger = LoggerFactory.getLogger(EmailService::class.java)
    private val awsRegion = "us-east-1"
    private val senderEmail = ""
    private val toAddress = listOf("")
    private val sesClient = SesClient { region = awsRegion }

    fun sendEmail(emailRequest: EmailRequest) {
        runBlocking {
            try {
                val bodyHTML =
                    """
                    <html>
                        <head>
                            <title>New Email Submission</title>
                        </head>
                        <body>
                            <h1>New Email Submission</h1>
                            <p><strong>Name:</strong> ${emailRequest.name}</p>
                            <p><strong>Phone:</strong> ${emailRequest.phoneNumber}</p>
                            <p><strong>Email:</strong> ${emailRequest.email}</p>
                            <p><strong>Message:</strong></p>
                            <pre>${emailRequest.body}</pre>
                        </body>
                        </html>        
                """.trimIndent()

                val destinationOb = Destination { toAddress }

                val contentOb = Content {
                    data = bodyHTML
                    charset = "UTF-8"
                }

                val textContentOb = Content {
                    data =
                        """
                            New Email Submission:
                            Name: ${emailRequest.name}
                            Phone: ${emailRequest.phoneNumber}
                            Email: ${emailRequest.email}
                            Message:
                            ${emailRequest.body}
                        """.trimIndent()
                    charset = "UTF-8"
                }

                val bodyOb = Body {
                    html = contentOb
                    text = textContentOb
                }

                val subOb = Content {
                    data = "New Email Submission"
                    charset = "UTF-8"
                }

                val msgOb = Message {
                    subject = subOb
                    body = bodyOb
                }

                val sendEmailRequest = SendEmailRequest {
                    destination = destinationOb
                    message = msgOb
                    source = senderEmail
                }

                val sendResponse = sesClient.sendEmail(sendEmailRequest)
                logger.info("Contact form email sent.")

            } catch (e: Exception) {
                logger.error("Error sending contact form email: ${e.message}", e)
            }
        }
    }
}