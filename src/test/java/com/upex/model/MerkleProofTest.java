package com.upex.model;

import com.upex.TestDataFactory;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MerkleProofTest {

    @Test
    public void validateReturnsTrueForConsistentPathAndRoot() {
        MerkleProof proof = TestDataFactory.createValidProof();
        assertTrue(proof.validate());
    }

    @Test
    public void validateReturnsFalseWhenRootHashIsTampered() {
        MerkleProof proof = TestDataFactory.createValidProof();
        proof.getPath().get(proof.getPath().size() - 1).setMerkelLeaf("tampered-root");
        assertFalse(proof.validate());
    }
}
