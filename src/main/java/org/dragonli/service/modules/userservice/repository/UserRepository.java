package org.dragonli.service.modules.userservice.repository;

import org.dragonli.jpatools.repository.IRepository;
import org.dragonli.service.modules.userservice.entity.models.UserEntity;
import org.dragonli.service.modules.userservice.repository.custom.UserRepositoryCustom;

import java.util.Collection;
import java.util.List;

public interface UserRepository
         extends IRepository<UserEntity,Long>
        , UserRepositoryCustom
{
    UserEntity findByPhone(String p);

	UserEntity findByUsername(String username);

	UserEntity findByUsernameOrNickname(String username, String nickname);

	UserEntity findByEmail(String email);

	List<UserEntity> findByIdIn(Collection<Long> idList);

}
