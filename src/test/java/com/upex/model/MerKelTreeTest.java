package com.upex.model;

import com.upex.TestDataFactory;
import com.upex.constants.TreeNodeRoleConstants;
import org.junit.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.*;

public class MerKelTreeTest {

    @Test(expected = IllegalArgumentException.class)
    public void buildMerkelTreeRootRejectsSinglePathNode() {
        MerKelTree tree = new MerKelTree();
        TreeNode self = TestDataFactory.createLeaf("audit", "uid", "nonce", 2, TreeNodeRoleConstants.LEFT_NODE,
                TestDataFactory.balances("BTC", "1"));
        tree.buildMerkelTreeRoot(Arrays.asList(self), self);
    }

    @Test
    public void buildMerkelTreeRootBuildsExpectedRootForValidProof() {
        MerkleProof proof = TestDataFactory.createValidProof();
        TreeNode root = new MerKelTree().buildMerkelTreeRoot(proof.getPath(), proof.getSelf());
        TreeNode expectedRoot = proof.getPath().get(proof.getPath().size() - 1);

        assertEquals(expectedRoot.getMerkelLeaf(), root.getMerkelLeaf());
        assertTrue(root.validateEqualsBalances(expectedRoot));
        assertEquals(expectedRoot.getLevel(), root.getLevel());
    }

    @Test
    public void constructInternalNodePadsWithEmptyNodeWhenRightChildIsMissing() throws Exception {
        MerKelTree tree = new MerKelTree();
        TreeNode left = TestDataFactory.createLeaf("audit", "uid", "nonce", 4, TreeNodeRoleConstants.LEFT_NODE,
                TestDataFactory.balances("BTC", "1", "ETH", "2", "USDT", "3"));

        Method method = MerKelTree.class.getDeclaredMethod("constructInternalNode", TreeNode.class, TreeNode.class);
        method.setAccessible(true);
        TreeNode parent = (TreeNode) method.invoke(tree, left, null);

        Map<String, BigDecimal> balances = parent.getBalances();
        assertEquals(new BigDecimal("1"), balances.get("BTC"));
        assertEquals(new BigDecimal("2"), balances.get("ETH"));
        assertEquals(new BigDecimal("3"), balances.get("USDT"));
        assertEquals(Integer.valueOf(3), parent.getLevel());
        assertNotNull(parent.getMerkelLeaf());
        assertEquals(16, parent.getMerkelLeaf().length());
    }
}
