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
@Table(name = "sizes")
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "size", fetch = FetchType.EAGER)
    private Set<Material> materials;

    public Size(String name) {
        this.name = name;
    }
}
