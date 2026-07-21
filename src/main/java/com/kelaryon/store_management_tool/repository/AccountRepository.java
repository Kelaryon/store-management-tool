package com.kelaryon.store_management_tool.repository;

import com.kelaryon.store_management_tool.data.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {

    @Query("""
        SELECT DISTINCT acc
        FROM Account acc
        LEFT JOIN FETCH acc.roles
        WHERE acc.email = :email
    """)
    Optional<Account> findAccountByEmailWithRoles(@Param("email") String email);
    boolean existsByEmail(String email);


}
