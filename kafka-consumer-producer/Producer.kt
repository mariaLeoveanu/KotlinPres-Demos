import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.Serializer
import org.apache.kafka.common.serialization.StringSerializer
import java.util.Properties

fun main() {
    val producer = createProducer()

    for (i in 1..10) {
        val future = producer.send(
            ProducerRecord("new-test-topic", i.toString(), UserMessage(i, "John $i"))
        )
        future.get()
    }
}

data class UserMessage (val userId: Int, val userName: String)

private fun createProducer(): Producer<String, UserMessage> {
    val props = Properties()
    props["bootstrap.servers"] = "localhost:29092"
    props["key.serializer"] = StringSerializer::class.java
    props["value.serializer"] = UserMessageSerializer::class.java
    return KafkaProducer(props)
}

class UserMessageSerializer : Serializer<UserMessage> {
    override fun serialize(topic: String?, data: UserMessage?): ByteArray {
        val mapper = ObjectMapper().registerModule(KotlinModule())
        return mapper.writeValueAsBytes(data)
    }
}