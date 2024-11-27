package gr.clothesmanager.model;
import jakarta.persistence.*;
import lombok.*;


@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "materials")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text")
    private String text;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "size_id", referencedColumnName = "id", nullable = false)
    private Size size;



    public Material(String text, Integer quantity, Size size) {
        this.text = text;
        this.quantity = quantity;
        this.size = size;
    }


}
