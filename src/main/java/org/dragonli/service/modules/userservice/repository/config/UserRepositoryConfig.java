package org.dragonli.service.modules.userservice.repository.config;

import org.dragonli.service.modules.userservice.jpaconfig.UserJpaConfig;
import org.dragonli.service.modules.userservice.repository.expand.UserRepositoryExpand;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EntityScan("org.dragonli.service.modules.userservice.entity.models.*.*")
@EnableJpaRepositories(
        entityManagerFactoryRef = UserJpaConfig.ENTITY_MANAGER_FACTORY_USER,
        transactionManagerRef = UserJpaConfig.TRANSACTION_MANAGER_USER,
//        repositoryFactoryBeanClass=AssetRepositoryRepositoryFactoryBean.class,
        repositoryBaseClass= UserRepositoryExpand.class,
        basePackages = {"org.dragonli.service.modules.userservice.repository"})//repository的目录
public class UserRepositoryConfig {
}
