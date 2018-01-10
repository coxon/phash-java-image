package com.coxon.phash.core;

/**
 * @author coxon
 */
public class CompareImageHash {

    private CompareImageHash() {
    }

    public static CompareImageHash getInstance() {
        return new CompareImageHash();
    }

    /**
     * 计算汉明距离
     * 0 , 几乎一样
     * 1-2 , 很像
     * 3-5 , 大部分相同
     * <10 , 近似
     * >10 , 不同
     * @param hash
     * @param hash1
     * @return
     */
    public int compare(String hash, String hash1) {
        int result = 0;
        char[] chars = hash.toCharArray();
        char[] chars1 = hash1.toCharArray();
        for (int i=0; i<hash.length(); i++) {
            if (chars[i] != chars1[i]) {
                result++;
            }
        }
        System.out.println(result);
        return result;
    }
}
