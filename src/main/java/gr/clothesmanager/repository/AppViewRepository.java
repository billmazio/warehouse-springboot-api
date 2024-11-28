package gr.clothesmanager.repository;

import gr.clothesmanager.model.AppView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface AppViewRepository extends JpaRepository<AppView, Long> {
    @Query("SELECT v FROM AppView v")
    List<AppView> findAllViewsRolesPermissions();

    //@Query("SELECT distinct v FROM AppView v JOIN FETCH v.rolePermissions rp JOIN FETCH rp.role WHERE v.name =:viewName ")
    @Query("SELECT distinct v FROM AppView v  WHERE v.name =:viewName ")
    Optional<AppView> findViewsRolesPermissionsPerView(String viewName);
}
