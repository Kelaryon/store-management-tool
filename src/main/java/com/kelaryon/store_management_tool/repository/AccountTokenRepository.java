package com.kelaryon.store_management_tool.repository;

import com.kelaryon.store_management_tool.data.AccountToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface AccountTokenRepository extends JpaRepository<AccountToken, Long> {
    @Query("""
            SELECT accountToken FROM AccountToken accountToken
            WHERE accountToken.accountId = :accountId
            AND accountToken.tokenType = :tokenType
            AND accountToken.expirationDate > NOW()
            AND accountToken.revokedDate is null
            """)
    AccountToken findActiveTokenByAccountIdAndTokenType(@Param("accountId")Long accountId, @Param("tokenType") String tokenType);

    @Query("""
        UPDATE AccountToken at SET at.revokedDate = NOW() WHERE at.accountId =:accountId
        """)
    @Modifying
    @Transactional
    void deactivateUserRefreshTokens(@Param("accountId")Long accountId);
}
