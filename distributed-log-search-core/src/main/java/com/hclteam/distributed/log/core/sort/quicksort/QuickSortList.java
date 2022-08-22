package com.hclteam.distributed.log.core.sort.quicksort;


import com.alibaba.fastjson.JSONObject;

import java.util.Arrays;
import java.util.List;

public class QuickSortList {
    public static <T extends Comparable<? super T>> void sort(List<T> arr) {
        sort(arr, 0, arr.size() - 1, 5);
    }

    /**
     * @param arr   待排序的数组
     * @param left  左闭
     * @param right 右闭
     * @param k     当快排递归到子问题的规模 <= k 时，采用插入排序优化
     * @param <T>   泛型，待排序可比较类型
     */
    public static <T extends Comparable<? super T>> void sort(List<T> arr, int left, int right, int k) {
        // 规模小时采用插入排序
//        if (right - left <= k) {
//            insertionSort(arr, left, right);
//            return;
//        }
        if (left >= right) return;
        int p = partition(arr, left, right);
        sort(arr, left, p - 1, k);
        sort(arr, p + 1, right, k);
    }

    public static <T extends Comparable<? super T>> void insertionSort(List<T> arr, int l, int r) {
        System.out.println("插入排序");
        for (int i = l + 1; i <= r; i++) {
            T cur = arr.get(i);
            int j = i - 1;
            for (; j >= 0 && cur.compareTo(arr.get(j)) > 0; j--) {
                arr.set(j + 1, arr.get(j));
            }
            arr.set(j + 1, cur);
        }
    }

    private static <T extends Comparable<? super T>> int partition(List<T> arr, int left, int right) {
        //排序前，先让基准值和随机的一个数进行交换。这样，基准值就有随机性。
        //就不至于在数组相对有序时，导致左右两边的递归规模不一致，产生最坏时间复杂度
        swap(arr, left, (int) (Math.random() * (right - left + 1) + left));
        T base = arr.get(left);
        //基准值，每次都把这个基准值抛出去，看成[left+1.....right]左闭右闭区间的排序
        int i = left;
        //对于上一行的[left+1.....right]区间，i表示 [left+1......i)左闭右开区间的值都小于等于base。
        int j = right;
        //对于上二行的[left+1.....right]区间，j表示 (j......right]左开右闭区间的值都大于等于base。
        while (i < j) {
            //从右到左扫描，扫描出第一个比base小的元素，然后j停在那里。
            while (j > i && arr.get(j).compareTo(base) < 0) j--;
            arr.set(i, arr.get(j));
//            arr[i] = arr[j];
            //从左到右扫描，扫描出第一个比base大的元素，然后i停在那里。
            while (i < j && arr.get(i).compareTo(base) > 0) i++;
            arr.set(j, arr.get(i));
//            arr[j] = arr[i];
        }
        arr.set(j, base);
//        arr[j] = base;
        return j;
        //返回一躺排序后，基准值的下角标
    }

    public static <T> void swap(List<T> arr, int i, int j) {
        if (i != j) {
            T temp = arr.get(i);
            arr.set(i, arr.get(j));
            arr.set(j, temp);
//            arr[i] = arr[j];
//            arr[j] = temp;
        }
    }

    private static <T> void printArr(List<T> arr) {
        for (T o : arr) {
            System.out.print(JSONObject.toJSONString(o));
            System.out.print("\t");
        }
        System.out.println();
    }

    public static void main(String args[]) {
//        Integer[] arr = {3, 5, 1, 7, 2, 9, 8, 0, 4, 6};
        Integer[] arr = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        List<Integer> list = Arrays.asList(arr);
        printArr(list);
        //3  5  1  7  2  9  8  0  4  6
        sort(list);
        printArr(list);
        //0  1  2  3  4  5  6  7  8  9
    }
}
