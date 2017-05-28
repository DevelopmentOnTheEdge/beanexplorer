# JMH Tests

```
$ mvn clean install
$ java -jar target/benchmarks.jar JsonFactoryBeanTestArray -wi 10 -i 10 -f 1
```

```text
# Run complete. Total time: 00:04:07

Benchmark                                 Mode  Cnt      Score      Error  Units
JsonFactoryBeanTestSimple.simpleGson         thrpt   40  55159,157 ▒ 4445,051  ops/s
JsonFactoryBeanTestSimple.simpleJackson      thrpt   40  96416,320 ▒ 3944,335  ops/s
JsonFactoryBeanTestSimple.simpleJsonFactory  thrpt   40  56475,096 ▒  955,274  ops/s
```

```text
Benchmark                                        Mode  Cnt       Score       Error  Units
JsonFactoryTestDps.simpleGsonForSimilarBean     thrpt   10   53037,139 ▒ 14217,360  ops/s
JsonFactoryTestDps.simpleJacksonForSimilarBean  thrpt   10   25844,191 ▒   555,991  ops/s
JsonFactoryTestDps.simpleJsonFactory            thrpt   10  117725,407 ▒  3230,461  ops/s
```

```text
# Run complete. Total time: 00:04:07

Benchmark                              Mode  Cnt      Score     Error  Units
JsonFactoryBeanTestArray.simpleGson         thrpt   40   4524,384 ▒ 217,785  ops/s
JsonFactoryBeanTestArray.simpleJackson      thrpt   40  15017,219 ▒ 315,174  ops/s
JsonFactoryBeanTestArray.simpleJsonFactory  thrpt   40    171,877 ▒   3,781  ops/s
```

```text
Benchmark                                   Mode  Cnt      Score      Error  Units
JsonFactoryTestBeanList.simpleGson         thrpt    5   9110,273 ▒  646,035  ops/s
JsonFactoryTestBeanList.simpleJackson      thrpt    5  16060,306 ▒ 4799,021  ops/s
JsonFactoryTestBeanList.simpleJsonFactory  thrpt    5    635,596 ▒   46,014  ops/s
```
