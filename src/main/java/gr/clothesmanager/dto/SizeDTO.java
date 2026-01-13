package gr.clothesmanager.dto;

import gr.clothesmanager.model.Material;
import gr.clothesmanager.model.Size;
import lombok.*;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class SizeDTO {
    private Long id;
    private String name;
    private Set<Long> materialIds;

    public Size toModel() {return new Size(id, name,null);}

    public static SizeDTO fromModel(Size size) {
        if (size == null) return null;
        return SizeDTO.builder()
                .id(size.getId())
                .name(size.getName())
                .materialIds(size.getMaterials() != null ? size.getMaterials().stream().map(Material::getId).collect(Collectors.toSet()) : null)
                .build();
    }

    @Override
    public String toString() {
        return "SizeDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", materialIds=" + materialIds +
                '}';
    }
}
