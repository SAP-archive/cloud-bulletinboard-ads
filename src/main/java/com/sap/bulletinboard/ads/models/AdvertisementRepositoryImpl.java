//package com.sap.bulletinboard.ads.models;
//
//import java.util.List;
//
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import javax.persistence.TypedQuery;
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.ParameterExpression;
//import javax.persistence.criteria.Root;
//
///**
// * Note: This implementation is optional, see step 4 of exercise 11
// * But it demonstrates the usage of using the CriteriaBuilder and JPQL.
// */
//public class AdvertisementRepositoryImpl implements AdvertisementRepositoryCustom {
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @Override
//    public List<Advertisement> findByTitle(String title) {
////        return findViaJPQL(title);
//        return findViaCriteriaBuilder(title);
//    }
//    
//    /**
//     * Example custom query using typed Queries (JPQL)
//     */
//    private List<Advertisement> findViaJPQL(String title) {
//        String qlString = "SELECT ads FROM Advertisement ads WHERE ads.title = :title";
//        TypedQuery<Advertisement> query = entityManager.createQuery(qlString, Advertisement.class);
//        query.setParameter("title", title);
//        return query.getResultList();
//    }
//    
//    /**
//     * Example custom query using Criteria Builder
//     */
//    private List<Advertisement> findViaCriteriaBuilder(String title) {
//        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//
//        CriteriaQuery<Advertisement> criteriaQuery = criteriaBuilder.createQuery(Advertisement.class);
//        Root<Advertisement> advertisement = criteriaQuery.from(Advertisement.class);
//        ParameterExpression<String> titleParameter = criteriaBuilder.parameter(String.class);
//        criteriaQuery.select(advertisement).where(criteriaBuilder.equal(advertisement.get("title"), titleParameter));
//
//        TypedQuery<Advertisement> query = entityManager.createQuery(criteriaQuery);
//        query.setParameter(titleParameter, title);
//
//        return query.getResultList();
//    }
//}
