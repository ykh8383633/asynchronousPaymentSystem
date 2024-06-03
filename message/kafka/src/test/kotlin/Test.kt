import org.apache.kafka.common.protocol.types.Field.Bool
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture

class Test {
    @Test
    fun futureTest() {

        val future = futureMethod()
        println(future.get())

    }

    fun futureMethod(): CompletableFuture<Boolean> {
        var result = true;

        return CompletableFuture.supplyAsync{
            if(false){
                return@supplyAsync true
            }

            throw Exception("err")
        }.exceptionally {
            false
        }.thenApply {
            return@thenApply  it
        }
    }

    @Test
    fun test2() {
        futureMethode2().thenApply{
            if(it == "hi"){
                throw Exception("err")
            }
            "bye"
        }.exceptionally {
            return@exceptionally null
        }.thenAccept(){
            println(it)
        }
    }

    fun futureMethode2(): CompletableFuture<String>{
        return CompletableFuture.supplyAsync{"hi"}
    }


}