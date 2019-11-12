package org.dragonli.service.modules.userservice.repository.impl;

import org.dragonli.service.modules.userservice.repository.UserRepository;
import org.dragonli.service.modules.userservice.repository.custom.UserRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;

//import javax.persistence.criteria.Join;
//import javax.persistence.criteria.Predicate;
//import javax.transaction.Transactional;

@SuppressWarnings("SpringJavaConstructorAutowiringInspection")
public class UserRepositoryImpl
//        extends GeneralRepositoryExpand<UserEntity,Long> 此试验不成立，它只会创建0参数的构造函数.
//        然则如何拿到domainClass, EntityManager em？如何拿到基类的一些方法？
        implements UserRepositoryCustom {

//    参见这篇文章，但不能认同他最后的搞法
//    https://blog.csdn.net/xiao_xuwen/article/details/53579353

    @SuppressWarnings("unused")
    @Autowired
    private UserRepository userRepository;

    // a expend method example
//    @Override
//    public String testFindCustom() {
//        return "findCustom";
//    }



    //stand join example and sum example
//    public BigDecimal sumWithdrawBtcValueTodayByUserId(Integer userId) {
//        return walletTransactionRepository.findOne((root, criteriaQuery, criteriaBuilder) -> {
//            Join<WalletTransactionEntity, WalletEntity> walletTransactionEntityWalletEntityJoin = root.join(
//                    WalletTransactionEntity_.walletEntity);
//            Join<WalletEntity, AssetEntity> walletEntityAssetEntityJoin = walletTransactionEntityWalletEntityJoin.join(
//                    WalletEntity_.assetEntity);
//            criteriaQuery.select(criteriaBuilder.sum(criteriaBuilder.prod(root.get(WalletTransactionEntity_.amount),
//                    walletEntityAssetEntityJoin.get(AssetEntity_.btcPrice))));
//            criteriaQuery.where(criteriaBuilder.and(
//                    criteriaBuilder.equal(walletTransactionEntityWalletEntityJoin.get(WalletEntity_.userId), userId),
//                    criteriaBuilder.equal(root.get(WalletTransactionEntity_.type),
//                            WalletTransactionTypeEnum.WITHDRAWAL),
//                    criteriaBuilder.greaterThanOrEqualTo(root.get(WalletTransactionEntity_.createdAt),
//                            DateTime.todayStart())));
//        }, BigDecimal.class);
//    }

    //other example
//    @Override
//    @Transactional
//    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
//    public WalletEntity getByUserIdAndAssetPFI(Integer userId, AssetCodeEnum assetCodeEnum) {
//        return getByUserIdAndAsset(userId, assetCodeEnum);
//    }


}
