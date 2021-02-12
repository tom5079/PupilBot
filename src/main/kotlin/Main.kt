import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import xyz.quaver.hitomi.getGalleryBlock

val urlRegex = Regex(object {}.javaClass.getResource("urlRegex").readText())
val numberRegex = Regex("\\d+")

val acceptedType = listOf("doujinshi", "manga", "game CG", "artist CG")
val warningTag = System.getenv("PUPILBOT_WARNING_TAG")?.split(";") ?: emptyList()

fun main() {
    val jda = JDABuilder.createDefault(System.getenv("PUPILBOT_DISCORD_TOKEN")).build()

    jda.addEventListener(object: ListenerAdapter() {
        override fun onMessageReceived(event: MessageReceivedEvent) {
            if (event.message.author.isBot)
                return

            if (urlRegex.matches(event.message.contentDisplay))
                return

            numberRegex.findAll(event.message.contentDisplay).mapNotNull { it.value.toIntOrNull() }
                .forEach { galleryID ->
                    val galleryBlcok = runCatching { getGalleryBlock(galleryID) }.getOrElse { return@forEach }!!

                    if (galleryBlcok.type in acceptedType)
                        event.channel.sendMessage(
                                """
                                    https://hitomi.la/galleries/$galleryID.html
                                    ${galleryBlcok.title}
                                    ${
                                        galleryBlcok.relatedTags.filter { tag ->
                                            warningTag.any { wtag -> tag.contains(wtag) }
                                        }.let { 
                                            if (it.isNotEmpty())
                                                ":warning: ${it.joinToString()}"
                                            else
                                                ""
                                        }
                                    }
                                """.trimIndent()
                        ).queue()
                }
        }
    })
}