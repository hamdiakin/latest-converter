package enums;

import java.util.*;

class Solution {

    /*
     * public List<List<String>> groupAnagrams(String[] strs) {
     * List<List<String>> res = new ArrayList<>();
     * if (strs.length == 0)
     * return res;
     * Map<String, List<String>> map = new HashMap<>();
     * 
     * for (String s : strs) {
     * char[] arr = new char[26];
     * for (char c : s.toCharArray()) {
     * arr[c - 'a']++;
     * }
     * String key = String.valueOf(arr);
     * map.computeIfAbsent(key, k -> new ArrayList<>());
     * map.get(key).add(s);
     * }
     * res.addAll(map.values());
     * return res;
     * }
     */

    public int[] topKFrequent1(int[] nums, int k) {
        // Hashmap to keep count of each number
        HashMap<Integer, Integer> map = new HashMap<>();

        for (int num : nums) {
            map.put(num, map.getOrDefault(num, 0) + 1);
        }

        // PriorityQueue to store top k frequent numbers
        PriorityQueue<Map.Entry<Integer, Integer>> pq = new PriorityQueue<>(
                (a, b) -> b.getValue() - a.getValue());
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            pq.add(entry);
        }

        // print the first k numbers in pq
        int[] res = new int[k];
        for (int i = 0; i < k; i++) {
            res[i] = pq.poll().getKey();
        }
        return res;
    }

    public int[] topKFrequent(int[] numbers, int k) {
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int number : numbers)
            map.put(
                    number,
                    map.getOrDefault(number, 0) + 1);

        int size = map.size();
        int[] keys = new int[size];
        int i = 0;
        for (int key : map.keySet())
            keys[i++] = key;

        select(keys, map, 0, size - 1, size - k);
        return Arrays.copyOfRange(keys, size - k, size);
    }

    // Modified implementation of Hoare's selection algorithm:

    private void select(
            int[] keys,
            Map<Integer, Integer> map,
            int left,
            int right,
            int kSmallest) {
        while (left != right) {
            int pivot = partition(keys, map, left, right, (left + right) / 2);

            if (kSmallest == pivot)
                return;

            if (kSmallest < pivot)
                right = pivot - 1;
            else
                left = pivot + 1;
        }
    }

    private int partition(
            int[] keys,
            Map<Integer, Integer> map,
            int left,
            int right,
            int pivot) {
        int pivotValue = map.get(keys[pivot]);
        swap(keys, pivot, right);
        int index = left;

        for (int i = left; i <= right; i++)
            if (map.get(keys[i]) < pivotValue) {
                swap(keys, i, index);
                index++;
            }
        swap(keys, right, index);
        return index;
    }

    private void swap(int[] array, int i1, int i2) {
        int temp = array[i1];
        array[i1] = array[i2];
        array[i2] = temp;
    }
}