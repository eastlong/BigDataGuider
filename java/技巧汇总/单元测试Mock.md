# PowerMockito基本使用
## 依赖
```xml
<dependency>
    <groupId>org.powermock</groupId>
    <artifactId>powermock-module-junit4</artifactId>
    <version>1.6.6</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.powermock</groupId>
    <artifactId>powermock-api-mockito</artifactId>
    <version>1.6.6</version>
    <scope>test</scope>
    <exclusions>
        <exclusion>
            <groupId>org.objenesis</groupId>
            <artifactId>objenesis</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.hamcrest</groupId>
            <artifactId>hamcrest-core</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

## 使用案例
```java
@RunWith(PowerMockRunner.class)
@PrepareForTest({ DalClient.class }) // mock的类
public class TrafficDaoTest {
    TrafficDao dao = null;

    @Before
    public void setUp() throws SQLException {
        DalClient client = PowerMockito.mock(DalClient.class);
        // mock delete
        PowerMockito.when(client.query(anyString(),anyObject(), anyObject(), (List<DalResultSetExtractor<?>>) anyObject())).thenReturn(null);
        // mock batchUpdate
        PowerMockito.when(client.batchUpdate(anyString(),anyObject(),anyObject())).thenReturn(new int[]{1,2,3});

        dao = new TrafficDao();
    }

    @Test
    public void testGetTrafficInfoByCondition(){
        dao.getTrafficInfoByCondition("");
    }

    @Test
    public void testInsertTrafficInfo(){
        List<TrafficInfo> trafficInfoList = new ArrayList<>();
        TrafficInfo info = new TrafficInfo();
        System.out.println("result:" + dao.insertTrafficInfo(trafficInfoList));
    }

}
```
        