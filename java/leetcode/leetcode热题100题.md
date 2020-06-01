LeetCode热题100题

<!-- TOC -->

- [1. 两数之和](#1-两数之和)
- [53. 最大子序和](#53-最大子序和)
- [121. 买卖股票的最佳时机](#121-买卖股票的最佳时机)
- [169. 多数元素](#169-多数元素)

<!-- /TOC -->
### 1. 两数之和
给定一个整数数组 nums 和一个目标值 target，请你在该数组中找出和为目标值的那 两个 整数，并返回他们的数组下标。

你可以假设每种输入只会对应一个答案。但是，你不能重复利用这个数组中同样的元素。

示例:

给定 nums = [2, 7, 11, 15], target = 9

因为 nums[0] + nums[1] = 2 + 7 = 9
所以返回 [0, 1]

1. 暴力解题法
```java
class Solution {
    public int[] twoSum(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[j] == target - nums[i]) {
                    return new int[] { i, j };
                }
            }
        }
        throw new IllegalArgumentException("No two sum solution");
    }
}
```

2. 两遍Hash表

```java
class Solution {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            map.put(nums[i], i);
        }
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement) && map.get(complement) != i) {
                return new int[] { i, map.get(complement) };
            }
        }
        throw new IllegalArgumentException("No two sum solution");
    }
}

```

### 53. 最大子序和
给定一个整数数组 nums ，找到一个具有最大和的连续子数组（子数组最少包含一个元素），返回其最大和。

示例:
```
输入: [-2,1,-3,4,-1,2,1,-5,4],
输出: 6
解释: 连续子数组 [4,-1,2,1] 的和最大，为 6。
```
a. 基础版本 - 动态规划
```java
class Solution {
    public int maxSubArray(int[] nums){
        int n = nums.length;
        int[] dp = new int[n]; // 动态数组，一直右移寻找
        dp[0] = nums[0];
        int res = nums[0]; // 最大结果值（保存了历史最大结果值）
        for(int i = 1;i<n;i++){
            dp[i] = Math.max(nums[i],dp[i-1]+nums[i]);
            res = Math.max(res,dp[i]);
        }
        return res;
    }
}
```

b.优化
```java
class Solution {
    public int maxSubArray(int[] nums) {
        int dp = nums[0]; 
        int res = dp; // 最大结果值（保存了历史最大结果值）
        for (int i = 1; i < nums.length; i++) {
            dp = Math.max(nums[i], dp + nums[i]);// 要么当下数值最大，要么前n-1个数值的maxSum+当前值的和最大
            res = Math.max(res, dp); // res才是全局的最大值
        }
        return res;
    }
}
```

### 121. 买卖股票的最佳时机
给定一个数组，它的第 i 个元素是一支给定股票第 i 天的价格。

如果你最多只允许完成一笔交易（即买入和卖出一支股票一次），设计一个算法来计算你所能获取的最大利润。

注意：你不能在买入股票前卖出股票。

```
输入: [7,1,5,3,6,4]
输出: 5
解释: 在第 2 天（股票价格 = 1）的时候买入，在第 5 天（股票价格 = 6）的时候卖出，最大利润 = 6-1 = 5 。
     注意利润不能是 7-1 = 6, 因为卖出价格需要大于买入价格。

输入: [7,6,4,3,1]
输出: 0
解释: 在这种情况下, 没有交易完成, 所以最大利润为 0。     
```

```java
class Solution {
    public int maxProfit(int[] prices) {
        if(prices.length == 0) return 0; // 有数组时最好先判断数组的长度是否为0的特殊情况
        int res = 0; // 存放利润结果值
        int minPrice = prices[0];
        for(int i = 1; i< prices.length; i++){
            res = Math.max(res,prices[i] - minPrice);
            minPrice = Math.min(minPrice,prices[i]);
        }

        return res;
    }
}
```

### 169. 多数元素
给定一个大小为 n 的数组，找到其中的多数元素。多数元素是指在数组中出现次数大于 ⌊ n/2 ⌋ 的元素。

你可以假设数组是非空的，并且给定的数组总是存在多数元素。

```
示例 1:

    输入: [3,2,3]
    输出: 3

示例 2:

    输入: [2,2,1,1,1,2,2]
    输出: 2
```

【解题思路】
如果我们把众数记为 +1+1+1，把其他数记为 −1-1−1，将它们全部加起来，显然和大于 0，从结果本身我们可以看出众数比其他数多。

算法 - Boyer-Moore 投票算法
```
Boyer-Moore 算法的本质和方法四中的分治十分类似。我们首先给出 Boyer-Moore 算法的详细步骤：

    我们维护一个候选众数 candidate 和它出现的次数 count。初始时 candidate 可以为任意值，count 为 0；

    我们遍历数组 nums 中的所有元素，对于每个元素 x，在判断 x 之前，如果 count 的值为 0，我们先将 x 的值赋予 candidate，随后我们判断 x：

        如果 x 与 candidate 相等，那么计数器 count 的值增加 1；

        如果 x 与 candidate 不等，那么计数器 count 的值减少 1。

    在遍历完成后，candidate 即为整个数组的众数。
``` 

【解题程序】
```java
class Solution {
    public int majorityElement(int[] nums) {
        int count = 0;
        Integer candidate = null;
        for(int num: nums){
            if(count == 0){
                candidate = num;
            }
            count += ((candidate == num) ? 1 : -1);
        }

        return candidate;
    }
}
```
