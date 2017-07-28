# JMH Tests

```
$ mvn clean install
$ java -jar target/benchmarks.jar JsonFactoryTestBeanSimple -wi 10 -i 40 -f 1
```

### Tested on i5-3550 3.3GHz
```text
# Run complete. Total time: 00:03:22

Benchmark                               Mode  Cnt        Score       Error  Units
JsonFactoryTestBeanSimple.gson         thrpt   40  2610378,517 ▒ 37294,638  ops/s
JsonFactoryTestBeanSimple.jackson      thrpt   40  5865670,372 ▒ 21658,370  ops/s
JsonFactoryTestBeanSimple.jsonB        thrpt   40  4154190,199 ▒ 10952,141  ops/s
JsonFactoryTestBeanSimple.jsonFactory  thrpt   40    88148,975 ▒   390,955  ops/s

# Run complete. Total time: 00:03:22

Benchmark                             Mode  Cnt      Score     Error  Units
JsonFactoryTestBeanList.gson         thrpt   40  37445,630 ▒ 284,642  ops/s
JsonFactoryTestBeanList.jackson      thrpt   40  83592,827 ▒ 218,986  ops/s
JsonFactoryTestBeanList.jsonb        thrpt   40  61246,003 ▒ 121,260  ops/s
JsonFactoryTestBeanList.jsonFactory  thrpt   40    983,276 ▒   8,150  ops/s

# Run complete. Total time: 00:03:23

Benchmark                              Mode  Cnt      Score     Error  Units
JsonFactoryTestBeanArray.gson         thrpt   40  10093,246 ▒ 107,504  ops/s
JsonFactoryTestBeanArray.jackson      thrpt   40  44485,383 ▒ 374,160  ops/s
JsonFactoryTestBeanArray.jsonB        thrpt   40  11151,769 ▒ 197,650  ops/s
JsonFactoryTestBeanArray.jsonFactory  thrpt   40    273,284 ▒   1,629  ops/s
```

```text
Benchmark                                  Mode  Cnt        Score       Error  Units
JsonFactoryTestDps.gsonForSimilarBean     thrpt   40  2796662,363 ▒ 21188,789  ops/s
JsonFactoryTestDps.jacksonForSimilarBean  thrpt   40  5888056,654 ▒ 13279,409  ops/s
JsonFactoryTestDps.jsonBForSimilarBean    thrpt   40  3732156,036 ▒ 19159,581  ops/s
JsonFactoryTestDps.jsonFactory            thrpt   40   167830,285 ▒   360,549  ops/s
```
