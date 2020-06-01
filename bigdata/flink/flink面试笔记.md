# WordCount
```scala
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object WordCountScala {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val lines = env.socketTextStream("192.168.1.4",9999)

    val result = lines
      .flatMap(x => x.split(","))
      .map(x => WordCountEntity(x,1))
      .keyBy("word")
      .sum("count")

    result.print().setParallelism(1)
    env.execute("WordCountScala")
  }

  case class WordCountEntity(word:String,count:Long)

}
```