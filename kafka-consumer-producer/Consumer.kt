import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.StringDeserializer
import java.util.Properties

fun main() {
    val consumer = createConsumer()
    consumer.subscribe(listOf("new-test-topic"))

    while(true) {
        consumer.poll(java.time.Duration.ofSeconds(3)).iterator().forEach { e ->
            println("Message received: ${e.value()}")
        }
    }
}

private fun createConsumer(): Consumer<String, UserMessage> {
    val props = Properties()
    props[BOOTSTRAP_SERVERS_CONFIG] = "localhost:29092"
    props[KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
    props[VALUE_DESERIALIZER_CLASS_CONFIG] = UserMessageDeserializer::class.java
    props[AUTO_OFFSET_RESET_CONFIG] = "latest"
    props[GROUP_ID_CONFIG] = "test-consumer-group"
    return KafkaConsumer(props)
}

class UserMessageDeserializer : Deserializer<UserMessage> {
    override fun deserialize(topic: String?, data: ByteArray?): UserMessage {
        val mapper = ObjectMapper().registerModule(KotlinModule())
        if (data == null) {
            return UserMessage(0, "None")
        }
        return mapper.readValue(data)
    }
}