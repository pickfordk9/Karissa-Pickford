package com.upex.util;

import com.upex.TestDataFactory;
import com.upex.model.TreeNode;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UtilityCoverageTest {

    @Test
    public void sha256ProducesKnownDigest() {
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
                EncryptionUtils.sha256("abc"));
    }

    @Test
    public void jsonUtilsSerializesBigDecimalWithoutTrailingZeros() {
        Map<String, BigDecimal> balances = new LinkedHashMap<>();
        balances.put("A", new BigDecimal("1.2300"));
        balances.put("B", new BigDecimal("1000.000"));

        String json = JSONUtils.toJSONString(balances);
        assertTrue(json.contains("\"A\":\"1.23\""));
        assertTrue(json.contains("\"B\":\"1000\""));
    }

    @Test
    public void numberUtilsComputesBinaryExponentOrNextPowerIndex() {
        assertEquals(3, NumberUtils.isTimesTwo(8));
        assertEquals(4, NumberUtils.isTimesTwo(9));
        assertEquals(1, NumberUtils.isTimesTwo(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void numberUtilsRejectsNegativeInput() {
        NumberUtils.isTimesTwo(-1);
    }

    @Test
    public void merkelTreeUtilsCreatesDeterministicNodeAndParentHashes() {
        TreeNode left = TestDataFactory.createLeaf("audit", "u1", "n1", 3, 1, TestDataFactory.balances("BTC", "1", "ETH", "2"));
        TreeNode right = TestDataFactory.createLeaf("audit", "u2", "n2", 3, 2, TestDataFactory.balances("BTC", "3", "ETH", "4"));

        String expectedLeaf = EncryptionUtils.sha256("u1,n1," + JSONUtils.toJSONString(left.getBalances())).substring(0, 16);
        assertEquals(expectedLeaf, MerkelTreeUtils.createMerkelNodeLeaf(left));

        TreeNode parent = new TreeNode();
        parent.mergeAsset(left);
        parent.mergeAsset(right);
        parent.setLevel(2);
        String expectedParent = EncryptionUtils.sha256(
                left.getMerkelLeaf() + right.getMerkelLeaf() + "," + JSONUtils.toJSONString(parent.getBalances()) + "," + parent.getLevel()
        ).substring(0, 16);
        assertEquals(expectedParent, MerkelTreeUtils.createMerkelParentLeaf(left, right, parent));
    }
}
