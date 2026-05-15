package com.upex.model;

import com.upex.TestDataFactory;
import com.upex.constants.TreeNodeRoleConstants;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TreeNodeTest {

    @Test
    public void validateSelfReturnsTrueForCorrectLeaf() {
        TreeNode self = TestDataFactory.createLeaf("audit", "uid", "nonce", 2, TreeNodeRoleConstants.LEFT_NODE,
                TestDataFactory.balances("BTC", "1", "ETH", "2"));
        assertTrue(self.validateSelf());
    }

    @Test
    public void validatePathRejectsInvalidMetadata() {
        TreeNode node = new TreeNode();
        node.setBalances(TestDataFactory.balances("BTC", "1"));
        node.setLevel(0);
        node.setRole(4);
        node.setMerkelLeaf("");
        assertFalse(node.validatePath());
    }

    @Test
    public void validateBalancesRejectsNullAmount() {
        TreeNode node = new TreeNode();
        Map<String, BigDecimal> balances = new HashMap<>();
        balances.put("BTC", null);
        node.setBalances(balances);
        assertFalse(node.validateBalances());
    }

    @Test
    public void mergeAssetSumsCoinAmountsAcrossChildren() {
        TreeNode root = new TreeNode();
        TreeNode child1 = new TreeNode();
        child1.setBalances(TestDataFactory.balances("BTC", "1.2", "ETH", "0.8"));
        TreeNode child2 = new TreeNode();
        child2.setBalances(TestDataFactory.balances("BTC", "2.3", "USDT", "5"));

        root.mergeAsset(child1);
        root.mergeAsset(child2);

        assertEquals(new BigDecimal("3.5"), root.getBalances().get("BTC"));
        assertEquals(new BigDecimal("0.8"), root.getBalances().get("ETH"));
        assertEquals(new BigDecimal("5"), root.getBalances().get("USDT"));
    }

    @Test
    public void validateEqualsBalancesDetectsDifference() {
        TreeNode nodeA = new TreeNode();
        nodeA.setBalances(TestDataFactory.balances("BTC", "10", "ETH", "2"));
        TreeNode nodeB = new TreeNode();
        nodeB.setBalances(TestDataFactory.balances("BTC", "10", "ETH", "3"));

        assertFalse(nodeA.validateEqualsBalances(nodeB));
    }
}
