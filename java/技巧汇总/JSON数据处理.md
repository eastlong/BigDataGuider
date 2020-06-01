# jackson
```java
    @Test
    public void test1() throws Exception {
        int flightAgency = 1001;
        int quantity = 100;
        int quantityInitPercent = 100;
        String quantityEndDate = "2020-12-31";
        Map<String, Object> newMap = new LinkedHashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        newMap.put("票台id", flightAgency);

        Map<String, Object> quantityMap = new LinkedHashMap<>();
        quantityMap.put("出票量", quantity);
        quantityMap.put("初始百分比", quantityInitPercent);
        quantityMap.put("截止日期", quantityEndDate);

        newMap.put("出票量数据", quantityMap.toString());
        String newJson = objectMapper.writeValueAsString(newMap);

        Map<String,Object> resultMap = objectMapper.readValue(newJson,new TypeReference<Map<String,String>>() { } );
        System.out.println(resultMap.get("票台id"));
        System.out.println(resultMap.get("出票量数据"));
    }
```