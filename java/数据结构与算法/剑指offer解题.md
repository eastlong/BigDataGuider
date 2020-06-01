<!-- TOC -->

- [1. 简单](#1-简单)
    - [1.1. 二维数组中的查找](#11-二维数组中的查找)
    - [1.2. 从尾到头打印链表](#12-从尾到头打印链表)
        - [1.2.1. 非递归](#121-非递归)
    - [1.3. 重建二叉树](#13-重建二叉树)
    - [1.4. 用两个栈来实现队列](#14-用两个栈来实现队列)

<!-- /TOC -->

# 1. 简单
## 1.1. 二维数组中的查找
题目描述
在一个二维数组中（每个一维数组的长度相同），每一行都按照从左到右递增的顺序排序，每一列都按照从上到下递增的顺序排序。请完成一个函数，输入这样的一个二维数组和一个整数，判断数组中是否含有该整数。

```java
public class Solution {
    public boolean Find(int target, int [][] array) {
        int rows = array.length; // 行
        int cols = array[0].length; // 列
        if(rows == 0 || cols == 0)
            return false;
        int row = 0;
        int col = cols -1;
        while(row < rows && col >= 0){
            if(array[row][col] < target){
                row ++;
            }else if(array[row][col] > target){
                col --;
            }else {
                return true;
            }
        }
        return false;
    }
}
```

## 1.2. 从尾到头打印链表
输入一个链表，按链表从尾到头的顺序返回一个ArrayList。
### 1.2.1. 非递归
1. 分析
listNode 是链表，只能从头遍历到尾，但是输出却要求从尾到头，这是典型的"先进后出"，我们可以想到栈！
ArrayList 中有个方法是 add(index,value)，可以指定 index 位置插入 value 值
所以我们在遍历 listNode 的同时将每个遇到的值插入到 list 的 0 位置，最后输出 listNode 即可得到逆序链表

```java
/**
*    public class ListNode {
*        int val;
*        ListNode next = null;
*
*        ListNode(int val) {
*            this.val = val;
*        }
*    }
*
*/
import java.util.ArrayList;
public class Solution {
    public ArrayList<Integer> printListFromTailToHead(ListNode listNode) {
        ArrayList<Integer> list = new ArrayList();
        ListNode tmp = listNode;
        while(tmp != null){
            list.add(0, tmp.val);
            tmp = tmp.next;
        }
        return list;
    }
}
```
3. 复杂度

时间复杂度：O(n)
空间复杂度：O(n)

2. 递归
```java
/**
*    public class ListNode {
*        int val;
*        ListNode next = null;
*
*        ListNode(int val) {
*            this.val = val;
*        }
*    }
*
*/
import java.util.ArrayList;
public class Solution {
    ArrayList<Integer> list = new ArrayList();
    public ArrayList<Integer> printListFromTailToHead(ListNode listNode) {
        if(listNode != null){
            printListFromTailToHead(listNode.next);
            list.add(listNode.val);
        }
        return list;
    }
}
```

## 1.3. 重建二叉树
输入某二叉树的前序遍历和中序遍历的结果，请重建出该二叉树。假设输入的前序遍历和中序遍历的结果中都不含重复的数字。例如输入前序遍历序列{1,2,4,7,3,5,6,8}和中序遍历序列{4,7,2,1,5,3,8,6}，则重建二叉树并返回。

1. 分析

根据中序遍历和前序遍历可以确定二叉树，具体过程为：
    * 根据前序序列第一个结点确定根结点
    * 根据根结点在中序序列中的位置分割出左右两个子序列
    * 对左子树和右子树分别递归使用同样的方法继续分解 

例如：
前序序列{1,2,4,7,3,5,6,8} = pre
中序序列{4,7,2,1,5,3,8,6} = in

1. 根据当前前序序列的第一个结点确定根结点，为 1
2. 找到 1 在中序遍历序列中的位置，为 in[3]
3. 切割左右子树，则 in[3] 前面的为左子树， in[3] 后面的为右子树
4. 则切割后的左子树前序序列为：{2,4,7}，切割后的左子树中序序列为：{4,7,2}；切割后的右子树前序序列为：{3,5,6,8}，切割后的右子树中序序列为：{5,3,8,6}
5. 对子树分别使用同样的方法分解 

```java
/**
 * Definition for binary tree
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */
import java.util.*;
public class Solution {
    public TreeNode reConstructBinaryTree(int [] pre,int [] in) {
        if(pre.length == 0 || in.length == 0){
            return null;
        }
        
        TreeNode root = new TreeNode(pre[0]);
        
        // 在中序中找前序的根
        for(int i = 0; i < in.length;i++){
            if(in[i] == pre[0]){
                root.left = reConstructBinaryTree(Arrays.copyOfRange(pre,1,i+1)
                                                  ,Arrays.copyOfRange(in,0,i));
                root.right = reConstructBinaryTree(Arrays.copyOfRange(pre,i+1,pre.length),
                                                   Arrays.copyOfRange(in,i+1,in.length));
                break;
            }
        }
        return root;  
    }
}
```

3. 复杂度
时间复杂度：O(n)
空间复杂度：O(n)

## 1.4. 用两个栈来实现队列
用两个栈来实现一个队列，完成队列的Push和Pop操作。 队列中的元素为int类型。

```java

import java.util.Stack;
public class Solution {
    Stack<Integer> stack1 = new Stack<Integer>();
    Stack<Integer> stack2 = new Stack<Integer>();

    public void push(int node) {
        stack1.push(node);
    }

    public int pop() {
        if (stack2.size() <= 0) {
            while (stack1.size() != 0) {
                stack2.push(stack1.pop());
            }
        }
        return stack2.pop();
    }
}
```

复杂度
push时间复杂度：O(1)
pop空间复杂度：O(1)