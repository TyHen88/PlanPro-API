package com.planprostructure.planpro.domain.folder;

import com.planprostructure.planpro.domain.users.Users;
import com.planprostructure.planpro.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    Optional<Folder> findByName(String name);

    @Query("SELECT f FROM Folder f WHERE f.id = ?1 AND f.userId = ?2")
    Optional<Folder> findByIdAndUser(Long id, Long userId);

    @Query("SELECT f FROM Folder f WHERE f.id = ?1 AND f.status <> '9'")
    List<Folder> findAllByFolderIdAndStatus(Long folderId, Status status);


}
