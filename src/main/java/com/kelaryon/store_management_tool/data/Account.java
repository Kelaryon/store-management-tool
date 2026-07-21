package com.kelaryon.store_management_tool.data;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name =  "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    private String passwordHash;
    private Date creationDate;
    private boolean activated;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "account_role_assignment",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<AccountRole> roles = new HashSet<>();

}
