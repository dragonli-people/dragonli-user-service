package org.dragonli.service.modules.userservice.repository.expand;

import org.dragonli.jpatools.repository.IRepositoryExpand;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface IUserRepositoryExpand<T, ID extends Serializable> extends IRepositoryExpand<T, ID> {
}
