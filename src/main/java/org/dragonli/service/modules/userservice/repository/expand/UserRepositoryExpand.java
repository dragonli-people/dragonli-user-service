package org.dragonli.service.modules.userservice.repository.expand;

import org.dragonli.jpatools.repository.AbstractRepositoryExpand;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;

import javax.persistence.EntityManager;
import java.io.Serializable;

@SuppressWarnings("SpringJavaConstructorAutowiringInspection")
public class UserRepositoryExpand<T, ID extends Serializable> extends AbstractRepositoryExpand<T, ID> {

    public UserRepositoryExpand(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
    }

    public UserRepositoryExpand(JpaEntityInformation<T, ?> entityInformation, EntityManager em) {
        super(entityInformation, em);
    }

    public String testFindExpandAssets(){return "findExpandAssets";}
//    public GeneralRepositoryExpand(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
//        super(entityInformation, entityManager);
//    }
}
