package com.upex;

import com.upex.constants.TreeNodeRoleConstants;
import com.upex.model.MerkleProof;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class BgMerkleValidatorTest {

    @Test
    public void validateReturnsTrueForValidProof() throws Exception {
        Method validate = BgMerkleValidator.class.getDeclaredMethod("validate", MerkleProof.class);
        validate.setAccessible(true);

        MerkleProof proof = TestDataFactory.createValidProof();
        boolean result = (Boolean) validate.invoke(null, proof);
        assertTrue(result);
    }

    @Test
    public void validateReturnsFalseWhenSelfAndPathSiblingHaveSameRole() throws Exception {
        Method validate = BgMerkleValidator.class.getDeclaredMethod("validate", MerkleProof.class);
        validate.setAccessible(true);

        MerkleProof proof = TestDataFactory.createValidProof();
        proof.getSelf().setRole(TreeNodeRoleConstants.RIGHT_NODE);
        boolean result = (Boolean) validate.invoke(null, proof);
        assertFalse(result);
    }

    @Test
    public void getMerkleJsonFileReadsBundledProofFile() throws Exception {
        Method getFile = BgMerkleValidator.class.getDeclaredMethod("getMerkleJsonFile");
        getFile.setAccessible(true);
        String content = (String) getFile.invoke(null);

        assertNotNull(content);
        assertTrue(content.contains("\"path\""));
        assertTrue(content.contains("\"self\""));
    }
}
