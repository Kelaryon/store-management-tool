package com.kelaryon.store_management_tool.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    @Column(unique = true)
    private String email;
    @NotNull
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
