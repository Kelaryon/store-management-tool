package com.kelaryon.store_management_tool.repository;

import com.kelaryon.store_management_tool.data.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRole, Long> {
    AccountRole findByName(String name);
}
