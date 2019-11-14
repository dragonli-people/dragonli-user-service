package org.dragonli.service.modules.userservice.repository;

import org.dragonli.jpatools.repository.IRepository;
import org.dragonli.service.modules.userservice.entity.models.CountryEntity;
import org.dragonli.service.modules.userservice.entity.models.UserEntity;
import org.dragonli.service.modules.userservice.repository.custom.UserRepositoryCustom;

import java.util.Collection;
import java.util.List;

public interface CountryRepository
         extends IRepository<CountryEntity,Long>
        , UserRepositoryCustom
{


}
