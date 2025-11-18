package com.planprostructure.planpro.domain.files;

import com.planprostructure.planpro.domain.folder.Folder;
import com.planprostructure.planpro.domain.users.Users;
import com.planprostructure.planpro.enums.FileType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<FileEntity,Long> {


}
