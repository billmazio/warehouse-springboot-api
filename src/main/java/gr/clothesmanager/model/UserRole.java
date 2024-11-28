package gr.clothesmanager.model;

import jakarta.persistence.*;
import lombok.*;


import java.util.Set;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "roles")
public class UserRole extends AbstractEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String tag;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    public UserRole(Long id, String name, String tag) {
        this.id = id;
        this.name = name;
        this.tag = tag;
    }


}
