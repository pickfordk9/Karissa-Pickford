package com.upex;

import com.upex.constants.TreeNodeRoleConstants;
import com.upex.model.MerkleProof;
import com.upex.model.TreeNode;
import com.upex.util.MerkelTreeUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public final class TestDataFactory {
    private TestDataFactory() {
    }

    public static MerkleProof createValidProof() {
        TreeNode self = createLeaf("audit", "uid-self", "nonce-self", 3, TreeNodeRoleConstants.LEFT_NODE,
                balances("BTC", "1", "ETH", "2", "USDT", "3"));
        TreeNode path0 = createLeaf("audit", "uid-p0", "nonce-p0", 3, TreeNodeRoleConstants.RIGHT_NODE,
                balances("BTC", "4", "ETH", "5", "USDT", "6"));

        TreeNode level2 = createInternal(self, path0, 2, TreeNodeRoleConstants.RIGHT_NODE);
        TreeNode path1 = createLeaf("audit", "uid-p1", "nonce-p1", 2, TreeNodeRoleConstants.RIGHT_NODE,
                balances("BTC", "7", "ETH", "8", "USDT", "9"));
        TreeNode root = createInternal(level2, path1, 1, TreeNodeRoleConstants.ROOT_NODE);

        MerkleProof proof = new MerkleProof();
        proof.setSelf(self);
        proof.setPath(Arrays.asList(path0, path1, root));
        return proof;
    }

    public static TreeNode createLeaf(String auditId, String uid, String nonce, int level, int role, Map<String, BigDecimal> balances) {
        TreeNode node = new TreeNode();
        node.setAuditId(auditId);
        node.setEncryptUid(uid);
        node.setNonce(nonce);
        node.setLevel(level);
        node.setRole(role);
        node.setBalances(balances);
        node.setMerkelLeaf(MerkelTreeUtils.createMerkelNodeLeaf(node));
        return node;
    }

    public static TreeNode createInternal(TreeNode left, TreeNode right, int level, int role) {
        TreeNode parent = new TreeNode();
        parent.mergeAsset(left);
        parent.mergeAsset(right);
        parent.setLevel(level);
        parent.setRole(role);
        parent.setAuditId("audit");
        parent.setMerkelLeaf(MerkelTreeUtils.createMerkelParentLeaf(left, right, parent));
        return parent;
    }

    public static Map<String, BigDecimal> balances(String... kv) {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            map.put(kv[i], new BigDecimal(kv[i + 1]));
        }
        return map;
    }
}
