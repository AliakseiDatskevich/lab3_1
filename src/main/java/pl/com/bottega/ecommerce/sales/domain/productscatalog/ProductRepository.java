package pl.com.bottega.ecommerce.sales.domain.productscatalog;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;

import java.util.List;

public interface ProductRepository {

    public Product load(Id productId);

    public List<Product> findProductWhereBestBeforeExpiredIn(int days);
}
