
# 解决maven Build项目依赖冲突的问
Maven Build项目过程中，常出现依赖冲突问题，导致项目build失败。  
【场景】依赖冲突，提示如下两个依赖：
  org.elasticsearch:elasticsearch:jar:2.4.3:compile
    org.elasticsearch.plugin:lang-mustache-client:jar:5.3.2:compile  
【分析】冲突的可能不是pom文件中的依赖，而是依赖的依赖，必须找到冲突的源头，去掉一个冲突类或者jar包。  

【解决方案】
```sh
mvn dependency:tree -Dverbose -Dincludes=org.elasticsearch:org.elasticsearch.plugin
```
`org.elasticsearch`  :  `org.elasticsearch.plugin` 分别是两个冲突的源头。

查找问题的源头，并在对应的依赖中去掉一个jar包依赖。

```xml
<dependency>
    <groupId>org.elasticsearch.client</groupId>
    <artifactId>transport</artifactId>
    <version>${elasticsearch.version}</version>
    </exclusions>
        <exclusion>
            <groupId>org.elasticsearch.plugin</groupId>
            <artifactId>lang-mustache-client</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

# maven install时报错
Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.12.4:test

解决办法1：
```sh
mvn clean package -Dmaven.test.skip=true
```
解决方法2：
```xml
<plugin>  
        <groupId>org.apache.maven.plugins</groupId>  
        <artifactId>maven-surefire-plugin</artifactId>  
        <version>2.4.2</version>  
        <configuration>  
          <skipTests>true</skipTests>  
        </configuration>  
</plugin>
```